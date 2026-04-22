package com.kjava.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kjava.models.Contrat;
import com.kjava.services.ContratService;

@RestController
@RequestMapping("/api/contrats")
@CrossOrigin(origins = "http://localhost:3000") // Pour autoriser React
public class ContratController {

    @Autowired
    private ContratService contratService;

    @PostMapping
    public ResponseEntity<Contrat> create(@RequestBody Contrat contrat) {
        return ResponseEntity.ok(contratService.creerContrat(contrat));
    }

    @PutMapping("/{id}/activer")
    public ResponseEntity<Contrat> activer(@PathVariable Long id) {
        return ResponseEntity.ok(contratService.activerContrat(id));
    }

    @GetMapping
    public List<Contrat> getAll() {
        return contratService.findAll(); // à ajouter dans le service
    }
}