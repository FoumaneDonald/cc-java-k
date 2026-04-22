package com.kjava.services;

import com.kjava.entities.RegleSalariale;
import com.kjava.enums.TypeRegle;
import com.kjava.repositories.RegleSalarialeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RegleSalarialeService {

    private final RegleSalarialeRepository regleSalarialeRepository;

    public RegleSalariale createRegleSalariale(RegleSalariale regleSalariale) {
        log.info("Création d'une nouvelle règle salariale: {}", regleSalariale.getCode());
        
        if (regleSalarialeRepository.existsByCode(regleSalariale.getCode())) {
            throw new IllegalArgumentException("Une règle salariale avec ce code existe déjà: " + regleSalariale.getCode());
        }
        
        return regleSalarialeRepository.save(regleSalariale);
    }

    public RegleSalariale updateRegleSalariale(UUID id, RegleSalariale regleSalariale) {
        log.info("Mise à jour de la règle salariale: {}", id);
        
        RegleSalariale existing = regleSalarialeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Règle salariale non trouvée: " + id));
        
        if (!existing.getCode().equals(regleSalariale.getCode()) && 
            regleSalarialeRepository.existsByCodeAndIdNot(regleSalariale.getCode(), id)) {
            throw new IllegalArgumentException("Une règle salariale avec ce code existe déjà: " + regleSalariale.getCode());
        }
        
        existing.setCode(regleSalariale.getCode());
        existing.setNom(regleSalariale.getNom());
        existing.setType(regleSalariale.getType());
        existing.setFormule(regleSalariale.getFormule());
        existing.setPlafond(regleSalariale.getPlafond());
        existing.setIsRecurrente(regleSalariale.getIsRecurrente());
        
        return regleSalarialeRepository.save(existing);
    }

    @Transactional(readOnly = true)
    public Optional<RegleSalariale> getRegleSalarialeById(UUID id) {
        return regleSalarialeRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<RegleSalariale> getRegleSalarialeByCode(String code) {
        return regleSalarialeRepository.findByCode(code);
    }

    @Transactional(readOnly = true)
    public List<RegleSalariale> getAllReglesSalariales() {
        return regleSalarialeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<RegleSalariale> getReglesByType(TypeRegle type) {
        return regleSalarialeRepository.findByType(type);
    }

    @Transactional(readOnly = true)
    public List<RegleSalariale> getReglesRecurrentes() {
        return regleSalarialeRepository.findByIsRecurrenteTrue();
    }

    @Transactional(readOnly = true)
    public List<RegleSalariale> getReglesNonRecurrentes() {
        return regleSalarialeRepository.findByIsRecurrenteFalse();
    }

    @Transactional(readOnly = true)
    public List<RegleSalariale> getReglesWithPlafond() {
        return regleSalarialeRepository.findWithPlafond();
    }

    @Transactional(readOnly = true)
    public List<RegleSalariale> getReglesByTypeAndRecurrente(TypeRegle type, Boolean isRecurrente) {
        return regleSalarialeRepository.findByTypeAndIsRecurrente(type, isRecurrente);
    }

    public void deleteRegleSalariale(UUID id) {
        log.info("Suppression de la règle salariale: {}", id);
        
        if (!regleSalarialeRepository.existsById(id)) {
            throw new IllegalArgumentException("Règle salariale non trouvée: " + id);
        }
        
        regleSalarialeRepository.deleteById(id);
    }

    public RegleSalariale createPrime(String code, String nom, String formule, BigDecimal plafond) {
        return createRegleSalariale(RegleSalariale.builder()
                .code(code)
                .nom(nom)
                .type(TypeRegle.PRIME)
                .formule(formule)
                .plafond(plafond)
                .isRecurrente(true)
                .build());
    }

    public RegleSalariale createIndemnite(String code, String nom, String formule, BigDecimal plafond) {
        return createRegleSalariale(RegleSalariale.builder()
                .code(code)
                .nom(nom)
                .type(TypeRegle.INDEMNITE)
                .formule(formule)
                .plafond(plafond)
                .isRecurrente(false)
                .build());
    }

    public RegleSalariale createRetenue(String code, String nom, String formule, BigDecimal plafond) {
        return createRegleSalariale(RegleSalariale.builder()
                .code(code)
                .nom(nom)
                .type(TypeRegle.RETENUE)
                .formule(formule)
                .plafond(plafond)
                .isRecurrente(true)
                .build());
    }

    public RegleSalariale createChargeSalariale(String code, String nom, String formule, BigDecimal plafond) {
        return createRegleSalariale(RegleSalariale.builder()
                .code(code)
                .nom(nom)
                .type(TypeRegle.CHARGE_SALARIALE)
                .formule(formule)
                .plafond(plafond)
                .isRecurrente(true)
                .build());
    }

    public RegleSalariale createAbsence(String code, String nom, String formule) {
        return createRegleSalariale(RegleSalariale.builder()
                .code(code)
                .nom(nom)
                .type(TypeRegle.ABSENCE)
                .formule(formule)
                .isRecurrente(false)
                .build());
    }
}
