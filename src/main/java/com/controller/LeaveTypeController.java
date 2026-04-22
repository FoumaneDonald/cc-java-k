package com.controller;

import com.dto.request.CreateLeaveTypeDTO;
import com.dto.response.ApiResponse;
import com.dto.response.LeaveTypeResponseDTO;
import com.mapper.LeaveRequestMapper;
import com.service.LeaveTypeService;
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
@RequestMapping("/api/v1/leave-types")
@RequiredArgsConstructor
@Tag(name = "Types de Congé", description = "Catalogue des types de congé et leurs règles (RG-M2-11)")
public class LeaveTypeController {

	@Autowired private LeaveTypeService leaveTypeService;
	@Autowired private LeaveRequestMapper mapper;

    @Operation(
        summary = "Créer un type de congé",
        description = """
            Crée un nouveau type de congé.
            
            - `RG-M2-11` : le code doit être **unique** dans le système
            - Si `conventionId` est fourni, le type est rattaché à cette convention collective
            - Le code est automatiquement mis en majuscules
            """
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Type créé"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Code déjà utilisé",
            content = @Content(examples = @ExampleObject(value = """
                {"success":false,"error":{"code":"DUPLICATE_CODE","message":"A leave type with code 'CP' already exists"}}""")))
    })
    @PostMapping
    public ResponseEntity<ApiResponse<LeaveTypeResponseDTO>> create(@Valid @RequestBody CreateLeaveTypeDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(mapper.toTypeResponse(leaveTypeService.create(dto))));
    }

    @Operation(summary = "Mettre à jour un type de congé",
        description = "Met à jour un type existant. Le code peut être modifié tant qu'il reste unique.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Type mis à jour"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Type introuvable"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Code déjà utilisé par un autre type")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LeaveTypeResponseDTO>> update(
            @Parameter(description = "UUID du type de congé", required = true) @PathVariable UUID id,
            @Valid @RequestBody CreateLeaveTypeDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok(mapper.toTypeResponse(leaveTypeService.update(id, dto))));
    }

    @Operation(summary = "Récupérer un type de congé par ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Type trouvé"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Type introuvable")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LeaveTypeResponseDTO>> findById(
            @Parameter(description = "UUID du type de congé", required = true) @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(mapper.toTypeResponse(leaveTypeService.findById(id))));
    }

    @Operation(summary = "Lister tous les types de congé",
        description = "Retourne le catalogue complet. Utilisé par le frontend pour alimenter les listes déroulantes.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<LeaveTypeResponseDTO>>> findAll() {
        List<LeaveTypeResponseDTO> list = leaveTypeService.findAll()
                .stream().map(mapper::toTypeResponse).toList();
        return ResponseEntity.ok(ApiResponse.ok(list, new ApiResponse.MetaDetail(1, list.size())));
    }

    @Operation(summary = "Supprimer un type de congé",
        description = "Supprime un type de congé. **Attention** : bloqué si des demandes actives référencent ce type.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Type supprimé"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Type introuvable")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "UUID du type de congé", required = true) @PathVariable UUID id) {
        leaveTypeService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
