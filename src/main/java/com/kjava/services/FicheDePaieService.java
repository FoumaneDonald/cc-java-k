package com.kjava.services;

import com.kjava.entities.FicheDePaie;
import com.kjava.entities.LigneFichePaie;
import com.kjava.enums.StatutBulletin;
import com.kjava.repositories.FicheDePaieRepository;
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
public class FicheDePaieService {

    private final FicheDePaieRepository ficheDePaieRepository;
    private final LigneFichePaieService ligneFichePaieService;

    public FicheDePaie createFicheDePaie(FicheDePaie ficheDePaie) {
        log.info("Création d'une nouvelle fiche de paie pour l'employé: {}", ficheDePaie.getEmployeeId());
        
        if (ficheDePaieRepository.existsByEmployeeIdAndLotBulletinPaieId(
                ficheDePaie.getEmployeeId(), ficheDePaie.getLotBulletinPaie().getId())) {
            throw new IllegalArgumentException("Une fiche de paie existe déjà pour cet employé et cette période");
        }
        
        ficheDePaie.setStatut(StatutBulletin.BROUILLON);
        
        FicheDePaie savedFiche = ficheDePaieRepository.save(ficheDePaie);
        
        recalculerTotaux(savedFiche.getId());
        
        return savedFiche;
    }

    public FicheDePaie updateFicheDePaie(UUID id, FicheDePaie ficheDePaie) {
        log.info("Mise à jour de la fiche de paie: {}", id);
        
        FicheDePaie existing = ficheDePaieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Fiche de paie non trouvée: " + id));
        
        if (existing.getStatut() == StatutBulletin.VALIDE) {
            throw new IllegalArgumentException("Impossible de modifier une fiche de paie validée");
        }
        
        existing.setSalaireDeBase(ficheDePaie.getSalaireDeBase());
        existing.setStructureSalariale(ficheDePaie.getStructureSalariale());
        
        FicheDePaie savedFiche = ficheDePaieRepository.save(existing);
        
        recalculerTotaux(savedFiche.getId());
        
        return savedFiche;
    }

    @Transactional(readOnly = true)
    public Optional<FicheDePaie> getFicheDePaieById(UUID id) {
        return ficheDePaieRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<FicheDePaie> getFicheDePaieByIdWithLignes(UUID id) {
        return ficheDePaieRepository.findByIdWithLignes(id);
    }

    @Transactional(readOnly = true)
    public List<FicheDePaie> getAllFichesDePaie() {
        return ficheDePaieRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<FicheDePaie> getFichesByEmployeeId(UUID employeeId) {
        return ficheDePaieRepository.findByEmployeeId(employeeId);
    }

    @Transactional(readOnly = true)
    public List<FicheDePaie> getFichesByLotBulletinPaieId(UUID lotBulletinPaieId) {
        return ficheDePaieRepository.findByLotBulletinPaieId(lotBulletinPaieId);
    }

    @Transactional(readOnly = true)
    public List<FicheDePaie> getFichesByStatut(StatutBulletin statut) {
        return ficheDePaieRepository.findByStatut(statut);
    }

    @Transactional(readOnly = true)
    public Optional<FicheDePaie> getFicheByEmployeeAndPeriod(UUID employeeId, Integer mois, Integer annee) {
        return ficheDePaieRepository.findByEmployeeIdAndPeriod(employeeId, mois, annee);
    }

    @Transactional(readOnly = true)
    public List<FicheDePaie> getFichesByLotIdAndStatut(UUID lotId, StatutBulletin statut) {
        return ficheDePaieRepository.findByLotIdAndStatut(lotId, statut);
    }

    public void deleteFicheDePaie(UUID id) {
        log.info("Suppression de la fiche de paie: {}", id);
        
        FicheDePaie existing = ficheDePaieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Fiche de paie non trouvée: " + id));
        
        if (existing.getStatut() == StatutBulletin.VALIDE) {
            throw new IllegalArgumentException("Impossible de supprimer une fiche de paie validée");
        }
        
        ficheDePaieRepository.deleteById(id);
    }

    public FicheDePaie validerFiche(UUID id) {
        log.info("Validation de la fiche de paie: {}", id);
        
        FicheDePaie existing = ficheDePaieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Fiche de paie non trouvée: " + id));
        
        if (existing.getStatut() == StatutBulletin.VALIDE) {
            throw new IllegalArgumentException("La fiche de paie est déjà validée");
        }
        
        recalculerTotaux(id);
        
        existing.setStatut(StatutBulletin.VALIDE);
        return ficheDePaieRepository.save(existing);
    }

    public FicheDePaie annulerFiche(UUID id) {
        log.info("Annulation de la fiche de paie: {}", id);
        
        FicheDePaie existing = ficheDePaieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Fiche de paie non trouvée: " + id));
        
        if (existing.getStatut() == StatutBulletin.ANNULE) {
            throw new IllegalArgumentException("La fiche de paie est déjà annulée");
        }
        
        existing.setStatut(StatutBulletin.ANNULE);
        return ficheDePaieRepository.save(existing);
    }

    @Transactional(readOnly = true)
    public long countValidatedByLotId(UUID lotId) {
        return ficheDePaieRepository.countValidatedByLotId(lotId);
    }

    @Transactional(readOnly = true)
    public BigDecimal sumNetAPayerByLotId(UUID lotId) {
        return ficheDePaieRepository.sumNetAPayerByLotId(lotId);
    }

    public void recalculerTotaux(UUID ficheId) {
        log.info("Recalcul des totaux pour la fiche de paie: {}", ficheId);
        
        FicheDePaie fiche = ficheDePaieRepository.findById(ficheId)
                .orElseThrow(() -> new IllegalArgumentException("Fiche de paie non trouvée: " + ficheId));
        
        if (fiche.getStatut() == StatutBulletin.VALIDE) {
            throw new IllegalArgumentException("Impossible de recalculer les totaux d'une fiche validée");
        }
        
        BigDecimal totalBrut = BigDecimal.ZERO;
        BigDecimal totalRetenues = BigDecimal.ZERO;
        BigDecimal totalChargesSalariales = BigDecimal.ZERO;
        
        List<LigneFichePaie> lignes = ligneFichePaieService.getLignesByFicheId(ficheId);
        
        for (LigneFichePaie ligne : lignes) {
            BigDecimal montant = ligne.getMontantCalcule();
            
            switch (ligne.getRegleSalariale().getType()) {
                case PRIME, INDEMNITE -> totalBrut = totalBrut.add(montant);
                case RETENUE -> totalRetenues = totalRetenues.add(montant);
                case CHARGE_SALARIALE -> totalChargesSalariales = totalChargesSalariales.add(montant);
                case ABSENCE -> totalBrut = totalBrut.subtract(montant);
            }
        }
        
        totalBrut = totalBrut.add(fiche.getSalaireDeBase());
        
        BigDecimal netAPayer = totalBrut.subtract(totalRetenues).subtract(totalChargesSalariales);
        
        fiche.setTotalBrut(totalBrut);
        fiche.setTotalRetenues(totalRetenues);
        fiche.setTotalChargesSalariales(totalChargesSalariales);
        fiche.setNetAPayer(netAPayer);
        
        ficheDePaieRepository.save(fiche);
    }

    public FicheDePaie createFicheForEmployee(UUID employeeId, UUID lotId, UUID structureId, BigDecimal salaireDeBase) {
        FicheDePaie fiche = FicheDePaie.builder()
                .employeeId(employeeId)
                .salaireDeBase(salaireDeBase)
                .totalBrut(salaireDeBase)
                .totalRetenues(BigDecimal.ZERO)
                .totalChargesSalariales(BigDecimal.ZERO)
                .netAPayer(salaireDeBase)
                .statut(StatutBulletin.BROUILLON)
                .build();
        
        // Définir les relations après la création du builder
        fiche.setLotBulletinPaie(com.kjava.entities.LotBulletinPaie.builder().id(lotId).build());
        fiche.setStructureSalariale(com.kjava.entities.StructureSalariale.builder().id(structureId).build());
        
        return createFicheDePaie(fiche);
    }
}
