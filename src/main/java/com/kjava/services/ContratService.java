package com.kjava.services;

import com.kjava.models.Contrat;
import com.kjava.models.Echelon;
import com.kjava.models.Employe;
import com.kjava.repository.CategorieRepository;
import com.kjava.repository.ContratRepository;
import com.kjava.repository.EchelonRepository;
import com.kjava.repository.EmployeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;

@Service
public class ContratService {

    @Autowired
    private ContratRepository contratRepository;

    @Autowired
    private EmployeRepository employeRepository;

    @Autowired
    private EchelonRepository echelonRepository;

    @Autowired
    private CategorieRepository categorieRepository;

    // CHEF_SERVICE — créer un contrat pour n'importe quel employé
    public Contrat creerContrat(Contrat contrat, Long chefServiceId) {

        // Résoudre l'employé (opérant) depuis la base de données
        if (contrat.getOperant() == null || contrat.getOperant().getId() == null) {
            throw new RuntimeException("L'employé (opérant) est obligatoire");
        }
        Employe operant = employeRepository.findById(contrat.getOperant().getId())
                .orElseThrow(() -> new RuntimeException("Employé introuvable avec l'id : " + contrat.getOperant().getId()));

        // Vérifier qu'il n'a pas déjà un contrat actif
        contratRepository.findByOperantAndStatutNot(operant, Contrat.StatutContrat.ANNULE)
            .ifPresent(c -> {
                if (c.getStatut() != Contrat.StatutContrat.TERMINE) {
                    throw new RuntimeException("Cet employé a déjà un contrat actif");
                }
            });

        // Résoudre le chef de service
        Employe chefService = employeRepository.findById(chefServiceId)
                .orElseThrow(() -> new RuntimeException("Chef de service introuvable"));

        // Résoudre l'échelon depuis la base de données
        if (contrat.getEchelon() == null || contrat.getEchelon().getId() == null) {
            throw new RuntimeException("L'échelon est obligatoire");
        }
        Echelon echelon = echelonRepository.findById(contrat.getEchelon().getId())
                .orElseThrow(() -> new RuntimeException("Échelon introuvable avec l'id : " + contrat.getEchelon().getId()));

        contrat.setOperant(operant);
        contrat.setChefService(chefService);
        contrat.setEchelon(echelon);

        // La catégorie est portée par l'échelon, on peut aussi la forcer
        if (contrat.getCategorie() == null || contrat.getCategorie().getId() == null) {
            contrat.setCategorie(echelon.getCategorie());
        } else {
            contrat.setCategorie(categorieRepository.findById(contrat.getCategorie().getId())
                    .orElseThrow(() -> new RuntimeException("Catégorie introuvable")));
        }

        contrat.setStatut(Contrat.StatutContrat.BROUILLON);
        return contratRepository.save(contrat);
    }

    // DIRECTEUR — valider (activer) un contrat
    @Transactional
    public Contrat activerContrat(Long id) {
        Contrat contrat = findById(id);
        contrat.setStatut(Contrat.StatutContrat.EN_COURS);
        return contratRepository.save(contrat);
    }

    // DIRECTEUR — annuler un contrat
    public Contrat annulerContrat(Long id, String motif) {
        if (motif == null || motif.isEmpty()) throw new RuntimeException("Motif obligatoire");
        Contrat contrat = findById(id);
        contrat.setStatut(Contrat.StatutContrat.ANNULE);
        return contratRepository.save(contrat);
    }

    // DIRECTEUR — terminer un contrat
    public Contrat terminerContrat(Long id) {
        Contrat contrat = findById(id);
        if (contrat.getStatut() != Contrat.StatutContrat.EN_COURS) {
            throw new RuntimeException("Seul un contrat EN_COURS peut être terminé");
        }
        contrat.setStatut(Contrat.StatutContrat.TERMINE);
        return contratRepository.save(contrat);
    }

    public List<Contrat> findAll() {
        return contratRepository.findAll();
    }

    public List<Contrat> findByOperantId(Long operantId) {
        return contratRepository.findByOperantId(operantId);
    }

    private Contrat findById(Long id) {
        return contratRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contrat introuvable"));
    }
}