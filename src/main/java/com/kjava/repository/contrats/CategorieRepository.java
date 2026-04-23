package com.kjava.repository.contrats;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kjava.models.contrats.Categorie;

@Repository
public interface CategorieRepository extends JpaRepository<Categorie, Long> {}