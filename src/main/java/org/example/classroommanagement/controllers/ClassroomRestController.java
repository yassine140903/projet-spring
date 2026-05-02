package org.example.classroommanagement.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.example.classroommanagement.entities.Classe;
import org.example.classroommanagement.entities.CoursClassroom;
import org.example.classroommanagement.entities.Niveau;
import org.example.classroommanagement.entities.Specialite;
import org.example.classroommanagement.entities.Utilisateur;
import org.example.classroommanagement.services.IClassroomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for classroom management operations.
 * Exposes HTTP endpoints for managing users, classes, and courses.
 * 
 * Validates Requirements: 1.5, 2.6, 3.7, 4.3, 5.3, 6.3, 8.5, 10.4, 12.2
 */
@RestController
@RequestMapping("/api/classroom")
@Tag(name = "Classroom Management", description = "APIs for managing users, classes, and courses")
public class ClassroomRestController {
    
    private final IClassroomService classroomService;
    
    /**
     * Constructor injection for IClassroomService.
     * 
     * @param classroomService the classroom service implementation
     */
    public ClassroomRestController(IClassroomService classroomService) {
        this.classroomService = classroomService;
    }
    
    /**
     * Endpoint 1: Add a new user to the system.
     * 
     * @param utilisateur the user to add (must have prenom, nom, and password)
     * @return ResponseEntity with created user and 201 Created status
     * 
     * Validates Requirement: 1.5
     */
    @PostMapping("/utilisateurs")
    @Operation(summary = "Add a new user", description = "Creates a new user in the system with prenom, nom, and password")
    public ResponseEntity<Utilisateur> ajouterUtilisateur(@RequestBody Utilisateur utilisateur) {
        Utilisateur createdUser = classroomService.ajouterUtilisateur(utilisateur);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
    
    /**
     * Endpoint 2: Add a new class to the system.
     * 
     * @param classe the class to add (must have titre and niveau)
     * @return ResponseEntity with created class and 201 Created status
     * 
     * Validates Requirement: 2.6
     */
    @PostMapping("/classes")
    @Operation(summary = "Add a new class", description = "Creates a new class in the system with titre and niveau")
    public ResponseEntity<Classe> ajouterClasse(@RequestBody Classe classe) {
        Classe createdClasse = classroomService.ajouterClasse(classe);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdClasse);
    }
    
    /**
     * Endpoint 3: Add a new course to a class.
     * 
     * @param cours the course to add (must have nom, nbHeures, and specialite)
     * @param codeClasse the ID of the class to associate the course with
     * @return ResponseEntity with created course and 201 Created status, or 404 if class not found
     * 
     * Validates Requirement: 3.7
     */
    @PostMapping("/cours/{codeClasse}")
    @Operation(summary = "Add a new course to a class", description = "Creates a new course and associates it with the specified class")
    public ResponseEntity<CoursClassroom> ajouterCoursClassroom(
            @RequestBody CoursClassroom cours,
            @PathVariable Integer codeClasse) {
        try {
            CoursClassroom createdCours = classroomService.ajouterCoursClassroom(cours, codeClasse);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCours);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    /**
     * Endpoint 4: Assign a user to a class.
     * 
     * @param idUtilisateur the ID of the user to assign
     * @param codeClasse the ID of the class to assign the user to
     * @return ResponseEntity with 204 No Content status, or 404 if user or class not found
     * 
     * Validates Requirement: 4.3
     */
    @PutMapping("/utilisateurs/{idUtilisateur}/classes/{codeClasse}")
    @Operation(summary = "Assign a user to a class", description = "Updates the user's class assignment")
    public ResponseEntity<Void> affecterUtilisateurClasse(
            @PathVariable Integer idUtilisateur,
            @PathVariable Integer codeClasse) {
        try {
            classroomService.affecterUtilisateurClasse(idUtilisateur, codeClasse);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    /**
     * Endpoint 5: Count users by academic level.
     * 
     * @param niveau the academic level to filter by
     * @return ResponseEntity with user count and 200 OK status
     * 
     * Validates Requirement: 5.3
     */
    @GetMapping("/utilisateurs/niveau/{niveau}")
    @Operation(summary = "Count users by academic level", description = "Returns the number of users assigned to classes with the specified niveau")
    public ResponseEntity<Integer> nbUtilisateursParNiveau(@PathVariable Niveau niveau) {
        Integer count = classroomService.nbUtilisateursParNiveau(niveau);
        return ResponseEntity.ok(count);
    }
    
    /**
     * Endpoint 6: Unassign a course from its class.
     * 
     * @param idCours the ID of the course to unassign
     * @return ResponseEntity with 204 No Content status, or 404 if course not found
     * 
     * Validates Requirement: 6.3
     */
    @PutMapping("/cours-classrooms/desaffecter/{idCours}")
    @Operation(summary = "Unassign a course from its class", description = "Sets the course's classe relationship to null")
    public ResponseEntity<Void> desaffecterCoursClassroomClasse(@PathVariable Integer idCours) {
        try {
            classroomService.desaffecterCoursClassroomClasse(idCours);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    /**
     * Endpoint 7: Calculate total hours by specialty and level.
     * 
     * @param specialite the specialty to filter by
     * @param niveau the academic level to filter by
     * @return ResponseEntity with total hours and 200 OK status
     * 
     * Validates Requirement: 8.5
     */
    @GetMapping("/cours/heures")
    @Operation(summary = "Calculate total hours by specialty and level", description = "Returns the sum of nbHeures for courses matching the specified specialite and niveau")
    public ResponseEntity<Integer> nbHeuresParSpecEtNiv(
            @RequestParam Specialite specialite,
            @RequestParam Niveau niveau) {
        Integer totalHours = classroomService.nbHeuresParSpecEtNiv(specialite, niveau);
        return ResponseEntity.ok(totalHours);
    }
    
    /**
     * Endpoint 8: Get all users.
     * 
     * @return ResponseEntity with list of all users and 200 OK status
     */
    @GetMapping("/utilisateurs")
    @Operation(summary = "Get all users", description = "Returns a list of all users in the system")
    public ResponseEntity<java.util.List<Utilisateur>> getAllUtilisateurs() {
        java.util.List<Utilisateur> utilisateurs = classroomService.getAllUtilisateurs();
        return ResponseEntity.ok(utilisateurs);
    }
    
    /**
     * Endpoint 9: Get user by ID.
     * 
     * @param idUtilisateur the ID of the user to retrieve
     * @return ResponseEntity with user and 200 OK status, or 404 if user not found
     */
    @GetMapping("/utilisateurs/{idUtilisateur}")
    @Operation(summary = "Get user by ID", description = "Returns the user with the specified ID")
    public ResponseEntity<Utilisateur> getUtilisateurById(@PathVariable Integer idUtilisateur) {
        try {
            Utilisateur utilisateur = classroomService.getUtilisateurById(idUtilisateur);
            return ResponseEntity.ok(utilisateur);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
