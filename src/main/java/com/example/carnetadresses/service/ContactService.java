package com.example.carnetadresses.service;

import com.example.carnetadresses.model.Contact;
import com.example.carnetadresses.model.User;
import com.example.carnetadresses.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    public Contact createContact(Contact contact, User user) {
        contact.setOwner(user);
        contact.setCreatedAt(LocalDateTime.now());
        return contactRepository.save(contact);
    }

    public List<Contact> getMyContacts(User user) {
        return contactRepository.findByOwnerAndDeletedAtIsNull(user);
    }

    public List<Contact> searchMyContacts(User user, String keyword) {
        return contactRepository.searchContacts(user, keyword);
    }

    public Contact updateContact(Long id, Contact contactDetails, User user) {
        Contact contact = contactRepository.findById(id)
                .filter(c -> c.getOwner().equals(user)) // Sécurité : doit appartenir à l'user
                .orElseThrow(() -> new RuntimeException("Contact non trouvé ou accès refusé"));

        contact.setName(contactDetails.getName());
        contact.setFirstName(contactDetails.getFirstName());
        contact.setEmail(contactDetails.getEmail());
        contact.setPhone(contactDetails.getPhone());
        contact.setCompany(contactDetails.getCompany());

        return contactRepository.save(contact);
    }

    public void deleteContact(Long id, User user) {
        Contact contact = contactRepository.findById(id)
                .filter(c -> c.getOwner().equals(user))
                .orElseThrow(() -> new RuntimeException("Contact non trouvé ou accès refusé"));
        
        contact.setDeletedAt(LocalDateTime.now());
        contactRepository.save(contact);
    }

        public String exportContactsToCsv(List<Contact> contacts) {        StringBuilder csvContent = new StringBuilder();

        csvContent.append("Nom, Prénom, Email, Téléphone, Entreprise\n");
        for (Contact contact: contacts){
            csvContent.append(contact.getName()).append(",")
                    .append(contact.getFirstName() != null ? contact.getFirstName() : "").append(",")
                    .append(contact.getEmail()).append(",")
                    .append(contact.getPhone() != null ? contact.getPhone(): "").append(",")
                    .append(contact.getCompany() != null ? contact.getCompany() : "").append("\n");

        }
        return csvContent.toString();
    }

    public List<Contact> getAllContactsForAdmin(){
        return contactRepository.findAll().stream()
            .filter(c -> c.getDeletedAt() == null)
            .toList();
    }
}