package com.repository;

import com.entities.Convention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ConventionRepository extends JpaRepository<Convention, UUID> {
}
