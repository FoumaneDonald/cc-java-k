package com.controller;

import com.dto.request.LeaveBalanceAdjustDTO;
import com.dto.response.ApiResponse;
import com.dto.response.LeaveBalanceResponseDTO;
import com.mapper.LeaveRequestMapper;
import com.service.LeaveBalanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/v1/leave-balances")
@RequiredArgsConstructor
@Tag(name = "Soldes de Congé", description = "Consultation et gestion des soldes par employé (RG-M2-01, RG-M2-15)")
public class LeaveBalanceController {

	@Autowired private LeaveBalanceService leaveBalanceService;
	@Autowired private LeaveRequestMapper mapper;

    @Operation(
        summary = "Consulter les soldes d'un employé",
        description = """
            Retourne les soldes de congé d'un employé pour une année donnée (ou toutes les années).
            
            Utilisé par :
            - L'employé pour vérifier son solde disponible avant de soumettre
            - Le frontend pour afficher la barre de progression des soldes
            - `RG-M2-01` : le service de soumission utilise ces données pour bloquer si insuffisant
            
            **Paramètres :**
            - `employeeId` (obligatoire) : UUID de l'employé venant de M1
            - `year` (optionnel) : si absent, retourne toutes les années
            """
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Soldes retournés")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<LeaveBalanceResponseDTO>>> getBalances(
            @Parameter(description = "UUID de l'employé (M1)", required = true) @RequestParam UUID employeeId,
            @Parameter(description = "Année (ex: 2025). Si absent, toutes les années.", example = "2025")
            @RequestParam(required = false) Integer year) {
        List<LeaveBalanceResponseDTO> list = year != null
                ? leaveBalanceService.getBalancesForEmployeeAndYear(employeeId, year).stream().map(mapper::toBalanceResponse).toList()
                : leaveBalanceService.getBalancesForEmployee(employeeId).stream().map(mapper::toBalanceResponse).toList();
        return ResponseEntity.ok(ApiResponse.ok(list, new ApiResponse.MetaDetail(1, list.size())));
    }

    @Operation(
        summary = "Ajuster le solde d'un employé (RH Manager)",
        description = """
            Crée ou met à jour le solde d'un employé pour un type de congé et une année.
            
            Utilisé par le RH Manager pour :
            - Initialiser les soldes en début d'année
            - Corriger manuellement un solde après une erreur
            - Attribuer des jours supplémentaires exceptionnels
            """
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Solde ajusté"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Type de congé introuvable")
    })
    @PostMapping("/adjust")
    public ResponseEntity<ApiResponse<LeaveBalanceResponseDTO>> adjust(@Valid @RequestBody LeaveBalanceAdjustDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok(mapper.toBalanceResponse(
                leaveBalanceService.createOrUpdateBalance(
                        dto.getEmployeeId(), dto.getLeaveTypeId(), dto.getYear(), dto.getTotalDays()))));
    }

    @Operation(
        summary = "Initialiser le solde prorata — nouvel employé",
        description = """
            Calcule et crée le solde initial d'un nouvel employé au **prorata de sa date d'embauche**.
            
            `RG-M2-15` : Pour les nouveaux employés, le solde est attribué au prorata du mois d'embauche.
            
            **Formule :** `(jours_annuels × mois_restants) / 12`
            
            Exemple : embauché le 01/07, solde CP = 30j → 30 × 6 / 12 = **15j**
            """
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Solde prorata créé"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Type de congé introuvable")
    })
    @PostMapping("/initialize")
    public ResponseEntity<ApiResponse<LeaveBalanceResponseDTO>> initialize(
            @Parameter(description = "UUID de l'employé (M1)", required = true) @RequestParam UUID employeeId,
            @Parameter(description = "UUID du type de congé", required = true) @RequestParam UUID leaveTypeId,
            @Parameter(description = "Date d'embauche (yyyy-MM-dd)", required = true, example = "2025-07-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hireDate) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(mapper.toBalanceResponse(
                leaveBalanceService.initializeBalance(employeeId, leaveTypeId, hireDate))));
    }
}
