package com.example.carnetadresses.repository;

import com.example.carnetadresses.model.Contact;
import com.example.carnetadresses.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    
    List<Contact> findByOwnerAndDeletedAtIsNull(User owner);
    
    @Query("select c from Contact c where c.owner = :owner and c.deletedAt is null " + 
        "and (c.name like %:keyword% or c.email like %:keyword%)")
    List<Contact> searchContacts(User owner, String keyword);
    
    List<Contact> findByDeletedAtIsNotNull();

    @Query("select c from Contact c join c.tags t where c.owner = :owner and c.deleteAt is null and t = :tag")
    List<Contact> findByOwnerAndTag(User owner, String tag);

    List<Contact> findByOwnerAndDeletedAtIsNullAndIsPrivateFalse(User owner);

    List<Contact> findByOwnerAndDeletedAtIsNullAndIsPrivateTrue(User owner);
}
