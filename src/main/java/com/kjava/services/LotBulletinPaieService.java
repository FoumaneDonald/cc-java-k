package com.kjava.services;

import com.kjava.entities.LotBulletinPaie;
import com.kjava.enums.StatutBulletin;
import com.kjava.repositories.LotBulletinPaieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LotBulletinPaieService {

    private final LotBulletinPaieRepository lotBulletinPaieRepository;

    public LotBulletinPaie createLotBulletinPaie(LotBulletinPaie lotBulletinPaie) {
        log.info("Création d'un nouveau lot de bulletin de paie: {}/{}", lotBulletinPaie.getMois(), lotBulletinPaie.getAnnee());
        
        if (lotBulletinPaieRepository.existsByMoisAndAnnee(lotBulletinPaie.getMois(), lotBulletinPaie.getAnnee())) {
            throw new IllegalArgumentException("Un lot pour cette période existe déjà: " + lotBulletinPaie.getMois() + "/" + lotBulletinPaie.getAnnee());
        }
        
        lotBulletinPaie.setDateGeneration(LocalDateTime.now());
        lotBulletinPaie.setStatut(StatutBulletin.BROUILLON);
        
        return lotBulletinPaieRepository.save(lotBulletinPaie);
    }

    public LotBulletinPaie updateLotBulletinPaie(UUID id, LotBulletinPaie lotBulletinPaie) {
        log.info("Mise à jour du lot de bulletin de paie: {}", id);
        
        LotBulletinPaie existing = lotBulletinPaieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lot de bulletin de paie non trouvé: " + id));
        
        if (!existing.getMois().equals(lotBulletinPaie.getMois()) || 
            !existing.getAnnee().equals(lotBulletinPaie.getAnnee())) {
            if (lotBulletinPaieRepository.existsByMoisAndAnnee(lotBulletinPaie.getMois(), lotBulletinPaie.getAnnee())) {
                throw new IllegalArgumentException("Un lot pour cette période existe déjà: " + lotBulletinPaie.getMois() + "/" + lotBulletinPaie.getAnnee());
            }
        }
        
        existing.setMois(lotBulletinPaie.getMois());
        existing.setAnnee(lotBulletinPaie.getAnnee());
        existing.setStatut(lotBulletinPaie.getStatut());
        
        return lotBulletinPaieRepository.save(existing);
    }

    @Transactional(readOnly = true)
    public Optional<LotBulletinPaie> getLotBulletinPaieById(UUID id) {
        return lotBulletinPaieRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<LotBulletinPaie> getAllLotsBulletinPaie() {
        return lotBulletinPaieRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<LotBulletinPaie> getLotsByMoisAndAnnee(Integer mois, Integer annee) {
        return lotBulletinPaieRepository.findByMoisAndAnnee(mois, annee);
    }

    @Transactional(readOnly = true)
    public List<LotBulletinPaie> getLotsByStatut(StatutBulletin statut) {
        return lotBulletinPaieRepository.findByStatut(statut);
    }

    @Transactional(readOnly = true)
    public List<LotBulletinPaie> getLotsByStatutOrderByAnneeMoisDesc(StatutBulletin statut) {
        return lotBulletinPaieRepository.findByStatutOrderByAnneeMoisDesc(statut);
    }

    @Transactional(readOnly = true)
    public List<LotBulletinPaie> getLotsByAnneeOrderByMoisDesc(Integer annee) {
        return lotBulletinPaieRepository.findByAnneeOrderByMoisDesc(annee);
    }

    @Transactional(readOnly = true)
    public Optional<LotBulletinPaie> getLotByMoisAndAnnee(Integer mois, Integer annee) {
        List<LotBulletinPaie> lots = lotBulletinPaieRepository.findByMoisAndAnnee(mois, annee);
        return lots.isEmpty() ? Optional.empty() : Optional.of(lots.get(0));
    }

    public void deleteLotBulletinPaie(UUID id) {
        log.info("Suppression du lot de bulletin de paie: {}", id);
        
        LotBulletinPaie existing = lotBulletinPaieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lot de bulletin de paie non trouvé: " + id));
        
        if (existing.getStatut() == StatutBulletin.VALIDE) {
            throw new IllegalArgumentException("Impossible de supprimer un lot validé");
        }
        
        lotBulletinPaieRepository.deleteById(id);
    }

    public LotBulletinPaie validerLot(UUID id) {
        log.info("Validation du lot de bulletin de paie: {}", id);
        
        LotBulletinPaie existing = lotBulletinPaieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lot de bulletin de paie non trouvé: " + id));
        
        if (existing.getStatut() == StatutBulletin.VALIDE) {
            throw new IllegalArgumentException("Le lot est déjà validé");
        }
        
        existing.setStatut(StatutBulletin.VALIDE);
        return lotBulletinPaieRepository.save(existing);
    }

    public LotBulletinPaie annulerLot(UUID id) {
        log.info("Annulation du lot de bulletin de paie: {}", id);
        
        LotBulletinPaie existing = lotBulletinPaieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lot de bulletin de paie non trouvé: " + id));
        
        if (existing.getStatut() == StatutBulletin.ANNULE) {
            throw new IllegalArgumentException("Le lot est déjà annulé");
        }
        
        existing.setStatut(StatutBulletin.ANNULE);
        return lotBulletinPaieRepository.save(existing);
    }

    @Transactional(readOnly = true)
    public boolean existsValidatedLot(Integer mois, Integer annee) {
        return lotBulletinPaieRepository.existsValidatedByMonthAndYear(mois, annee);
    }

    @Transactional(readOnly = true)
    public long countValidatedByMonthAndYear(Integer mois, Integer annee) {
        return lotBulletinPaieRepository.countValidatedByMonthAndYear(mois, annee);
    }

    public LotBulletinPaie createLotForPeriod(Integer mois, Integer annee) {
        return createLotBulletinPaie(LotBulletinPaie.builder()
                .mois(mois)
                .annee(annee)
                .statut(StatutBulletin.BROUILLON)
                .dateGeneration(LocalDateTime.now())
                .build());
    }
}
