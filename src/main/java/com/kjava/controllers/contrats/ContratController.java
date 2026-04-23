package com.kjava.controllers.contrats;

import com.kjava.config.contrats.JwtUtil;
import com.kjava.models.contrats.Contrat;
import com.kjava.models.contrats.Echelon;
import com.kjava.models.contrats.Employe;
import com.kjava.repository.contrats.EchelonRepository;
import com.kjava.repository.contrats.EmployeRepository;
import com.kjava.services.contrats.ContratService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/contrats")
public class ContratController {

    @Autowired
    private ContratService contratService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EchelonRepository echelonRepository;

    @Autowired
    private EmployeRepository employeRepository;

    // CHEF_SERVICE — créer un contrat
    @PostMapping
    public ResponseEntity<Contrat> create(@RequestBody Map<String, Object> payload,
                                           HttpServletRequest request) {
        try {
            Long chefId = extractId(request);
            
            // Valider les champs obligatoires
            if (!payload.containsKey("employeId") || !payload.containsKey("typeContrat") || 
                !payload.containsKey("dateDebut")) {
                return ResponseEntity.badRequest().build();
            }
            
            // Créer le contrat
            Contrat contrat = new Contrat();
            
            // Employé (operant)
            Long employeId = ((Number) payload.get("employeId")).longValue();
            Employe operant = employeRepository.findById(employeId)
                .orElseThrow(() -> new RuntimeException("Employé introuvable : " + employeId));
            contrat.setOperant(operant);
            
            // Chef de service
            Employe chefService = employeRepository.findById(chefId)
                .orElseThrow(() -> new RuntimeException("Chef de service introuvable : " + chefId));
            contrat.setChefService(chefService);
            
            // Type et dates
            contrat.setTypeContrat((String) payload.get("typeContrat"));
            contrat.setDateDebut(LocalDate.parse((String) payload.get("dateDebut")));
            if (payload.containsKey("dateFin") && payload.get("dateFin") != null) {
                contrat.setDateFin(LocalDate.parse((String) payload.get("dateFin")));
            }
            
            // Échelon (optionnel)
            if (payload.containsKey("echelon") && payload.get("echelon") != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> echelonMap = (Map<String, Object>) payload.get("echelon");
                if (echelonMap.containsKey("id")) {
                    Long echelonId = ((Number) echelonMap.get("id")).longValue();
                    Echelon echelon = echelonRepository.findById(echelonId)
                        .orElseThrow(() -> new RuntimeException("Échelon introuvable : " + echelonId));
                    contrat.setEchelon(echelon);
                    // La catégorie est déduite de l'échelon
                    contrat.setCategorie(echelon.getCategorie());
                }
            }
            
            return ResponseEntity.ok(contratService.creerContrat(contrat, chefId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // DIRECTEUR — valider un contrat
    @PutMapping("/{id}/activer")
    public ResponseEntity<Contrat> activer(@PathVariable Long id) {
        return ResponseEntity.ok(contratService.activerContrat(id));
    }

    // DIRECTEUR — annuler un contrat
    @PutMapping("/{id}/annuler")
    public ResponseEntity<Contrat> annuler(@PathVariable Long id,
                                            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(contratService.annulerContrat(id, body.get("motif")));
    }

    // DIRECTEUR — terminer un contrat
    @PutMapping("/{id}/terminer")
    public ResponseEntity<Contrat> terminer(@PathVariable Long id) {
        return ResponseEntity.ok(contratService.terminerContrat(id));
    }

    // DIRECTEUR / CHEF_SERVICE — voir tous
    @GetMapping
    public List<Contrat> getAll() {
        return contratService.findAll();
    }

    // OPERANT — voir son contrat
    @GetMapping("/mon-contrat")
    public ResponseEntity<List<Contrat>> getMonContrat(HttpServletRequest request) {
        Long id = extractId(request);
        return ResponseEntity.ok(contratService.findByOperantId(id));
    }

    private Long extractId(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtUtil.extractId(token);
    }
}