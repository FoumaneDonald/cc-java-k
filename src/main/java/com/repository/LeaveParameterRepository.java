package com.repository;

import com.entities.LeaveParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LeaveParameterRepository extends JpaRepository<LeaveParameter, UUID> {
    Optional<LeaveParameter> findByActiveTrue();
}
