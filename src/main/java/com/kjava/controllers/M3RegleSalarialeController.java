package com.kjava.controllers;

import com.kjava.dto.ApiResponse;
import com.kjava.dto.ApiError;
import com.kjava.entities.RegleSalariale;
import com.kjava.services.RegleSalarialeService;
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
@RequestMapping("/api/paie/regles")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Règles Salariales", description = "Gestion des règles salariales du Module M3")
public class M3RegleSalarialeController {

    private final RegleSalarialeService regleSalarialeService;

    @GetMapping
    @Operation(summary = "Lister toutes les règles salariales", description = "Retourne la liste de toutes les règles salariales")
    public ResponseEntity<ApiResponse<List<RegleSalariale>>> getAllRegles() {
        log.info("GET /api/paie/regles - Récupération de toutes les règles");
        
        try {
            List<RegleSalariale> regles = regleSalarialeService.getAllReglesSalariales();
            ApiResponse<List<RegleSalariale>> response = ApiResponse.success(regles);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des règles", e);
            ApiResponse<List<RegleSalariale>> response = ApiResponse.error(
                ApiError.of("GET_REGLES_ERROR", "Erreur lors de la récupération des règles: " + e.getMessage())
            );
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une règle par ID", description = "Retourne une règle salariale spécifique par son ID")
    public ResponseEntity<ApiResponse<RegleSalariale>> getRegleById(
            @Parameter(description = "ID de la règle à récupérer") @PathVariable UUID id) {
        log.info("GET /api/paie/regles/{} - Récupération de la règle", id);
        
        try {
            Optional<RegleSalariale> regle = regleSalarialeService.getRegleSalarialeById(id);
            if (regle.isPresent()) {
                ApiResponse<RegleSalariale> response = ApiResponse.success(regle.get());
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<RegleSalariale> response = ApiResponse.error(
                    ApiError.of("REGLE_NOT_FOUND", "Règle non trouvée avec l'ID: " + id)
                );
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de la règle {}", id, e);
            ApiResponse<RegleSalariale> response = ApiResponse.error(
                ApiError.of("GET_REGLE_ERROR", "Erreur lors de la récupération de la règle: " + e.getMessage())
            );
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/test")
    @Operation(summary = "Endpoint de test", description = "Endpoint simple pour tester le fonctionnement de l'API")
    public ResponseEntity<ApiResponse<String>> testEndpoint() {
        log.info("POST /api/paie/regles/test - Endpoint de test");
        
        try {
            String message = "API Règles Salariales - Module M3 fonctionne correctement!";
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
