package com.example.carnetadresses.repository;

import com.example.carnetadresses.model.Groupe;
import com.example.carnetadresses.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GroupeRepository extends JpaRepository<Groupe, Long> {
    List<Groupe> findByOwner(User owner);
}
