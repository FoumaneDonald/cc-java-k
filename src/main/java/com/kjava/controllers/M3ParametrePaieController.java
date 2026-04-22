package com.kjava.controllers;

import com.kjava.dto.ApiResponse;
import com.kjava.dto.ApiError;
import com.kjava.entities.ParametrePaie;
import com.kjava.services.ParametrePaieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/paie/parametres")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Paramètres de Paie", description = "Gestion des paramètres de paie du Module M3")
public class M3ParametrePaieController {

    private final ParametrePaieService parametrePaieService;

    @GetMapping
    @Operation(summary = "Lister tous les paramètres de paie", description = "Retourne la liste de tous les paramètres de paie")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des paramètres récupérée avec succès")
    })
    public ResponseEntity<ApiResponse<List<ParametrePaie>>> getAllParametres() {
        log.info("GET /api/paie/parametres - Récupération de tous les paramètres");
        
        try {
            List<ParametrePaie> parametres = parametrePaieService.getAllParametresPaie();
            ApiResponse<List<ParametrePaie>> response = ApiResponse.success(parametres);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des paramètres", e);
            ApiResponse<List<ParametrePaie>> response = ApiResponse.error(
                ApiError.of("GET_PARAMETRES_ERROR", "Erreur lors de la récupération des paramètres: " + e.getMessage())
            );
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un paramètre par ID", description = "Retourne un paramètre de paie spécifique par son ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Paramètre récupéré avec succès"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Paramètre non trouvé")
    })
    public ResponseEntity<ApiResponse<ParametrePaie>> getParametreById(
            @Parameter(description = "ID du paramètre à récupérer") @PathVariable UUID id) {
        log.info("GET /api/paie/parametres/{} - Récupération du paramètre", id);
        
        try {
            Optional<ParametrePaie> parametre = parametrePaieService.getParametrePaieById(id);
            if (parametre.isPresent()) {
                ApiResponse<ParametrePaie> response = ApiResponse.success(parametre.get());
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<ParametrePaie> response = ApiResponse.error(
                    ApiError.of("PARAMETRE_NOT_FOUND", "Paramètre non trouvé avec l'ID: " + id)
                );
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Erreur lors de la récupération du paramètre {}", id, e);
            ApiResponse<ParametrePaie> response = ApiResponse.error(
                ApiError.of("GET_PARAMETRE_ERROR", "Erreur lors de la récupération du paramètre: " + e.getMessage())
            );
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/test")
    @Operation(summary = "Endpoint de test", description = "Endpoint simple pour tester le fonctionnement de l'API")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Test réussi")
    })
    public ResponseEntity<ApiResponse<String>> testEndpoint() {
        log.info("POST /api/paie/parametres/test - Endpoint de test");
        
        try {
            String message = "API Module M3 - Gestion de Paie fonctionne correctement!";
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
