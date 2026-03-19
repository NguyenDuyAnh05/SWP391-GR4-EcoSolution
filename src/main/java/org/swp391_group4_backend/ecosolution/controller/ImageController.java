package org.swp391_group4_backend.ecosolution.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.swp391_group4_backend.ecosolution.service.ImageService;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/images")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"}) 
public class ImageController {
    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        String imageUrl = imageService.uploadImage(file);
        return ResponseEntity.ok(imageUrl);
    }
}
