package com.controller;

import com.dto.request.LeaveParameterDTO;
import com.dto.response.ApiResponse;
import com.dto.response.LeaveParameterResponseDTO;
import com.mapper.LeaveRequestMapper;
import com.service.LeaveParameterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/leave-parameters")
@RequiredArgsConstructor
@Tag(name = "Paramètres Congé", description = "Configuration système : jours fériés, mode de calcul, délai de prévenance (RG-M2-14)")
public class LeaveParameterController {

	@Autowired private LeaveParameterService leaveParameterService;
	@Autowired private LeaveRequestMapper mapper;

    @Operation(
        summary = "Créer une nouvelle configuration active",
        description = """
            Crée une nouvelle configuration et **désactive automatiquement** la précédente.
            Il ne peut exister qu'une seule configuration active à la fois.
            
            `RG-M2-14` : définit les jours fériés, le mode de calcul (ouvrables vs calendaires)
            et le délai minimum de prévenance.
            
            Exemple de body :
            ```json
            {
              "calculationMode": "WORKING_DAYS",
              "minNoticeDays": 2,
              "publicHolidays": ["2025-01-01", "2025-05-01", "2025-12-25"]
            }
            ```
            """
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Configuration créée et activée"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<LeaveParameterResponseDTO>> create(@Valid @RequestBody LeaveParameterDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(mapper.toParameterResponse(leaveParameterService.create(dto))));
    }

    @Operation(summary = "Mettre à jour la configuration active",
        description = "Modifie la configuration active. Seule la config active peut être modifiée directement. Pour changer de configuration, créez-en une nouvelle via POST.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Configuration mise à jour"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Configuration inactive — créez-en une nouvelle",
            content = @Content(examples = @ExampleObject(value = """
                {"success":false,"error":{"code":"INACTIVE_PARAMETER","message":"Cannot update an inactive parameter configuration."}}""")))
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LeaveParameterResponseDTO>> update(
            @Parameter(description = "UUID de la configuration", required = true) @PathVariable UUID id,
            @Valid @RequestBody LeaveParameterDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok(mapper.toParameterResponse(leaveParameterService.update(id, dto))));
    }

    @Operation(summary = "Récupérer la configuration active",
        description = "Retourne la configuration actuellement utilisée par le calculateur de jours ouvrables. Endpoint le plus utilisé en lecture.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Configuration active trouvée"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Aucune configuration active — créez-en une via POST")
    })
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<LeaveParameterResponseDTO>> getActive() {
        return ResponseEntity.ok(ApiResponse.ok(mapper.toParameterResponse(leaveParameterService.findActive())));
    }

    @Operation(summary = "Récupérer une configuration par ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Configuration trouvée"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Configuration introuvable")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LeaveParameterResponseDTO>> findById(
            @Parameter(description = "UUID de la configuration", required = true) @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(mapper.toParameterResponse(leaveParameterService.findById(id))));
    }

    @Operation(summary = "Historique de toutes les configurations",
        description = "Retourne toutes les configurations (actives et inactives). Utile pour l'audit.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<LeaveParameterResponseDTO>>> findAll() {
        List<LeaveParameterResponseDTO> list = leaveParameterService.findAll()
                .stream().map(mapper::toParameterResponse).toList();
        return ResponseEntity.ok(ApiResponse.ok(list, new ApiResponse.MetaDetail(1, list.size())));
    }
}
