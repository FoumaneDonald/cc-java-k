package com.grh.backend_m1.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@Table(name = "employees")
@Getter 
@Setter
@NoArgsConstructor @AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    

    @Column(unique = true)
    private String registrationNumber; // Matricule (RG-M1-01) 

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String emailPro; // RG-M1-03 

    @Column(nullable = false)
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    private EmployeeStatus status = EmployeeStatus.BROUILLON; // RG-M1-04 

    private LocalDate departureDate; // Requis pour RG-M1-06 
    private String departureReason;
    
    public String getRegistrationNumber1() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
    
    public String getEmailPro() { return emailPro; }
    public void setEmailPro(String emailPro) { this.emailPro = emailPro; }

    // Relations (À compléter plus tard)
    // @ManyToOne private Department department;
    // @ManyToOne private Position position;
}