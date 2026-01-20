package com.example.carnetadresses.model;

import jakarta.persistence.*;
import java.util.Set;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.HashSet;

@Entity
@Table(name = "contacts")
@Getter @Setter
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    private String name;

    private String firstName;

    @Email(message = "Email invalide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;

    private String phone;
    private String company;

    private boolean isPrivate = false;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime deletedAt; 

    private String photoUrl;
    
    private String linkedinUrl;
    private String twitterUrl;
    private String githubUrl;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner; 

    @ManyToMany
    @JoinTable(
        name = "contact_groupe_mapping",
        joinColumns = @JoinColumn(name = "contact_id"),
        inverseJoinColumns = @JoinColumn(name = "groupe_id")
    )
    private Set<Groupe> groupes = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "contact_tags", joinColumns = @JoinColumn(name = "contact_id"))
    @Column(name="tag_name")
    private Set<String> tags = new HashSet<>();
}