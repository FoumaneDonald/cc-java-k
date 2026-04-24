package com.kjava.controllers;

import com.kjava.config.JwtUtil;
import com.kjava.models.Employe;
import com.kjava.services.EmployeService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/employes")
public class EmployeController {

    @Autowired
    private EmployeService employeService;

    @Autowired
    private JwtUtil jwtUtil;

    // DIRECTEUR — créer un employé
    @PostMapping
    public ResponseEntity<Employe> creer(@RequestBody Employe employe) {
        return ResponseEntity.ok(employeService.creer(employe));
    }

    // DIRECTEUR — voir tous
    @GetMapping
    public List<Employe> getAll() {
        return employeService.findAll();
    }

    // CHEF_SERVICE / DIRECTEUR — voir tous les employés (pour sélection dans formulaire contrat)
    @GetMapping("/tous")
    public List<Employe> getTous() {
        return employeService.findAll();
    }

    // CHEF_SERVICE / DIRECTEUR — voir tous les opérants
    @GetMapping("/operants")
    public List<Employe> getOperants() {
        return employeService.findOperants();
    }

    // Tous les authentifiés — voir tous les chefs de service
    @GetMapping("/chefs-service")
    public List<Employe> getChefsService() {
        return employeService.findChefsService();
    }

    // Tous les authentifiés — voir son propre profil
    @GetMapping("/profil")
    public ResponseEntity<Employe> getProfil(HttpServletRequest request) {
        Long id = extractId(request);
        return ResponseEntity.ok(employeService.findById(id));
    }

    // Tous les authentifiés — modifier son nom et famille
    @PutMapping("/profil")
    public ResponseEntity<Employe> modifierProfil(@RequestBody Map<String, String> body,
                                                   HttpServletRequest request) {
        Long id = extractId(request);
        return ResponseEntity.ok(employeService.modifierProfil(id, body.get("nom"), body.get("famille")));
    }

    // DIRECTEUR — modifier un employé
    @PutMapping("/{id}")
    public ResponseEntity<Employe> modifier(@PathVariable Long id,
                                             @RequestBody Employe employe,
                                             HttpServletRequest request) {
        Long directeurId = extractId(request);
        return ResponseEntity.ok(employeService.modifier(id, employe, directeurId));
    }

    // DIRECTEUR — supprimer un employé
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerParDirecteur(@PathVariable Long id,
                                                       HttpServletRequest request) {
        Long directeurId = extractId(request);
        employeService.supprimer(id, directeurId);
        return ResponseEntity.noContent().build();
    }

    // CHEF_SERVICE — supprimer un opérant
    @DeleteMapping("/operants/{id}")
    public ResponseEntity<Void> supprimerOperant(@PathVariable Long id) {
        employeService.supprimerOperant(id);
        return ResponseEntity.noContent().build();
    }

    private Long extractId(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtUtil.extractId(token);
    }
}