package com.example.carnetadresses.service;

import com.example.carnetadresses.model.Contact;
import com.example.carnetadresses.model.Groupe;
import com.example.carnetadresses.model.User;
import com.example.carnetadresses.repository.ContactRepository;
import com.example.carnetadresses.repository.GroupeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupeService {

    @Autowired
    private GroupeRepository groupeRepository;

    @Autowired
    private ContactRepository contactRepository;

    public Groupe creerGroupe(String nom, User user) {
        Groupe groupe = new Groupe();
        groupe.setNom(nom);
        groupe.setOwner(user);
        return groupeRepository.save(groupe);
    }

    public List<Groupe> getMesGroupes(User user) {
        return groupeRepository.findByOwner(user);
    }

    public void ajouterContactAuGroupe(Long contactId, Long groupeId, User user) {
        Contact contact = contactRepository.findById(contactId)
                .filter(c -> c.getOwner().equals(user))
                .orElseThrow(() -> new RuntimeException("Contact non trouvé ou accès refusé"));
            
        Groupe groupe = groupeRepository.findById(groupeId)
                .filter(g -> g.getOwner().equals(user))
                .orElseThrow(() -> new RuntimeException("Groupe non trouvé ou accès refusé"));

        contact.getGroupes().add(groupe);
        contactRepository.save(contact);
    }
}