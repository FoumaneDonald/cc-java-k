package com.controller;

import com.dto.request.CreateConventionDTO;
import com.dto.response.ApiResponse;
import com.dto.response.ConventionResponseDTO;
import com.mapper.LeaveRequestMapper;
import com.service.ConventionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/v1/conventions")
@RequiredArgsConstructor
@Tag(name = "Conventions", description = "Conventions collectives — surchargent les règles par défaut des types de congé (RG-M2-12)")
public class ConventionController {

	@Autowired private ConventionService conventionService;
	@Autowired private LeaveRequestMapper mapper;

    @Operation(summary = "Créer une convention collective",
        description = """
            Crée une convention collective. Les champs `overrideAnnualDays` et `minNoticeDays`
            permettent de surcharger les valeurs par défaut des types de congé qui y sont rattachés (`RG-M2-12`).
            """)
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Convention créée"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<ConventionResponseDTO>> create(@Valid @RequestBody CreateConventionDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(mapper.toConventionResponse(conventionService.create(dto))));
    }

    @Operation(summary = "Mettre à jour une convention")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Convention mise à jour"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Convention introuvable")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ConventionResponseDTO>> update(
            @Parameter(description = "UUID de la convention", required = true) @PathVariable UUID id,
            @Valid @RequestBody CreateConventionDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok(mapper.toConventionResponse(conventionService.update(id, dto))));
    }

    @Operation(summary = "Récupérer une convention par ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Convention trouvée"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Convention introuvable")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ConventionResponseDTO>> findById(
            @Parameter(description = "UUID de la convention", required = true) @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(mapper.toConventionResponse(conventionService.findById(id))));
    }

    @Operation(summary = "Lister toutes les conventions")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ConventionResponseDTO>>> findAll() {
        List<ConventionResponseDTO> list = conventionService.findAll()
                .stream().map(mapper::toConventionResponse).toList();
        return ResponseEntity.ok(ApiResponse.ok(list, new ApiResponse.MetaDetail(1, list.size())));
    }

    @Operation(summary = "Supprimer une convention",
        description = "**Attention** : bloqué si des types de congé actifs référencent cette convention.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Convention supprimée"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Convention introuvable")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "UUID de la convention", required = true) @PathVariable UUID id) {
        conventionService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
