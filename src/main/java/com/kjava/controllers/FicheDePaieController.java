package com.kjava.controllers;

import com.kjava.entities.FicheDePaie;
import com.kjava.enums.StatutBulletin;
import com.kjava.services.FicheDePaieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/fiches-de-paie")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class FicheDePaieController {

    private final FicheDePaieService ficheDePaieService;

    @PostMapping
    public ResponseEntity<FicheDePaie> createFicheDePaie(@RequestBody FicheDePaie ficheDePaie) {
        log.info("POST /api/v1/fiches-de-paie - Création fiche pour employé: {}", ficheDePaie.getEmployeeId());
        FicheDePaie created = ficheDePaieService.createFicheDePaie(ficheDePaie);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FicheDePaie> updateFicheDePaie(
            @PathVariable UUID id, 
            @RequestBody FicheDePaie ficheDePaie) {
        log.info("PUT /api/v1/fiches-de-paie/{} - Mise à jour fiche", id);
        FicheDePaie updated = ficheDePaieService.updateFicheDePaie(id, ficheDePaie);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FicheDePaie> getFicheDePaieById(@PathVariable UUID id) {
        log.info("GET /api/v1/fiches-de-paie/{}", id);
        Optional<FicheDePaie> fiche = ficheDePaieService.getFicheDePaieById(id);
        return fiche.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/with-lignes")
    public ResponseEntity<Optional<FicheDePaie>> getFicheDePaieByIdWithLignes(@PathVariable UUID id) {
        log.info("GET /api/v1/fiches-de-paie/{}/with-lignes", id);
        Optional<FicheDePaie> fiche = ficheDePaieService.getFicheDePaieByIdWithLignes(id);
        return ResponseEntity.ok(fiche);
    }

    @GetMapping
    public ResponseEntity<List<FicheDePaie>> getAllFichesDePaie() {
        log.info("GET /api/v1/fiches-de-paie");
        List<FicheDePaie> fiches = ficheDePaieService.getAllFichesDePaie();
        return ResponseEntity.ok(fiches);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<FicheDePaie>> getFichesByEmployeeId(@PathVariable UUID employeeId) {
        log.info("GET /api/v1/fiches-de-paie/employee/{}", employeeId);
        List<FicheDePaie> fiches = ficheDePaieService.getFichesByEmployeeId(employeeId);
        return ResponseEntity.ok(fiches);
    }

    @GetMapping("/lot/{lotBulletinPaieId}")
    public ResponseEntity<List<FicheDePaie>> getFichesByLotBulletinPaieId(@PathVariable UUID lotBulletinPaieId) {
        log.info("GET /api/v1/fiches-de-paie/lot/{}", lotBulletinPaieId);
        List<FicheDePaie> fiches = ficheDePaieService.getFichesByLotBulletinPaieId(lotBulletinPaieId);
        return ResponseEntity.ok(fiches);
    }

    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<FicheDePaie>> getFichesByStatut(@PathVariable StatutBulletin statut) {
        log.info("GET /api/v1/fiches-de-paie/statut/{}", statut);
        List<FicheDePaie> fiches = ficheDePaieService.getFichesByStatut(statut);
        return ResponseEntity.ok(fiches);
    }

    @GetMapping("/employee/{employeeId}/periode")
    public ResponseEntity<Optional<FicheDePaie>> getFicheByEmployeeAndPeriod(
            @PathVariable UUID employeeId,
            @RequestParam Integer mois,
            @RequestParam Integer annee) {
        log.info("GET /api/v1/fiches-de-paie/employee/{}/periode?mois={}&annee={}", employeeId, mois, annee);
        Optional<FicheDePaie> fiche = ficheDePaieService.getFicheByEmployeeAndPeriod(employeeId, mois, annee);
        return ResponseEntity.ok(fiche);
    }

    @GetMapping("/lot/{lotId}/statut/{statut}")
    public ResponseEntity<List<FicheDePaie>> getFichesByLotIdAndStatut(
            @PathVariable UUID lotId,
            @PathVariable StatutBulletin statut) {
        log.info("GET /api/v1/fiches-de-paie/lot/{}/statut/{}", lotId, statut);
        List<FicheDePaie> fiches = ficheDePaieService.getFichesByLotIdAndStatut(lotId, statut);
        return ResponseEntity.ok(fiches);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFicheDePaie(@PathVariable UUID id) {
        log.info("DELETE /api/v1/fiches-de-paie/{}", id);
        ficheDePaieService.deleteFicheDePaie(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/valider")
    public ResponseEntity<FicheDePaie> validerFiche(@PathVariable UUID id) {
        log.info("PUT /api/v1/fiches-de-paie/{}/valider", id);
        FicheDePaie validated = ficheDePaieService.validerFiche(id);
        return ResponseEntity.ok(validated);
    }

    @PutMapping("/{id}/annuler")
    public ResponseEntity<FicheDePaie> annulerFiche(@PathVariable UUID id) {
        log.info("PUT /api/v1/fiches-de-paie/{}/annuler", id);
        FicheDePaie cancelled = ficheDePaieService.annulerFiche(id);
        return ResponseEntity.ok(cancelled);
    }

    @PutMapping("/{id}/recalculer")
    public ResponseEntity<FicheDePaie> recalculerTotaux(@PathVariable UUID id) {
        log.info("PUT /api/v1/fiches-de-paie/{}/recalculer", id);
        ficheDePaieService.recalculerTotaux(id);
        Optional<FicheDePaie> fiche = ficheDePaieService.getFicheDePaieById(id);
        return fiche.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/lot/{lotId}/validated-count")
    public ResponseEntity<Long> countValidatedByLotId(@PathVariable UUID lotId) {
        log.info("GET /api/v1/fiches-de-paie/lot/{}/validated-count", lotId);
        long count = ficheDePaieService.countValidatedByLotId(lotId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/lot/{lotId}/sum-net")
    public ResponseEntity<BigDecimal> sumNetAPayerByLotId(@PathVariable UUID lotId) {
        log.info("GET /api/v1/fiches-de-paie/lot/{}/sum-net", lotId);
        BigDecimal sum = ficheDePaieService.sumNetAPayerByLotId(lotId);
        return ResponseEntity.ok(sum != null ? sum : BigDecimal.ZERO);
    }

    @PostMapping("/employee")
    public ResponseEntity<FicheDePaie> createFicheForEmployee(
            @RequestParam UUID employeeId,
            @RequestParam UUID lotId,
            @RequestParam UUID structureId,
            @RequestParam BigDecimal salaireDeBase) {
        log.info("POST /api/v1/fiches-de-paie/employee - Création fiche pour employé: {}", employeeId);
        FicheDePaie fiche = ficheDePaieService.createFicheForEmployee(employeeId, lotId, structureId, salaireDeBase);
        return ResponseEntity.ok(fiche);
    }
}
