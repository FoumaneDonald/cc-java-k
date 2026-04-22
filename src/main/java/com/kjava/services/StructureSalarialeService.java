package com.kjava.services;

import com.kjava.entities.StructureSalariale;
import com.kjava.repositories.StructureSalarialeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StructureSalarialeService {

    private final StructureSalarialeRepository structureSalarialeRepository;

    public StructureSalariale createStructureSalariale(StructureSalariale structureSalariale) {
        log.info("Création d'une nouvelle structure salariale: {}", structureSalariale.getCode());
        
        if (structureSalarialeRepository.existsByCode(structureSalariale.getCode())) {
            throw new IllegalArgumentException("Une structure salariale avec ce code existe déjà: " + structureSalariale.getCode());
        }
        
        return structureSalarialeRepository.save(structureSalariale);
    }

    public StructureSalariale updateStructureSalariale(UUID id, StructureSalariale structureSalariale) {
        log.info("Mise à jour de la structure salariale: {}", id);
        
        StructureSalariale existing = structureSalarialeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Structure salariale non trouvée: " + id));
        
        if (!existing.getCode().equals(structureSalariale.getCode()) && 
            structureSalarialeRepository.existsByCodeAndIdNot(structureSalariale.getCode(), id)) {
            throw new IllegalArgumentException("Une structure salariale avec ce code existe déjà: " + structureSalariale.getCode());
        }
        
        existing.setCode(structureSalariale.getCode());
        existing.setNom(structureSalariale.getNom());
        existing.setCategoryId(structureSalariale.getCategoryId());
        existing.setReglesSalariales(structureSalariale.getReglesSalariales());
        
        return structureSalarialeRepository.save(existing);
    }

    @Transactional(readOnly = true)
    public Optional<StructureSalariale> getStructureSalarialeById(UUID id) {
        return structureSalarialeRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<StructureSalariale> getStructureSalarialeByIdWithRegles(UUID id) {
        return structureSalarialeRepository.findByIdWithRegles(id);
    }

    @Transactional(readOnly = true)
    public Optional<StructureSalariale> getStructureSalarialeByCode(String code) {
        return structureSalarialeRepository.findByCode(code);
    }

    @Transactional(readOnly = true)
    public List<StructureSalariale> getAllStructuresSalariales() {
        return structureSalarialeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<StructureSalariale> getStructuresByCategoryId(UUID categoryId) {
        return structureSalarialeRepository.findByCategoryId(categoryId);
    }

    @Transactional(readOnly = true)
    public List<StructureSalariale> getStructuresByCategoryIdOrderByNom(UUID categoryId) {
        return structureSalarialeRepository.findByCategoryIdOrderByNom(categoryId);
    }

    @Transactional(readOnly = true)
    public List<StructureSalariale> getStructuresByRegleSalarialeId(UUID regleSalarialeId) {
        return structureSalarialeRepository.findByRegleSalarialeId(regleSalarialeId);
    }

    public void deleteStructureSalariale(UUID id) {
        log.info("Suppression de la structure salariale: {}", id);
        
        if (!structureSalarialeRepository.existsById(id)) {
            throw new IllegalArgumentException("Structure salariale non trouvée: " + id);
        }
        
        structureSalarialeRepository.deleteById(id);
    }

    public StructureSalariale addRegleToStructure(UUID structureId, UUID regleId) {
        log.info("Ajout de la règle {} à la structure {}", regleId, structureId);
        
        StructureSalariale structure = structureSalarialeRepository.findById(structureId)
                .orElseThrow(() -> new IllegalArgumentException("Structure salariale non trouvée: " + structureId));
        
        StructureSalariale structureWithRegles = structureSalarialeRepository.findByIdWithRegles(structureId)
                .orElse(structure);
        
        if (structureWithRegles.getReglesSalariales() != null && 
            structureWithRegles.getReglesSalariales().stream().anyMatch(r -> r.getId().equals(regleId))) {
            throw new IllegalArgumentException("La règle est déjà associée à cette structure");
        }
        
        return structureSalarialeRepository.save(structureWithRegles);
    }

    public StructureSalariale removeRegleFromStructure(UUID structureId, UUID regleId) {
        log.info("Retrait de la règle {} de la structure {}", regleId, structureId);
        
        StructureSalariale structure = structureSalarialeRepository.findByIdWithRegles(structureId)
                .orElseThrow(() -> new IllegalArgumentException("Structure salariale non trouvée: " + structureId));
        
        if (structure.getReglesSalariales() != null) {
            structure.getReglesSalariales().removeIf(r -> r.getId().equals(regleId));
        }
        
        return structureSalarialeRepository.save(structure);
    }
}
