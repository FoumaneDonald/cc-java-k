package com.kjava.controllers;

import com.kjava.config.JwtUtil;
import com.kjava.models.Categorie;
import com.kjava.repository.CategorieRepository;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategorieController {

    @Autowired
    private CategorieRepository categorieRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    public List<Categorie> getAll() { return categorieRepository.findAll(); }

    @PostMapping
    public ResponseEntity<Categorie> create(@RequestBody Categorie categorie) {
        if (categorie.getCode() == null || categorie.getCode().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (categorie.getLibelle() == null || categorie.getLibelle().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Categorie saved = categorieRepository.save(categorie);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categorie> update(@PathVariable Long id, @RequestBody Categorie data) {
        Categorie c = categorieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable : " + id));
        
        if (data.getCode() != null && !data.getCode().trim().isEmpty()) {
            c.setCode(data.getCode());
        }
        if (data.getLibelle() != null && !data.getLibelle().trim().isEmpty()) {
            c.setLibelle(data.getLibelle());
        }
        Categorie saved = categorieRepository.save(c);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categorieRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}