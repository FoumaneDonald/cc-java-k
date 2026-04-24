package com.kjava.controllers;

import com.kjava.config.JwtUtil;
import com.kjava.models.Contrat;
import com.kjava.models.Echelon;
import com.kjava.models.Employe;
import com.kjava.repository.CategorieRepository;
import com.kjava.repository.EchelonRepository;
import com.kjava.repository.EmployeRepository;
import com.kjava.services.ContratService;

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
    private CategorieRepository categorieRepository;

    @Autowired
    private EmployeRepository employeRepository;

    // CHEF_SERVICE — créer un contrat
    // Payload attendu :
    // {
    //   "operant":   { "id": 5 },
    //   "typeContrat": "CDD",
    //   "dateDebut": "2024-01-01",
    //   "dateFin":   "2024-12-31",   // optionnel
    //   "categorie": { "id": 2 },
    //   "echelon":   { "id": 3 }
    // }
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> payload,
                                    HttpServletRequest request) {
        try {
            Long chefId = extractId(request);

            // ── 1. Opérant ──────────────────────────────────────────────
            @SuppressWarnings("unchecked")
            Map<String, Object> operantMap = (Map<String, Object>) payload.get("operant");
            if (operantMap == null || operantMap.get("id") == null) {
                return ResponseEntity.badRequest().body("L'opérant est obligatoire");
            }
            Long operantId = ((Number) operantMap.get("id")).longValue();
            Employe operant = employeRepository.findById(operantId)
                .orElseThrow(() -> new RuntimeException("Employé introuvable : " + operantId));

            // ── 2. Type et dates ─────────────────────────────────────────
            String typeContrat = (String) payload.get("typeContrat");
            if (typeContrat == null || typeContrat.isBlank()) {
                return ResponseEntity.badRequest().body("Le type de contrat est obligatoire");
            }

            String dateDebutStr = (String) payload.get("dateDebut");
            if (dateDebutStr == null || dateDebutStr.isBlank()) {
                return ResponseEntity.badRequest().body("La date de début est obligatoire");
            }

            // ── 3. Échelon ───────────────────────────────────────────────
            @SuppressWarnings("unchecked")
            Map<String, Object> echelonMap = (Map<String, Object>) payload.get("echelon");
            if (echelonMap == null || echelonMap.get("id") == null) {
                return ResponseEntity.badRequest().body("L'échelon est obligatoire");
            }
            Long echelonId = ((Number) echelonMap.get("id")).longValue();
            Echelon echelon = echelonRepository.findById(echelonId)
                .orElseThrow(() -> new RuntimeException("Échelon introuvable : " + echelonId));

            // ── 4. Construire l'objet Contrat ────────────────────────────
            Contrat contrat = new Contrat();
            contrat.setOperant(operant);
            contrat.setTypeContrat(typeContrat);
            contrat.setDateDebut(LocalDate.parse(dateDebutStr));

            String dateFinStr = (String) payload.get("dateFin");
            if (dateFinStr != null && !dateFinStr.isBlank()) {
                contrat.setDateFin(LocalDate.parse(dateFinStr));
            }

            contrat.setEchelon(echelon);
            // La catégorie est portée par l'échelon (déduite automatiquement)
            contrat.setCategorie(echelon.getCategorie());

            // ── 5. Sauvegarder via le service ────────────────────────────
            return ResponseEntity.ok(contratService.creerContrat(contrat, chefId));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erreur interne : " + e.getMessage());
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

    // DIRECTEUR / CHEF_SERVICE — voir tous les contrats
    @GetMapping
    public List<Contrat> getAll() {
        return contratService.findAll();
    }

    // OPERANT — voir ses propres contrats
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