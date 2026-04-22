package com.kjava.controllers;

import com.kjava.dto.ApiResponse;
import com.kjava.dto.ApiError;
import com.kjava.entities.StructureSalariale;
import com.kjava.services.StructureSalarialeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/paie/structures")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Structures Salariales", description = "Gestion des structures salariales du Module M3")
public class M3StructureSalarialeController {

    private final StructureSalarialeService structureSalarialeService;

    @GetMapping
    @Operation(summary = "Lister toutes les structures salariales", description = "Retourne la liste de toutes les structures salariales")
    public ResponseEntity<ApiResponse<List<StructureSalariale>>> getAllStructures() {
        log.info("GET /api/paie/structures - Récupération de toutes les structures");
        
        try {
            List<StructureSalariale> structures = structureSalarialeService.getAllStructuresSalariales();
            ApiResponse<List<StructureSalariale>> response = ApiResponse.success(structures);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des structures", e);
            ApiResponse<List<StructureSalariale>> response = ApiResponse.error(
                ApiError.of("GET_STRUCTURES_ERROR", "Erreur lors de la récupération des structures: " + e.getMessage())
            );
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une structure par ID", description = "Retourne une structure salariale spécifique par son ID")
    public ResponseEntity<ApiResponse<StructureSalariale>> getStructureById(
            @Parameter(description = "ID de la structure à récupérer") @PathVariable UUID id) {
        log.info("GET /api/paie/structures/{} - Récupération de la structure", id);
        
        try {
            Optional<StructureSalariale> structure = structureSalarialeService.getStructureSalarialeById(id);
            if (structure.isPresent()) {
                ApiResponse<StructureSalariale> response = ApiResponse.success(structure.get());
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<StructureSalariale> response = ApiResponse.error(
                    ApiError.of("STRUCTURE_NOT_FOUND", "Structure non trouvée avec l'ID: " + id)
                );
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de la structure {}", id, e);
            ApiResponse<StructureSalariale> response = ApiResponse.error(
                ApiError.of("GET_STRUCTURE_ERROR", "Erreur lors de la récupération de la structure: " + e.getMessage())
            );
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/test")
    @Operation(summary = "Endpoint de test", description = "Endpoint simple pour tester le fonctionnement de l'API")
    public ResponseEntity<ApiResponse<String>> testEndpoint() {
        log.info("POST /api/paie/structures/test - Endpoint de test");
        
        try {
            String message = "API Structures Salariales - Module M3 fonctionne correctement!";
            ApiResponse<String> response = ApiResponse.success(message);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erreur lors du test de l'API", e);
            ApiResponse<String> response = ApiResponse.error(
                ApiError.of("TEST_ERROR", "Erreur lors du test: " + e.getMessage())
            );
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
