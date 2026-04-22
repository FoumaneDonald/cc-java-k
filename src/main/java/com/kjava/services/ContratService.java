package com.kjava.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kjava.models.Contrat;
import com.kjava.repository.ContratRepository;

import jakarta.transaction.Transactional;


@Service
public class ContratService {

    @Autowired
    private ContratRepository contratRepository;

    // UC-M4-01 : Créer un contrat
    public Contrat creerContrat(Contrat contrat) {
        contrat.setStatut(Contrat.StatutContrat.BROUILLON);
        return contratRepository.save(contrat);
    }

    // UC-M4-02 : Activer un contrat
    @Transactional
    public Contrat activerContrat(Long id) {
        Contrat contrat = contratRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Contrat introuvable"));

        // RG-M4-01 : Clôturer l'ancien contrat si nécessaire
        contratRepository.findByEmployeIdAndStatut(contrat.getEmployeId(), Contrat.StatutContrat.EN_COURS)
            .ifPresent(ancien -> ancien.setStatut(Contrat.StatutContrat.TERMINE));

        contrat.setStatut(Contrat.StatutContrat.EN_COURS);
        
        // Ici, ajouter l'appel pour mettre à jour M1 et M3 (Structure salariale)
        return contratRepository.save(contrat);
    }

    // RG-M4-07 : Annuler un contrat
    public Contrat annulerContrat(Long id, String motif) {
        if (motif == null || motif.isEmpty()) throw new RuntimeException("Motif obligatoire");
        Contrat contrat = contratRepository.findById(id).get();
        contrat.setStatut(Contrat.StatutContrat.ANNULE);
        return contratRepository.save(contrat);
    }

	public List<Contrat> findAll() {
		return contratRepository.findAll();
	}
}