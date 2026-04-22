package com.kjava.controllers;

import com.kjava.entities.LotBulletinPaie;
import com.kjava.enums.StatutBulletin;
import com.kjava.services.LotBulletinPaieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/lots-bulletin-paie")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class LotBulletinPaieController {

    private final LotBulletinPaieService lotBulletinPaieService;

    @PostMapping
    public ResponseEntity<LotBulletinPaie> createLotBulletinPaie(@RequestBody LotBulletinPaie lotBulletinPaie) {
        log.info("POST /api/v1/lots-bulletin-paie - Création lot: {}/{}", lotBulletinPaie.getMois(), lotBulletinPaie.getAnnee());
        LotBulletinPaie created = lotBulletinPaieService.createLotBulletinPaie(lotBulletinPaie);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LotBulletinPaie> updateLotBulletinPaie(
            @PathVariable UUID id, 
            @RequestBody LotBulletinPaie lotBulletinPaie) {
        log.info("PUT /api/v1/lots-bulletin-paie/{} - Mise à jour lot", id);
        LotBulletinPaie updated = lotBulletinPaieService.updateLotBulletinPaie(id, lotBulletinPaie);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LotBulletinPaie> getLotBulletinPaieById(@PathVariable UUID id) {
        log.info("GET /api/v1/lots-bulletin-paie/{}", id);
        Optional<LotBulletinPaie> lot = lotBulletinPaieService.getLotBulletinPaieById(id);
        return lot.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<LotBulletinPaie>> getAllLotsBulletinPaie() {
        log.info("GET /api/v1/lots-bulletin-paie");
        List<LotBulletinPaie> lots = lotBulletinPaieService.getAllLotsBulletinPaie();
        return ResponseEntity.ok(lots);
    }

    @GetMapping("/periode")
    public ResponseEntity<List<LotBulletinPaie>> getLotsByMoisAndAnnee(
            @RequestParam Integer mois,
            @RequestParam Integer annee) {
        log.info("GET /api/v1/lots-bulletin-paie/periode?mois={}&annee={}", mois, annee);
        List<LotBulletinPaie> lots = lotBulletinPaieService.getLotsByMoisAndAnnee(mois, annee);
        return ResponseEntity.ok(lots);
    }

    @GetMapping("/periode/unique")
    public ResponseEntity<Optional<LotBulletinPaie>> getLotByMoisAndAnnee(
            @RequestParam Integer mois,
            @RequestParam Integer annee) {
        log.info("GET /api/v1/lots-bulletin-paie/periode/unique?mois={}&annee={}", mois, annee);
        Optional<LotBulletinPaie> lot = lotBulletinPaieService.getLotByMoisAndAnnee(mois, annee);
        return ResponseEntity.ok(lot);
    }

    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<LotBulletinPaie>> getLotsByStatut(@PathVariable StatutBulletin statut) {
        log.info("GET /api/v1/lots-bulletin-paie/statut/{}", statut);
        List<LotBulletinPaie> lots = lotBulletinPaieService.getLotsByStatut(statut);
        return ResponseEntity.ok(lots);
    }

    @GetMapping("/statut/{statut}/ordered")
    public ResponseEntity<List<LotBulletinPaie>> getLotsByStatutOrdered(@PathVariable StatutBulletin statut) {
        log.info("GET /api/v1/lots-bulletin-paie/statut/{}/ordered", statut);
        List<LotBulletinPaie> lots = lotBulletinPaieService.getLotsByStatutOrderByAnneeMoisDesc(statut);
        return ResponseEntity.ok(lots);
    }

    @GetMapping("/annee/{annee}")
    public ResponseEntity<List<LotBulletinPaie>> getLotsByAnnee(@PathVariable Integer annee) {
        log.info("GET /api/v1/lots-bulletin-paie/annee/{}", annee);
        List<LotBulletinPaie> lots = lotBulletinPaieService.getLotsByAnneeOrderByMoisDesc(annee);
        return ResponseEntity.ok(lots);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLotBulletinPaie(@PathVariable UUID id) {
        log.info("DELETE /api/v1/lots-bulletin-paie/{}", id);
        lotBulletinPaieService.deleteLotBulletinPaie(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/valider")
    public ResponseEntity<LotBulletinPaie> validerLot(@PathVariable UUID id) {
        log.info("PUT /api/v1/lots-bulletin-paie/{}/valider", id);
        LotBulletinPaie validated = lotBulletinPaieService.validerLot(id);
        return ResponseEntity.ok(validated);
    }

    @PutMapping("/{id}/annuler")
    public ResponseEntity<LotBulletinPaie> annulerLot(@PathVariable UUID id) {
        log.info("PUT /api/v1/lots-bulletin-paie/{}/annuler", id);
        LotBulletinPaie cancelled = lotBulletinPaieService.annulerLot(id);
        return ResponseEntity.ok(cancelled);
    }

    @GetMapping("/validated-exists")
    public ResponseEntity<Boolean> existsValidatedLot(
            @RequestParam Integer mois,
            @RequestParam Integer annee) {
        log.info("GET /api/v1/lots-bulletin-paie/validated-exists?mois={}&annee={}", mois, annee);
        boolean exists = lotBulletinPaieService.existsValidatedLot(mois, annee);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/validated-count")
    public ResponseEntity<Long> countValidatedByMonthAndYear(
            @RequestParam Integer mois,
            @RequestParam Integer annee) {
        log.info("GET /api/v1/lots-bulletin-paie/validated-count?mois={}&annee={}", mois, annee);
        long count = lotBulletinPaieService.countValidatedByMonthAndYear(mois, annee);
        return ResponseEntity.ok(count);
    }

    @PostMapping("/periode")
    public ResponseEntity<LotBulletinPaie> createLotForPeriod(
            @RequestParam Integer mois,
            @RequestParam Integer annee) {
        log.info("POST /api/v1/lots-bulletin-paie/periode?mois={}&annee={}", mois, annee);
        LotBulletinPaie lot = lotBulletinPaieService.createLotForPeriod(mois, annee);
        return ResponseEntity.ok(lot);
    }
}
