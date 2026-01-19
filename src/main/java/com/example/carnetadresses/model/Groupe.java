package com.example.carnetadresses.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="groupes")
@Getter @Setter
public class Groupe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User owner;

    @ManyToMany(mappedBy="groupes")
    private Set<Contact> contacts = new HashSet<>();
}
