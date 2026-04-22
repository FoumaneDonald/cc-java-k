package com.kjava.controllers;

import com.kjava.entities.ParametrePaie;
import com.kjava.services.ParametrePaieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/parametres-paie")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ParametrePaieController {

    private final ParametrePaieService parametrePaieService;

    @PostMapping
    public ResponseEntity<ParametrePaie> createParametrePaie(@RequestBody ParametrePaie parametrePaie) {
        log.info("POST /api/v1/parametres-paie - Création paramètre: {}", parametrePaie.getCode());
        ParametrePaie created = parametrePaieService.createParametrePaie(parametrePaie);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParametrePaie> updateParametrePaie(
            @PathVariable UUID id, 
            @RequestBody ParametrePaie parametrePaie) {
        log.info("PUT /api/v1/parametres-paie/{} - Mise à jour paramètre", id);
        ParametrePaie updated = parametrePaieService.updateParametrePaie(id, parametrePaie);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParametrePaie> getParametrePaieById(@PathVariable UUID id) {
        log.info("GET /api/v1/parametres-paie/{}", id);
        Optional<ParametrePaie> parametre = parametrePaieService.getParametrePaieById(id);
        return parametre.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ParametrePaie>> getAllParametresPaie() {
        log.info("GET /api/v1/parametres-paie");
        List<ParametrePaie> parametres = parametrePaieService.getAllParametresPaie();
        return ResponseEntity.ok(parametres);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<List<ParametrePaie>> getParametresByCode(@PathVariable String code) {
        log.info("GET /api/v1/parametres-paie/code/{}", code);
        List<ParametrePaie> parametres = parametrePaieService.getParametresByCode(code);
        return ResponseEntity.ok(parametres);
    }

    @GetMapping("/latest")
    public ResponseEntity<Optional<ParametrePaie>> getLatestParametre(
            @RequestParam String code,
            @RequestParam LocalDate dateEffet) {
        log.info("GET /api/v1/parametres-paie/latest?code={}&dateEffet={}", code, dateEffet);
        Optional<ParametrePaie> parametre = parametrePaieService.getLatestParametreByCodeAndDate(code, dateEffet);
        return ResponseEntity.ok(parametre);
    }

    @GetMapping("/range")
    public ResponseEntity<List<ParametrePaie>> getParametresByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        log.info("GET /api/v1/parametres-paie/range?startDate={}&endDate={}", startDate, endDate);
        List<ParametrePaie> parametres = parametrePaieService.getParametresByDateRange(startDate, endDate);
        return ResponseEntity.ok(parametres);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParametrePaie(@PathVariable UUID id) {
        log.info("DELETE /api/v1/parametres-paie/{}", id);
        parametrePaieService.deleteParametrePaie(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/new-version")
    public ResponseEntity<ParametrePaie> createNewVersion(
            @RequestParam String code,
            @RequestParam String nom,
            @RequestParam BigDecimal valeur,
            @RequestParam LocalDate dateEffet) {
        log.info("POST /api/v1/parametres-paie/new-version - Nouvelle version pour: {}", code);
        ParametrePaie newVersion = parametrePaieService.createNewVersion(code, nom, valeur, dateEffet);
        return ResponseEntity.ok(newVersion);
    }
}
