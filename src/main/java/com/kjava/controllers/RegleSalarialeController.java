package com.kjava.controllers;

import com.kjava.entities.RegleSalariale;
import com.kjava.enums.TypeRegle;
import com.kjava.services.RegleSalarialeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/regles-salariales")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class RegleSalarialeController {

    private final RegleSalarialeService regleSalarialeService;

    @PostMapping
    public ResponseEntity<RegleSalariale> createRegleSalariale(@RequestBody RegleSalariale regleSalariale) {
        log.info("POST /api/v1/regles-salariales - Création règle: {}", regleSalariale.getCode());
        RegleSalariale created = regleSalarialeService.createRegleSalariale(regleSalariale);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegleSalariale> updateRegleSalariale(
            @PathVariable UUID id, 
            @RequestBody RegleSalariale regleSalariale) {
        log.info("PUT /api/v1/regles-salariales/{} - Mise à jour règle", id);
        RegleSalariale updated = regleSalarialeService.updateRegleSalariale(id, regleSalariale);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegleSalariale> getRegleSalarialeById(@PathVariable UUID id) {
        log.info("GET /api/v1/regles-salariales/{}", id);
        Optional<RegleSalariale> regle = regleSalarialeService.getRegleSalarialeById(id);
        return regle.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<Optional<RegleSalariale>> getRegleSalarialeByCode(@PathVariable String code) {
        log.info("GET /api/v1/regles-salariales/code/{}", code);
        Optional<RegleSalariale> regle = regleSalarialeService.getRegleSalarialeByCode(code);
        return ResponseEntity.ok(regle);
    }

    @GetMapping
    public ResponseEntity<List<RegleSalariale>> getAllReglesSalariales() {
        log.info("GET /api/v1/regles-salariales");
        List<RegleSalariale> regles = regleSalarialeService.getAllReglesSalariales();
        return ResponseEntity.ok(regles);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<RegleSalariale>> getReglesByType(@PathVariable TypeRegle type) {
        log.info("GET /api/v1/regles-salariales/type/{}", type);
        List<RegleSalariale> regles = regleSalarialeService.getReglesByType(type);
        return ResponseEntity.ok(regles);
    }

    @GetMapping("/recurrentes")
    public ResponseEntity<List<RegleSalariale>> getReglesRecurrentes() {
        log.info("GET /api/v1/regles-salariales/recurrentes");
        List<RegleSalariale> regles = regleSalarialeService.getReglesRecurrentes();
        return ResponseEntity.ok(regles);
    }

    @GetMapping("/non-recurrentes")
    public ResponseEntity<List<RegleSalariale>> getReglesNonRecurrentes() {
        log.info("GET /api/v1/regles-salariales/non-recurrentes");
        List<RegleSalariale> regles = regleSalarialeService.getReglesNonRecurrentes();
        return ResponseEntity.ok(regles);
    }

    @GetMapping("/avec-plafond")
    public ResponseEntity<List<RegleSalariale>> getReglesWithPlafond() {
        log.info("GET /api/v1/regles-salariales/avec-plafond");
        List<RegleSalariale> regles = regleSalarialeService.getReglesWithPlafond();
        return ResponseEntity.ok(regles);
    }

    @GetMapping("/filtre")
    public ResponseEntity<List<RegleSalariale>> getReglesByTypeAndRecurrente(
            @RequestParam TypeRegle type,
            @RequestParam Boolean isRecurrente) {
        log.info("GET /api/v1/regles-salariales/filtre?type={}&isRecurrente={}", type, isRecurrente);
        List<RegleSalariale> regles = regleSalarialeService.getReglesByTypeAndRecurrente(type, isRecurrente);
        return ResponseEntity.ok(regles);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegleSalariale(@PathVariable UUID id) {
        log.info("DELETE /api/v1/regles-salariales/{}", id);
        regleSalarialeService.deleteRegleSalariale(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/prime")
    public ResponseEntity<RegleSalariale> createPrime(
            @RequestParam String code,
            @RequestParam String nom,
            @RequestParam String formule,
            @RequestParam(required = false) BigDecimal plafond) {
        log.info("POST /api/v1/regles-salariales/prime - Création prime: {}", code);
        RegleSalariale prime = regleSalarialeService.createPrime(code, nom, formule, plafond);
        return ResponseEntity.ok(prime);
    }

    @PostMapping("/indemnite")
    public ResponseEntity<RegleSalariale> createIndemnite(
            @RequestParam String code,
            @RequestParam String nom,
            @RequestParam String formule,
            @RequestParam(required = false) BigDecimal plafond) {
        log.info("POST /api/v1/regles-salariales/indemnite - Création indemnité: {}", code);
        RegleSalariale indemnite = regleSalarialeService.createIndemnite(code, nom, formule, plafond);
        return ResponseEntity.ok(indemnite);
    }

    @PostMapping("/retenue")
    public ResponseEntity<RegleSalariale> createRetenue(
            @RequestParam String code,
            @RequestParam String nom,
            @RequestParam String formule,
            @RequestParam(required = false) BigDecimal plafond) {
        log.info("POST /api/v1/regles-salariales/retenue - Création retenue: {}", code);
        RegleSalariale retenue = regleSalarialeService.createRetenue(code, nom, formule, plafond);
        return ResponseEntity.ok(retenue);
    }

    @PostMapping("/charge-salariale")
    public ResponseEntity<RegleSalariale> createChargeSalariale(
            @RequestParam String code,
            @RequestParam String nom,
            @RequestParam String formule,
            @RequestParam(required = false) BigDecimal plafond) {
        log.info("POST /api/v1/regles-salariales/charge-salariale - Création charge: {}", code);
        RegleSalariale charge = regleSalarialeService.createChargeSalariale(code, nom, formule, plafond);
        return ResponseEntity.ok(charge);
    }

    @PostMapping("/absence")
    public ResponseEntity<RegleSalariale> createAbsence(
            @RequestParam String code,
            @RequestParam String nom,
            @RequestParam String formule) {
        log.info("POST /api/v1/regles-salariales/absence - Création absence: {}", code);
        RegleSalariale absence = regleSalarialeService.createAbsence(code, nom, formule);
        return ResponseEntity.ok(absence);
    }
}
