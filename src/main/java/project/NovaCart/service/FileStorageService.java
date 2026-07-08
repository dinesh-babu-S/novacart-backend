package project.NovaCart.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String uploadFile(MultipartFile file) {

        try {

            Path path = Paths.get(uploadDir);

            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            String fileName = System.currentTimeMillis()
                    + "_"
                    + file.getOriginalFilename();

            Path filePath = path.resolve(fileName);

            Files.copy(file.getInputStream(), filePath);

            return "/uploads/" + fileName;

        } catch (IOException e) {

            throw new RuntimeException("File upload failed.");
        }

    }

}