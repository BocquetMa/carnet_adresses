package com.example.carnetadresses.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {
    private final Path root = Paths.get("uploads");

    public String save(MultipartFile file){
        try{
            if(!Files.exists(root)) Files.createDirectories(root);

            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Files.copy(file.getInputStream(), this.root.resolve(filename));

            return filename;
        }catch (Exception e){
            throw new RuntimeException("Impossible de sotcker le fichier : " + e.getMessage());
        }
    }
}
