package com.kjava.services;

import com.kjava.entities.ParametrePaie;
import com.kjava.repositories.ParametrePaieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ParametrePaieService {

    private final ParametrePaieRepository parametrePaieRepository;

    public ParametrePaie createParametrePaie(ParametrePaie parametrePaie) {
        log.info("Création d'un nouveau paramètre de paie: {}", parametrePaie.getCode());
        
        if (parametrePaieRepository.existsByCodeAndDateEffetAndVersion(
                parametrePaie.getCode(), parametrePaie.getDateEffet(), parametrePaie.getVersion())) {
            throw new IllegalArgumentException("Un paramètre avec ce code, date d'effet et version existe déjà");
        }
        
        return parametrePaieRepository.save(parametrePaie);
    }

    public ParametrePaie updateParametrePaie(UUID id, ParametrePaie parametrePaie) {
        log.info("Mise à jour du paramètre de paie: {}", id);
        
        ParametrePaie existing = parametrePaieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paramètre de paie non trouvé: " + id));
        
        existing.setCode(parametrePaie.getCode());
        existing.setNom(parametrePaie.getNom());
        existing.setValeur(parametrePaie.getValeur());
        existing.setDateEffet(parametrePaie.getDateEffet());
        existing.setVersion(parametrePaie.getVersion());
        
        return parametrePaieRepository.save(existing);
    }

    @Transactional(readOnly = true)
    public Optional<ParametrePaie> getParametrePaieById(UUID id) {
        return parametrePaieRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<ParametrePaie> getAllParametresPaie() {
        return parametrePaieRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<ParametrePaie> getLatestParametreByCodeAndDate(String code, LocalDate dateEffet) {
        return parametrePaieRepository.findLatestVersionByCodeAndDate(code, dateEffet);
    }

    @Transactional(readOnly = true)
    public List<ParametrePaie> getParametresByCode(String code) {
        return parametrePaieRepository.findByCodeOrderByDateEffetDescVersionDesc(code);
    }

    @Transactional(readOnly = true)
    public List<ParametrePaie> getParametresByDateRange(LocalDate startDate, LocalDate endDate) {
        return parametrePaieRepository.findByDateEffetBetweenOrderByDateEffetDesc(startDate, endDate);
    }

    public void deleteParametrePaie(UUID id) {
        log.info("Suppression du paramètre de paie: {}", id);
        
        if (!parametrePaieRepository.existsById(id)) {
            throw new IllegalArgumentException("Paramètre de paie non trouvé: " + id);
        }
        
        parametrePaieRepository.deleteById(id);
    }

    public ParametrePaie createNewVersion(String code, String nom, BigDecimal valeur, LocalDate dateEffet) {
        log.info("Création d'une nouvelle version du paramètre: {}", code);
        
        List<ParametrePaie> existingVersions = parametrePaieRepository.findByCodeOrderByDateEffetDescVersionDesc(code);
        Integer newVersion = existingVersions.isEmpty() ? 1 : 
            existingVersions.get(0).getVersion() + 1;
        
        ParametrePaie newParametre = ParametrePaie.builder()
                .code(code)
                .nom(nom)
                .valeur(valeur)
                .dateEffet(dateEffet)
                .version(newVersion)
                .build();
        
        return parametrePaieRepository.save(newParametre);
    }
}
