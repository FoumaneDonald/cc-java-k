package com.kjava.modules.m1.service;

import com.kjava.modules.m1.enums.ChecklistStatus;
import com.kjava.modules.m1.model.Checklist;
import com.kjava.modules.m1.repository.ChecklistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChecklistService {
    private final ChecklistRepository checklistRepository;

    public Checklist updateChecklistItems(UUID employeeId, List<String> items) {
        Checklist checklist = checklistRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Checklist non trouvée"));
        
        if (checklist.getStatus() == ChecklistStatus.ANNULE) {
            throw new RuntimeException("Une checklist annulée est en lecture seule.");
        }

        checklist.setCompletedItems(items);
        return checklistRepository.save(checklist);
    }

    public Checklist validateChecklist(UUID employeeId) {
        Checklist checklist = checklistRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Checklist non trouvée"));
        
        // RG-M1-15: La checklist ne peut passer au statut Réceptionné que si tous les éléments obligatoires sont cochés.
        // Pour l'exemple, on considère que "contrat" et "id" sont obligatoires.
        if (!checklist.getCompletedItems().contains("contrat") || !checklist.getCompletedItems().contains("id")) {
            throw new RuntimeException("Éléments obligatoires manquants dans la checklist.");
        }

        checklist.setStatus(ChecklistStatus.RECEPTIONNE);
        return checklistRepository.save(checklist);
    }
}
