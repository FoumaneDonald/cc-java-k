package com.kjava.controllers;

import com.kjava.entities.LigneFichePaie;
import com.kjava.enums.TypeRegle;
import com.kjava.services.LigneFichePaieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/lignes-fiche-paie")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class LigneFichePaieController {

    private final LigneFichePaieService ligneFichePaieService;

    @PostMapping
    public ResponseEntity<LigneFichePaie> createLigneFichePaie(@RequestBody LigneFichePaie ligneFichePaie) {
        log.info("POST /api/v1/lignes-fiche-paie - Création ligne pour fiche: {}", ligneFichePaie.getFicheDePaie().getId());
        LigneFichePaie created = ligneFichePaieService.createLigneFichePaie(ligneFichePaie);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LigneFichePaie> updateLigneFichePaie(
            @PathVariable UUID id, 
            @RequestBody LigneFichePaie ligneFichePaie) {
        log.info("PUT /api/v1/lignes-fiche-paie/{} - Mise à jour ligne", id);
        LigneFichePaie updated = ligneFichePaieService.updateLigneFichePaie(id, ligneFichePaie);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LigneFichePaie> getLigneFichePaieById(@PathVariable UUID id) {
        log.info("GET /api/v1/lignes-fiche-paie/{}", id);
        Optional<LigneFichePaie> ligne = ligneFichePaieService.getLigneFichePaieById(id);
        return ligne.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<LigneFichePaie>> getAllLignesFichePaie() {
        log.info("GET /api/v1/lignes-fiche-paie");
        List<LigneFichePaie> lignes = ligneFichePaieService.getAllLignesFichePaie();
        return ResponseEntity.ok(lignes);
    }

    @GetMapping("/fiche/{ficheDePaieId}")
    public ResponseEntity<List<LigneFichePaie>> getLignesByFicheId(@PathVariable UUID ficheDePaieId) {
        log.info("GET /api/v1/lignes-fiche-paie/fiche/{}", ficheDePaieId);
        List<LigneFichePaie> lignes = ligneFichePaieService.getLignesByFicheId(ficheDePaieId);
        return ResponseEntity.ok(lignes);
    }

    @GetMapping("/fiche/{ficheDePaieId}/ordered")
    public ResponseEntity<List<LigneFichePaie>> getLignesByFicheIdOrdered(@PathVariable UUID ficheDePaieId) {
        log.info("GET /api/v1/lignes-fiche-paie/fiche/{}/ordered", ficheDePaieId);
        List<LigneFichePaie> lignes = ligneFichePaieService.getLignesByFicheIdOrderByRegleNom(ficheDePaieId);
        return ResponseEntity.ok(lignes);
    }

    @GetMapping("/regle/{regleSalarialeId}")
    public ResponseEntity<List<LigneFichePaie>> getLignesByRegleSalarialeId(@PathVariable UUID regleSalarialeId) {
        log.info("GET /api/v1/lignes-fiche-paie/regle/{}", regleSalarialeId);
        List<LigneFichePaie> lignes = ligneFichePaieService.getLignesByRegleSalarialeId(regleSalarialeId);
        return ResponseEntity.ok(lignes);
    }

    @GetMapping("/fiche/{ficheDePaieId}/type/{type}")
    public ResponseEntity<List<LigneFichePaie>> getLignesByFicheIdAndType(
            @PathVariable UUID ficheDePaieId,
            @PathVariable TypeRegle type) {
        log.info("GET /api/v1/lignes-fiche-paie/fiche/{}/type/{}", ficheDePaieId, type);
        List<LigneFichePaie> lignes = ligneFichePaieService.getLignesByFicheIdAndType(ficheDePaieId, type);
        return ResponseEntity.ok(lignes);
    }

    @GetMapping("/fiche/{ficheDePaieId}/primes-indemnites")
    public ResponseEntity<List<LigneFichePaie>> getPrimesAndIndemnitesByFicheId(@PathVariable UUID ficheDePaieId) {
        log.info("GET /api/v1/lignes-fiche-paie/fiche/{}/primes-indemnites", ficheDePaieId);
        List<LigneFichePaie> lignes = ligneFichePaieService.getPrimesAndIndemnitesByFicheId(ficheDePaieId);
        return ResponseEntity.ok(lignes);
    }

    @GetMapping("/fiche/{ficheDePaieId}/absences")
    public ResponseEntity<List<LigneFichePaie>> getAbsencesByFicheId(@PathVariable UUID ficheDePaieId) {
        log.info("GET /api/v1/lignes-fiche-paie/fiche/{}/absences", ficheDePaieId);
        List<LigneFichePaie> lignes = ligneFichePaieService.getAbsencesByFicheId(ficheDePaieId);
        return ResponseEntity.ok(lignes);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLigneFichePaie(@PathVariable UUID id) {
        log.info("DELETE /api/v1/lignes-fiche-paie/{}", id);
        ligneFichePaieService.deleteLigneFichePaie(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/fiche/{ficheId}/sum-by-types")
    public ResponseEntity<BigDecimal> sumMontantCalculeByFicheIdAndTypes(
            @PathVariable UUID ficheId,
            @RequestParam List<TypeRegle> types) {
        log.info("GET /api/v1/lignes-fiche-paie/fiche/{}/sum-by-types", ficheId);
        BigDecimal sum = ligneFichePaieService.sumMontantCalculeByFicheIdAndTypes(ficheId, types);
        return ResponseEntity.ok(sum);
    }

    @GetMapping("/fiche/{ficheId}/sum-charges")
    public ResponseEntity<BigDecimal> sumChargesSalarialesByFicheId(@PathVariable UUID ficheId) {
        log.info("GET /api/v1/lignes-fiche-paie/fiche/{}/sum-charges", ficheId);
        BigDecimal sum = ligneFichePaieService.sumChargesSalarialesByFicheId(ficheId);
        return ResponseEntity.ok(sum);
    }

    @GetMapping("/fiche/{ficheId}/sum-retenues")
    public ResponseEntity<BigDecimal> sumRetenuesByFicheId(@PathVariable UUID ficheId) {
        log.info("GET /api/v1/lignes-fiche-paie/fiche/{}/sum-retenues", ficheId);
        BigDecimal sum = ligneFichePaieService.sumRetenuesByFicheId(ficheId);
        return ResponseEntity.ok(sum);
    }

    @PostMapping("/fiche-regle")
    public ResponseEntity<LigneFichePaie> createLigneForFicheAndRegle(
            @RequestParam UUID ficheId,
            @RequestParam UUID regleId,
            @RequestParam BigDecimal montantBase,
            @RequestParam(required = false) BigDecimal taux) {
        log.info("POST /api/v1/lignes-fiche-paie/fiche-regle - Création ligne pour fiche: {}, règle: {}", ficheId, regleId);
        LigneFichePaie ligne = ligneFichePaieService.createLigneForFicheAndRegle(ficheId, regleId, montantBase, taux);
        return ResponseEntity.ok(ligne);
    }

    @PutMapping("/{id}/appliquer-plafond")
    public ResponseEntity<LigneFichePaie> appliquerPlafond(
            @PathVariable UUID id,
            @RequestParam BigDecimal plafond) {
        log.info("PUT /api/v1/lignes-fiche-paie/{}/appliquer-plafond - Plafond: {}", id, plafond);
        LigneFichePaie ligne = ligneFichePaieService.appliquerPlafond(id, plafond);
        return ResponseEntity.ok(ligne);
    }
}
