package com.kjava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kjava.models.Categorie;

@Repository
public interface CategorieRepository extends JpaRepository<Categorie, Long> {}