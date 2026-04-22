package com.kjava.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/contrats")
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

    @PutMapping("/{id}/annuler")
    public ResponseEntity<Contrat> annuler(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String motif = body.get("motif");
        return ResponseEntity.ok(contratService.annulerContrat(id, motif));
    }

    @GetMapping
    public List<Contrat> getAll() {
        return contratService.findAll();
    }
}