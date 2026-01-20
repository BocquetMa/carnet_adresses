package com.example.carnetadresses.controller;

import com.example.carnetadresses.model.Contact;
import com.example.carnetadresses.model.User;
import com.example.carnetadresses.repository.ContactRepository;
import com.example.carnetadresses.repository.UserRepository;
import com.example.carnetadresses.service.FileStorageService;
import com.example.carnetadresses.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @Autowired
    private UserRepository userRepository;

    
    @Autowired
    private ContactRepository contactRepository;

    @Autowired 
    private FileStorageService fileStorageService;

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    @GetMapping
    public List<Contact> getAllMyContacts(@RequestParam(required = false) String keyword) {
        User user = getAuthenticatedUser();
        if (keyword != null && !keyword.isEmpty()) {
            return contactService.searchMyContacts(user, keyword);
        }
        return contactService.getMyContacts(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contact> getContactById(@PathVariable Long id) {
        User user = getAuthenticatedUser();
        return ResponseEntity.ok(contactService.updateContact(id, new Contact(), user)); 
    }

    @PostMapping
    public Contact createContact(@RequestBody Contact contact) {
        User user = getAuthenticatedUser();
        return contactService.createContact(contact, user);
    }

    @PutMapping("/{id}")
    public Contact updateContact(@PathVariable Long id, @RequestBody Contact contactDetails) {
        User user = getAuthenticatedUser();
        return contactService.updateContact(id, contactDetails, user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteContact(@PathVariable Long id) {
        User user = getAuthenticatedUser();
        contactService.deleteContact(id, user);
        return ResponseEntity.ok().body("Contact supprimé avec succès");
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCsv() {
        User user = getAuthenticatedUser();
        List<Contact> contacts;

        if ("ROLE_ADMIN".equals(user.getRole())) {
            contacts = contactService.getAllContactsForAdmin();
        } else {
            contacts = contactService.getMyContacts(user);
        }

        String csvData = contactService.exportContactsToCsv(contacts);
        byte[] csvBytes = csvData.getBytes(java.nio.charset.StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=contacts_export.csv")
                .header("Content-Type", "text/csv; charset=UTF-8")
                .body(csvBytes);
    }

    @PostMapping("/import")
    public ResponseEntity<String> importCsv(@RequestParam("file") MultipartFile file){
        if(file.isEmpty()){
            return ResponseEntity.badRequest().body("veuillez sélectionner un fichier csv");
        }
        try {
            User user = getAuthenticatedUser();
            contactService.ImportContactsFromCsv(file, user);
            return ResponseEntity.ok("importation réussi");
        } catch(Exception e) {
            return ResponseEntity.status(500).body("Erreur lors de l'import : " + e.getMessage());

        }
    }

    @GetMapping("/corbeille")
    public List<Contact> getCorbeille(){
        return contactService.getMaCorbeille(getAuthenticatedUser());
    }

    @PutMapping("/{id}/restaurer")
    public ResponseEntity<Contact> restaurer(@PathVariable Long id){
        Contact restored = contactService.restaurerContact(id, getAuthenticatedUser());
        return ResponseEntity.ok(restored);
    }

    @PostMapping("/{id}/photo")
    public ResponseEntity<String> uploadPhoto(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        String filename = fileStorageService.save(file);
        
        User user = getAuthenticatedUser();
        Contact contact = contactRepository.findById(id)
                .filter(c -> c.getOwner().equals(user))
                .orElseThrow(() -> new RuntimeException("Contact non trouvé"));
                
        contact.setPhotoUrl(filename);
        contactRepository.save(contact);
        
        return ResponseEntity.ok("Photo uploadée : " + filename);
    }

    @GetMapping
    public List<Contact> getAllMyContacts(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String tag
    ){
        User user= getAuthenticatedUser();

        if(tag != null && !tag.isEmpty()){
            return contactService.getContactsByTag(user, tag);
        }

        if(keyword != null && !keyword.isEmpty()){
            return contactService.searchMyContacts(user, keyword);
        }

        return contactService.getMyContacts(user);
    }

    @GetMapping
    public List<Contact> getAll(
        @RequestParam(required = false) String tag,
        @RequestParam(defaultValue = "false") boolean hidden) {
            User user = getAuthenticatedUser();

            if (tag != null && !tag.isEmpty()) {
                return contactService.getContactsByTag(user, tag).stream()
                    .filter(c -> hidden || !c.isPrivate())
                    .toList();
            }
            return contactService.getContactsFiltres(user, hidden);
        }

    @PatchMapping("/{id}/toggle-private")
    public ResponseEntity<Contact> togglePrivate(@PathVariable Long id){
        Contact updated = contactService.toggleConfidentialite(id, getAuthenticatedUser());
        return ResponseEntity.ok(updated);
    }
    
    @PatchMapping("/{id}/social")
    public ResponseEntity<Contact> updateSocialLinks(@PathVariable Long id, @RequestBody Contact socialData){
        Contact updated = contactService.updateContactSocial(id, socialData, getAuthenticatedUser());
        return ResponseEntity.ok(updated);
    }
}