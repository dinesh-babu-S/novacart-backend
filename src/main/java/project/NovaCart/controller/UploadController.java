package project.NovaCart.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import project.NovaCart.service.FileStorageService;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private final FileStorageService storageService;

    public UploadController(FileStorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping
    public ResponseEntity<String> uploadImage(
            @RequestParam("file") MultipartFile file) {

        return ResponseEntity.ok(
                storageService.uploadFile(file));
    }

}