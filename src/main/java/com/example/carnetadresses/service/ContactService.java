package com.example.carnetadresses.service;

import com.example.carnetadresses.model.Contact;
import com.example.carnetadresses.model.User;
import com.example.carnetadresses.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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

    public void ImportContactsFromCsv(MultipartFile file, User user) throws Exception{
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean isFirstLine = true;

            while((line = fileReader.readLine()) != null){
                if(isFirstLine){
                    isFirstLine = false;
                    continue;
                }

                String[] data = line.split(",");

                if (data.length >= 3 && !data[0].isBlank() && !data[2].isBlank()) {
                Contact contact = new Contact();
                contact.setName(data[0].trim());
                contact.setFirstName(data[1].trim());
                contact.setEmail(data[2].trim());
                if (data.length >= 4) contact.setPhone(data[3].trim());
                if (data.length >= 5) contact.setCompany(data[4].trim());
                
                contact.setOwner(user);
                contact.setCreatedAt(LocalDateTime.now());
                
                contactRepository.save(contact);
            }
            }
        }
    }

    public List<Contact> getMaCorbeille(User user){
        return contactRepository.findAll().stream()
            .filter(c -> c.getOwner().equals(user) && c.getDeletedAt() != null)
            .toList();
    }

    public Contact restaurerContact(Long id, User user){
        Contact contact = contactRepository.findById(id)
            .filter(c -> c.getOwner().equals(user))
            .orElseThrow(() -> new RuntimeException("contact non trouvé"));

            contact.setDeletedAt(null);
            return contactRepository.save(contact);
    }

    public List<Contact> getContactsByTag(User user, String tag){
        return contactRepository.findByOwnerAndTag(user, tag);
    }

    public Contact toggleConfidentialite(Long id, User user){
        Contact contact = contactRepository.findById(id)
            .filter(c -> c.getOwner().equals(user))
            .orElseThrow(() -> new RuntimeException("Contact non trouvé"));

        contact.setPrivate(!contact.isPrivate());
        return contactRepository.save(contact);
    }

    public List<Contact> getContactsFiltres(User user, boolean voirPrivé){
        if(voirPrivé){
            return contactRepository.findByOwnerAndDeletedAtIsNull(user);
        }
        return contactRepository.findByOwnerAndDeletedAtIsNullAndIsPrivateFalse(user);
    }

    public Contact updateContactSocial(Long id, Contact socialData, User user){
        Contact contact = contactRepository.findById(id)
            .filter(c -> c.getOwner().equals(user))
            .orElseThrow(() -> new RuntimeException("contact non trouvé"));
     
        contact.setLinkedinUrl(socialData.getLinkedinUrl());
        contact.setTwitterUrl(socialData.getTwitterUrl());
        contact.setGithubUrl(socialData.getGithubUrl());

        return contactRepository.save(contact);
    }
}