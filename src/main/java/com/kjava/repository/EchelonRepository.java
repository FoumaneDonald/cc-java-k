package com.kjava.repository;

import com.kjava.models.Echelon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EchelonRepository extends JpaRepository<Echelon, Long> {

    // Filtrer les échelons par catégorie (utilisé dans le formulaire de contrat)
    List<Echelon> findByCategorieId(Long categorieId);
}