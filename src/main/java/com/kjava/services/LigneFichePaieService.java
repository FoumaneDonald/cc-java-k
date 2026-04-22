package com.kjava.services;

import com.kjava.entities.FicheDePaie;
import com.kjava.entities.LigneFichePaie;
import com.kjava.entities.RegleSalariale;
import com.kjava.enums.StatutBulletin;
import com.kjava.enums.TypeRegle;
import com.kjava.repositories.LigneFichePaieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LigneFichePaieService {

    private final LigneFichePaieRepository ligneFichePaieRepository;
    private final FicheDePaieService ficheDePaieService;

    public LigneFichePaie createLigneFichePaie(LigneFichePaie ligneFichePaie) {
        log.info("Création d'une nouvelle ligne de fiche de paie pour la fiche: {}", ligneFichePaie.getFicheDePaie().getId());
        
        FicheDePaie fiche = ficheDePaieService.getFicheDePaieById(ligneFichePaie.getFicheDePaie().getId())
                .orElseThrow(() -> new IllegalArgumentException("Fiche de paie non trouvée"));
        
        if (fiche.getStatut() == StatutBulletin.VALIDE) {
            throw new IllegalArgumentException("Impossible d'ajouter une ligne à une fiche de paie validée");
        }
        
        if (ligneFichePaieRepository.existsByFicheDePaieIdAndRegleSalarialeId(
                ligneFichePaie.getFicheDePaie().getId(), ligneFichePaie.getRegleSalariale().getId())) {
            throw new IllegalArgumentException("Une ligne avec cette règle existe déjà pour cette fiche");
        }
        
        LigneFichePaie savedLigne = ligneFichePaieRepository.save(ligneFichePaie);
        
        ficheDePaieService.recalculerTotaux(fiche.getId());
        
        return savedLigne;
    }

    public LigneFichePaie updateLigneFichePaie(UUID id, LigneFichePaie ligneFichePaie) {
        log.info("Mise à jour de la ligne de fiche de paie: {}", id);
        
        LigneFichePaie existing = ligneFichePaieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ligne de fiche de paie non trouvée: " + id));
        
        FicheDePaie fiche = ficheDePaieService.getFicheDePaieById(existing.getFicheDePaie().getId())
                .orElseThrow(() -> new IllegalArgumentException("Fiche de paie non trouvée"));
        
        if (fiche.getStatut() == StatutBulletin.VALIDE) {
            throw new IllegalArgumentException("Impossible de modifier une ligne d'une fiche de paie validée");
        }
        
        existing.setMontantBase(ligneFichePaie.getMontantBase());
        existing.setTaux(ligneFichePaie.getTaux());
        existing.setMontantCalcule(ligneFichePaie.getMontantCalcule());
        
        LigneFichePaie savedLigne = ligneFichePaieRepository.save(existing);
        
        ficheDePaieService.recalculerTotaux(fiche.getId());
        
        return savedLigne;
    }

    @Transactional(readOnly = true)
    public Optional<LigneFichePaie> getLigneFichePaieById(UUID id) {
        return ligneFichePaieRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<LigneFichePaie> getAllLignesFichePaie() {
        return ligneFichePaieRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<LigneFichePaie> getLignesByFicheId(UUID ficheDePaieId) {
        return ligneFichePaieRepository.findByFicheDePaieId(ficheDePaieId);
    }

    @Transactional(readOnly = true)
    public List<LigneFichePaie> getLignesByFicheIdOrderByRegleNom(UUID ficheDePaieId) {
        return ligneFichePaieRepository.findByFicheIdOrderByRegleNom(ficheDePaieId);
    }

    @Transactional(readOnly = true)
    public List<LigneFichePaie> getLignesByRegleSalarialeId(UUID regleSalarialeId) {
        return ligneFichePaieRepository.findByRegleSalarialeId(regleSalarialeId);
    }

    @Transactional(readOnly = true)
    public List<LigneFichePaie> getLignesByFicheIdAndType(UUID ficheDePaieId, TypeRegle type) {
        return ligneFichePaieRepository.findByFicheIdAndRegleType(ficheDePaieId, type);
    }

    @Transactional(readOnly = true)
    public List<LigneFichePaie> getPrimesAndIndemnitesByFicheId(UUID ficheDePaieId) {
        return ligneFichePaieRepository.findPrimesAndIndemnitesByFicheId(ficheDePaieId);
    }

    @Transactional(readOnly = true)
    public List<LigneFichePaie> getAbsencesByFicheId(UUID ficheDePaieId) {
        return ligneFichePaieRepository.findAbsencesByFicheId(ficheDePaieId);
    }

    public void deleteLigneFichePaie(UUID id) {
        log.info("Suppression de la ligne de fiche de paie: {}", id);
        
        LigneFichePaie existing = ligneFichePaieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ligne de fiche de paie non trouvée: " + id));
        
        FicheDePaie fiche = ficheDePaieService.getFicheDePaieById(existing.getFicheDePaie().getId())
                .orElseThrow(() -> new IllegalArgumentException("Fiche de paie non trouvée"));
        
        if (fiche.getStatut() == StatutBulletin.VALIDE) {
            throw new IllegalArgumentException("Impossible de supprimer une ligne d'une fiche de paie validée");
        }
        
        UUID ficheId = existing.getFicheDePaie().getId();
        
        ligneFichePaieRepository.deleteById(id);
        
        ficheDePaieService.recalculerTotaux(ficheId);
    }

    @Transactional(readOnly = true)
    public BigDecimal sumMontantCalculeByFicheIdAndTypes(UUID ficheId, List<TypeRegle> types) {
        BigDecimal result = ligneFichePaieRepository.sumMontantCalculeByFicheIdAndTypes(ficheId, types);
        return result != null ? result : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public BigDecimal sumChargesSalarialesByFicheId(UUID ficheId) {
        BigDecimal result = ligneFichePaieRepository.sumChargesSalarialesByFicheId(ficheId);
        return result != null ? result : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public BigDecimal sumRetenuesByFicheId(UUID ficheId) {
        BigDecimal result = ligneFichePaieRepository.sumRetenuesByFicheId(ficheId);
        return result != null ? result : BigDecimal.ZERO;
    }

    public LigneFichePaie createLigneForFicheAndRegle(UUID ficheId, UUID regleId, BigDecimal montantBase, BigDecimal taux) {
        BigDecimal montantCalcule = calculerMontantCalcule(montantBase, taux);
        
        return createLigneFichePaie(LigneFichePaie.builder()
                .montantBase(montantBase)
                .taux(taux)
                .montantCalcule(montantCalcule)
                .ficheDePaie(FicheDePaie.builder().id(ficheId).build())
                .regleSalariale(RegleSalariale.builder().id(regleId).build())
                .build());
    }

    private BigDecimal calculerMontantCalcule(BigDecimal montantBase, BigDecimal taux) {
        if (taux != null) {
            return montantBase.multiply(taux.divide(BigDecimal.valueOf(100)));
        }
        return montantBase;
    }

    public LigneFichePaie appliquerPlafond(UUID ligneId, BigDecimal plafond) {
        log.info("Application du plafond {} à la ligne: {}", plafond, ligneId);
        
        LigneFichePaie ligne = ligneFichePaieRepository.findById(ligneId)
                .orElseThrow(() -> new IllegalArgumentException("Ligne de fiche de paie non trouvée: " + ligneId));
        
        if (plafond != null && ligne.getMontantCalcule().compareTo(plafond) > 0) {
            ligne.setMontantCalcule(plafond);
            ligneFichePaieRepository.save(ligne);
            
            ficheDePaieService.recalculerTotaux(ligne.getFicheDePaie().getId());
        }
        
        return ligne;
    }
}
