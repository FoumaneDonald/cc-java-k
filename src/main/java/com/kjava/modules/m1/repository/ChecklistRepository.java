package com.kjava.modules.m1.repository;

import com.kjava.modules.m1.model.Checklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.Optional;

@Repository
public interface ChecklistRepository extends JpaRepository<Checklist, UUID> {
    Optional<Checklist> findByEmployeeId(UUID employeeId);
}
