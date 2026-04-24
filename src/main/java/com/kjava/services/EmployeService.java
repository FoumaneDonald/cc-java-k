package com.kjava.services;

import com.kjava.models.Employe;
import com.kjava.repository.EmployeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeService {

    @Autowired
    private EmployeRepository employeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Employe creer(Employe employe) {
        employe.setMotDePasse(passwordEncoder.encode(employe.getMotDePasse()));
        return employeRepository.save(employe);
    }

    public List<Employe> findAll() {
        return employeRepository.findAll();
    }

    public List<Employe> findOperants() {
        return employeRepository.findByRole(Employe.Role.OPERANT);
    }

    public List<Employe> findChefsService() {
        return employeRepository.findByRole(Employe.Role.CHEF_SERVICE);
    }

    public Employe findById(Long id) {
        return employeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employé introuvable"));
    }

    // Directeur peut modifier tous sauf autres directeurs
    public Employe modifier(Long id, Employe data, Long directeurId) {
        Employe employe = findById(id);
        if (employe.getRole() == Employe.Role.DIRECTEUR && !employe.getId().equals(directeurId)) {
            throw new RuntimeException("Impossible de modifier un autre directeur");
        }
        employe.setNom(data.getNom());
        employe.setAge(data.getAge());
        employe.setFamille(data.getFamille());
        employe.setRole(data.getRole());
        return employeRepository.save(employe);
    }

    // Opérant peut modifier uniquement son nom et famille
    public Employe modifierProfil(Long id, String nom, String famille) {
        Employe employe = findById(id);
        employe.setNom(nom);
        employe.setFamille(famille);
        return employeRepository.save(employe);
    }

    public void supprimer(Long id, Long directeurId) {
        Employe employe = findById(id);
        if (employe.getRole() == Employe.Role.DIRECTEUR && !employe.getId().equals(directeurId)) {
            throw new RuntimeException("Impossible de supprimer un autre directeur");
        }
        employeRepository.deleteById(id);
    }

    // Chef de service peut supprimer uniquement les opérants
    public void supprimerOperant(Long id) {
        Employe employe = findById(id);
        if (employe.getRole() != Employe.Role.OPERANT) {
            throw new RuntimeException("Vous ne pouvez supprimer que des opérants");
        }
        employeRepository.deleteById(id);
    }
}