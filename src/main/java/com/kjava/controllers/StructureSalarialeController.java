package com.kjava.controllers;

import com.kjava.entities.StructureSalariale;
import com.kjava.services.StructureSalarialeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/structures-salariales")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class StructureSalarialeController {

    private final StructureSalarialeService structureSalarialeService;

    @PostMapping
    public ResponseEntity<StructureSalariale> createStructureSalariale(@RequestBody StructureSalariale structureSalariale) {
        log.info("POST /api/v1/structures-salariales - Création structure: {}", structureSalariale.getCode());
        StructureSalariale created = structureSalarialeService.createStructureSalariale(structureSalariale);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StructureSalariale> updateStructureSalariale(
            @PathVariable UUID id, 
            @RequestBody StructureSalariale structureSalariale) {
        log.info("PUT /api/v1/structures-salariales/{} - Mise à jour structure", id);
        StructureSalariale updated = structureSalarialeService.updateStructureSalariale(id, structureSalariale);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StructureSalariale> getStructureSalarialeById(@PathVariable UUID id) {
        log.info("GET /api/v1/structures-salariales/{}", id);
        Optional<StructureSalariale> structure = structureSalarialeService.getStructureSalarialeById(id);
        return structure.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/with-regles")
    public ResponseEntity<Optional<StructureSalariale>> getStructureSalarialeByIdWithRegles(@PathVariable UUID id) {
        log.info("GET /api/v1/structures-salariales/{}/with-regles", id);
        Optional<StructureSalariale> structure = structureSalarialeService.getStructureSalarialeByIdWithRegles(id);
        return ResponseEntity.ok(structure);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<Optional<StructureSalariale>> getStructureSalarialeByCode(@PathVariable String code) {
        log.info("GET /api/v1/structures-salariales/code/{}", code);
        Optional<StructureSalariale> structure = structureSalarialeService.getStructureSalarialeByCode(code);
        return ResponseEntity.ok(structure);
    }

    @GetMapping
    public ResponseEntity<List<StructureSalariale>> getAllStructuresSalariales() {
        log.info("GET /api/v1/structures-salariales");
        List<StructureSalariale> structures = structureSalarialeService.getAllStructuresSalariales();
        return ResponseEntity.ok(structures);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<StructureSalariale>> getStructuresByCategoryId(@PathVariable UUID categoryId) {
        log.info("GET /api/v1/structures-salariales/category/{}", categoryId);
        List<StructureSalariale> structures = structureSalarialeService.getStructuresByCategoryId(categoryId);
        return ResponseEntity.ok(structures);
    }

    @GetMapping("/category/{categoryId}/ordered")
    public ResponseEntity<List<StructureSalariale>> getStructuresByCategoryIdOrderByNom(@PathVariable UUID categoryId) {
        log.info("GET /api/v1/structures-salariales/category/{}/ordered", categoryId);
        List<StructureSalariale> structures = structureSalarialeService.getStructuresByCategoryIdOrderByNom(categoryId);
        return ResponseEntity.ok(structures);
    }

    @GetMapping("/regle/{regleId}")
    public ResponseEntity<List<StructureSalariale>> getStructuresByRegleSalarialeId(@PathVariable UUID regleId) {
        log.info("GET /api/v1/structures-salariales/regle/{}", regleId);
        List<StructureSalariale> structures = structureSalarialeService.getStructuresByRegleSalarialeId(regleId);
        return ResponseEntity.ok(structures);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStructureSalariale(@PathVariable UUID id) {
        log.info("DELETE /api/v1/structures-salariales/{}", id);
        structureSalarialeService.deleteStructureSalariale(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{structureId}/regles/{regleId}")
    public ResponseEntity<StructureSalariale> addRegleToStructure(
            @PathVariable UUID structureId,
            @PathVariable UUID regleId) {
        log.info("POST /api/v1/structures-salariales/{}/regles/{} - Ajout règle à structure", structureId, regleId);
        StructureSalariale updated = structureSalarialeService.addRegleToStructure(structureId, regleId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{structureId}/regles/{regleId}")
    public ResponseEntity<StructureSalariale> removeRegleFromStructure(
            @PathVariable UUID structureId,
            @PathVariable UUID regleId) {
        log.info("DELETE /api/v1/structures-salariales/{}/regles/{} - Retrait règle de structure", structureId, regleId);
        StructureSalariale updated = structureSalarialeService.removeRegleFromStructure(structureId, regleId);
        return ResponseEntity.ok(updated);
    }
}
