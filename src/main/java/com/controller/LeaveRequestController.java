package com.controller;

import com.dto.request.CreateLeaveRequestDTO;
import com.dto.request.RejectLeaveRequestDTO;
import com.dto.response.AbsenceSummaryDTO;
import com.dto.response.ApiResponse;
import com.dto.response.LeaveRequestResponseDTO;
import com.entities.LeaveRequest;
import com.mapper.LeaveRequestMapper;
import com.service.LeaveRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/leave-requests")
@RequiredArgsConstructor
@Tag(name = "Demandes de Congé", description = "Cycle de vie complet d'une demande de congé")
public class LeaveRequestController {

	@Autowired private LeaveRequestService leaveRequestService;
	@Autowired private LeaveRequestMapper mapper;

    @Operation(
        summary = "Créer une demande de congé (DRAFT)",
        description = """
            Crée une nouvelle demande au statut **DRAFT**. Aucun solde n'est débité à cette étape.
            
            **UC-M2-01 étapes 1–4.** La demande doit ensuite être soumise via `/submit`.
            
            Règles vérifiées :
            - `RG-M2-02` : dates obligatoires, endDate ≥ startDate
            - `RG-M2-03` : jours ouvrables calculés automatiquement
            """
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Demande créée en DRAFT"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Données invalides (dates, champs manquants)",
            content = @Content(examples = @ExampleObject(value = """
                {"success":false,"error":{"code":"VALIDATION_ERROR","message":"endDate must be >= startDate"}}"""))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Type de congé introuvable",
            content = @Content(examples = @ExampleObject(value = """
                {"success":false,"error":{"code":"NOT_FOUND","message":"LeaveType not found: ..."}}""")))
    })
    @PostMapping
    public ResponseEntity<ApiResponse<LeaveRequestResponseDTO>> create(
            @Valid @RequestBody CreateLeaveRequestDTO dto) {
        LeaveRequest created = leaveRequestService.create(
                dto.getEmployeeId(), dto.getLeaveTypeId(),
                dto.getStartDate(), dto.getEndDate(), dto.getJustificationPath());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(mapper.toResponse(created)));
    }

    @Operation(
        summary = "Soumettre une demande (DRAFT → SUBMITTED)",
        description = """
            Passe la demande de **DRAFT** à **SUBMITTED** et notifie le Chef de Service.
            
            **UC-M2-01 étape 5.**
            
            Règles vérifiées à la soumission :
            - `RG-M2-05` : seul un DRAFT peut être soumis
            - `RG-M2-14` : délai minimum de prévenance respecté
            - `RG-M2-01` : solde suffisant pour le type demandé
            - `RG-M2-04` : aucun chevauchement avec une demande active
            """
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Demande soumise — statut SUBMITTED"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Transition de statut invalide (déjà soumise, validée, etc.)",
            content = @Content(examples = @ExampleObject(value = """
                {"success":false,"error":{"code":"INVALID_STATUS_TRANSITION","message":"Cannot transition leave request from SUBMITTED to SUBMITTED"}}"""))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Solde insuffisant ou chevauchement détecté",
            content = @Content(examples = @ExampleObject(value = """
                {"success":false,"error":{"code":"INSUFFICIENT_BALANCE","message":"Requested: 10 day(s), Available: 5.0"}}""")))
    })
    @PutMapping("/{id}/submit")
    public ResponseEntity<ApiResponse<LeaveRequestResponseDTO>> submit(
            @Parameter(description = "UUID de la demande", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(mapper.toResponse(leaveRequestService.submit(id))));
    }

    @Operation(
        summary = "Valider une demande — Chef de Service (SUBMITTED → VALIDATED)",
        description = """
            Premier niveau d'approbation. Seul le Chef de Service responsable de l'équipe peut valider.
            
            **UC-M2-02.** La demande passe ensuite à la confirmation RH.
            
            - `RG-M2-07` : workflow à deux niveaux obligatoire
            - Une alerte est visible si d'autres membres de l'équipe sont absents les mêmes jours (non bloquant)
            """
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Demande validée — statut VALIDATED"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "La demande n'est pas au statut SUBMITTED")
    })
    @PutMapping("/{id}/validate")
    public ResponseEntity<ApiResponse<LeaveRequestResponseDTO>> validate(
            @Parameter(description = "UUID de la demande", required = true) @PathVariable UUID id,
            @Parameter(description = "UUID du Chef de Service qui valide", required = true) @RequestParam UUID chefId) {
        return ResponseEntity.ok(ApiResponse.ok(mapper.toResponse(leaveRequestService.validate(id, chefId))));
    }

    @Operation(
        summary = "Confirmer une demande — RH Manager (VALIDATED → CONFIRMED)",
        description = """
            Confirmation finale par le RH Manager. **Débit automatique du solde** à cette étape.
            
            **UC-M2-03.**
            
            - `RG-M2-09` : le solde est débité uniquement ici — pas avant
            - La demande devient immuable après confirmation
            - Une notification est envoyée à l'employé
            """
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Demande confirmée — statut CONFIRMED, solde débité"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "La demande n'est pas au statut VALIDATED")
    })
    @PutMapping("/{id}/confirm")
    public ResponseEntity<ApiResponse<LeaveRequestResponseDTO>> confirm(
            @Parameter(description = "UUID de la demande", required = true) @PathVariable UUID id,
            @Parameter(description = "UUID du RH Manager qui confirme", required = true) @RequestParam UUID rhId) {
        return ResponseEntity.ok(ApiResponse.ok(mapper.toResponse(leaveRequestService.confirm(id, rhId))));
    }

    @Operation(
        summary = "Rejeter une demande (Chef ou RH)",
        description = """
            Rejette une demande au statut **SUBMITTED** (par le Chef) ou **VALIDATED** (par le RH).
            
            - `RG-M2-08` : motif de rejet **obligatoire** — la requête est bloquée sans motif
            - Le solde n'est **pas** débité en cas de rejet
            - L'employé est notifié avec le motif
            """
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Demande rejetée — statut REJECTED"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Motif de rejet absent",
            content = @Content(examples = @ExampleObject(value = """
                {"success":false,"error":{"code":"REJECTION_REASON_REQUIRED","message":"A rejection reason is mandatory"}}"""))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "La demande n'est ni SUBMITTED ni VALIDATED")
    })
    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<LeaveRequestResponseDTO>> reject(
            @Parameter(description = "UUID de la demande", required = true) @PathVariable UUID id,
            @Valid @RequestBody RejectLeaveRequestDTO dto,
            @Parameter(description = "UUID de l'approbateur qui rejette", required = true) @RequestParam UUID rejectorId) {
        return ResponseEntity.ok(ApiResponse.ok(
                mapper.toResponse(leaveRequestService.reject(id, rejectorId, dto.getRejectionReason()))));
    }

    @Operation(
        summary = "Annuler une demande",
        description = """
            Annule une demande. Comportement selon le statut actuel :
            
            | Statut actuel | Qui peut annuler | Effet sur le solde |
            |---|---|---|
            | DRAFT | Employé | Aucun |
            | SUBMITTED | Employé (`RG-M2-06`) | Aucun |
            | VALIDATED | RH Manager | Aucun |
            | CONFIRMED | RH Manager | **Solde recrédité** (`RG-M2-09`) |
            
            Une demande **REJECTED** ne peut pas être annulée.
            """
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Demande annulée"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Annulation impossible sur ce statut",
            content = @Content(examples = @ExampleObject(value = """
                {"success":false,"error":{"code":"CANNOT_CANCEL","message":"A REJECTED request cannot be cancelled"}}""")))
    })
    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<LeaveRequestResponseDTO>> cancel(
            @Parameter(description = "UUID de la demande", required = true) @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(mapper.toResponse(leaveRequestService.cancel(id))));
    }

    @Operation(
        summary = "Modifier une demande (DRAFT uniquement)",
        description = """
            Met à jour les dates et la pièce jointe d'une demande.
            
            `RG-M2-05` : seules les demandes au statut **DRAFT** peuvent être modifiées.
            Après soumission, la modification est interdite.
            """
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Demande mise à jour"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "La demande n'est pas en DRAFT",
            content = @Content(examples = @ExampleObject(value = """
                {"success":false,"error":{"code":"NOT_EDITABLE","message":"Only DRAFT requests can be modified (RG-M2-05)"}}""")))
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LeaveRequestResponseDTO>> update(
            @Parameter(description = "UUID de la demande", required = true) @PathVariable UUID id,
            @Valid @RequestBody CreateLeaveRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok(mapper.toResponse(
                leaveRequestService.update(id, dto.getStartDate(), dto.getEndDate(), dto.getJustificationPath()))));
    }

    @Operation(summary = "Récupérer une demande par ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Demande trouvée"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Demande introuvable")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LeaveRequestResponseDTO>> findById(
            @Parameter(description = "UUID de la demande", required = true) @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(mapper.toResponse(leaveRequestService.findById(id))));
    }

    @Operation(
        summary = "Lister les demandes d'un employé",
        description = "Retourne toutes les demandes d'un employé, triées par date de création décroissante."
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<LeaveRequestResponseDTO>>> findByEmployee(
            @Parameter(description = "UUID de l'employé (provient de M1)", required = true)
            @RequestParam UUID employeeId) {
        List<LeaveRequestResponseDTO> list = leaveRequestService.findByEmployee(employeeId)
                .stream().map(mapper::toResponse).toList();
        return ResponseEntity.ok(ApiResponse.ok(list, new ApiResponse.MetaDetail(1, list.size())));
    }

    @Operation(
        summary = "Demandes en attente de validation — dashboard Chef de Service",
        description = "Retourne toutes les demandes au statut **SUBMITTED**, triées par date de création croissante (FIFO)."
    )
    @GetMapping("/pending-validation")
    public ResponseEntity<ApiResponse<List<LeaveRequestResponseDTO>>> pendingValidation() {
        List<LeaveRequestResponseDTO> list = leaveRequestService.findPendingValidation()
                .stream().map(mapper::toResponse).toList();
        return ResponseEntity.ok(ApiResponse.ok(list, new ApiResponse.MetaDetail(1, list.size())));
    }

    @Operation(
        summary = "Demandes en attente de confirmation — dashboard RH Manager",
        description = "Retourne toutes les demandes au statut **VALIDATED** (validées par le Chef), en attente de confirmation finale."
    )
    @GetMapping("/pending-confirmation")
    public ResponseEntity<ApiResponse<List<LeaveRequestResponseDTO>>> pendingConfirmation() {
        List<LeaveRequestResponseDTO> list = leaveRequestService.findPendingConfirmation()
                .stream().map(mapper::toResponse).toList();
        return ResponseEntity.ok(ApiResponse.ok(list, new ApiResponse.MetaDetail(1, list.size())));
    }

    @Operation(
        summary = "Absences confirmées sur une période — endpoint M3 Paie",
        description = """
            **Endpoint d'intégration inter-modules** consommé exclusivement par **M3 (Gestion de Paie)**.
            
            Retourne les congés confirmés d'un employé qui chevauchent la période demandée.
            Utilisé par M3 pour calculer les retenues sur absence non justifiée (`RG-M3-05`).
            
            Format de date : `yyyy-MM-dd` (ISO 8601)
            """,
        tags = {"Demandes de Congé", "Intégration M3"}
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des absences confirmées sur la période")
    })
    @GetMapping("/absences")
    public ResponseEntity<ApiResponse<List<AbsenceSummaryDTO>>> absencesInPeriod(
            @Parameter(description = "UUID de l'employé", required = true) @RequestParam UUID employeeId,
            @Parameter(description = "Début de la période (yyyy-MM-dd)", required = true, example = "2025-07-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodStart,
            @Parameter(description = "Fin de la période (yyyy-MM-dd)", required = true, example = "2025-07-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodEnd) {
        List<AbsenceSummaryDTO> list = leaveRequestService
                .findAbsencesInPeriod(employeeId, periodStart, periodEnd)
                .stream().map(mapper::toAbsenceSummary).toList();
        return ResponseEntity.ok(ApiResponse.ok(list, new ApiResponse.MetaDetail(1, list.size())));
    }
}
