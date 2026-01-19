package com.example.carnetadresses.controller;

import com.example.carnetadresses.model.Groupe;
import com.example.carnetadresses.model.User;
import com.example.carnetadresses.repository.UserRepository;
import com.example.carnetadresses.service.GroupeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groupes")
public class GroupeController {
    
    @Autowired
    private GroupeService groupeService;

    @Autowired
    private UserRepository userRepository;

    private User getAuthenticatedUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(auth.getName())
            .orElseThrow(() -> new RuntimeException("utilisateur non trouvé"));
    }

    @PostMapping
    public Groupe createGroupe(@RequestBody String nom){
        return groupeService.creerGroupe(nom, getAuthenticatedUser());
    }

    @GetMapping
    public List<Groupe> getMyGroupes(){
        return groupeService.getMesGroupes(getAuthenticatedUser());
    }

    @PostMapping("/{groupeId}/contacts/{contactId}")
    public ResponseEntity<?> addContactToGroupe(@PathVariable Long groupeId, @PathVariable Long contactId) {
        groupeService.ajouterContactAuGroupe(contactId, groupeId, getAuthenticatedUser());
        return ResponseEntity.ok().body("contact ajouté au groupe");
    }
}
