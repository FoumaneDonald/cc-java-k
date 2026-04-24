package com.kjava.controllers;

import com.kjava.models.Categorie;
import com.kjava.models.Echelon;
import com.kjava.repository.CategorieRepository;
import com.kjava.repository.EchelonRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/echelons")
public class EchelonController {

    @Autowired
    private EchelonRepository echelonRepository;

    @Autowired
    private CategorieRepository categorieRepository;

    @GetMapping
    public List<Echelon> getAll() {
        return echelonRepository.findAll();
    }

    /**
     * Payload attendu :
     * {
     *   "code": "E1",
     *   "indiceSalarial": 350.0,
     *   "categorie": { "id": 2 }
     * }
     */
    @PostMapping
    public ResponseEntity<Echelon> create(@RequestBody Map<String, Object> payload) {
        String code = (String) payload.get("code");
        if (code == null || code.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        if (!payload.containsKey("indiceSalarial")) {
            return ResponseEntity.badRequest().build();
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> catMap = (Map<String, Object>) payload.get("categorie");
        if (catMap == null || catMap.get("id") == null) {
            return ResponseEntity.badRequest().build();
        }
        
        Echelon echelon = new Echelon();
        echelon.setCode(code);
        echelon.setIndiceSalarial(((Number) payload.get("indiceSalarial")).doubleValue());
        
        Long categorieId = ((Number) catMap.get("id")).longValue();
        Categorie categorie = categorieRepository.findById(categorieId)
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable : " + categorieId));
        echelon.setCategorie(categorie);

        return ResponseEntity.ok(echelonRepository.save(echelon));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Echelon> update(@PathVariable Long id,
                                           @RequestBody Map<String, Object> payload) {
        Echelon echelon = echelonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Échelon introuvable : " + id));

        if (payload.containsKey("code")) {
            echelon.setCode((String) payload.get("code"));
        }
        if (payload.containsKey("indiceSalarial")) {
            echelon.setIndiceSalarial(((Number) payload.get("indiceSalarial")).doubleValue());
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> catMap = (Map<String, Object>) payload.get("categorie");
        if (catMap != null && catMap.get("id") != null) {
            Long categorieId = ((Number) catMap.get("id")).longValue();
            Categorie categorie = categorieRepository.findById(categorieId)
                    .orElseThrow(() -> new RuntimeException("Catégorie introuvable : " + categorieId));
            echelon.setCategorie(categorie);
        }

        return ResponseEntity.ok(echelonRepository.save(echelon));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        echelonRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}