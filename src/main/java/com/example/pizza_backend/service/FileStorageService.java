package com.example.pizza_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {
    public static void saveFile(String uploadDir , MultipartFile multipartFile, String fileName) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new IOException("Image file is null or empty");
        }

        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IOException("Could not save image file: "+ fileName, e);
        }
    }

    public static void deleteFile(String uploadDir, String fileName) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        Path filePath = uploadPath.resolve(fileName);

        try {
            if (Files.deleteIfExists(filePath)) {
                System.out.println("Deleted file: " + fileName);
            } else {
                System.out.println("File not found: " + fileName);
            }
        } catch (IOException e) {
            throw new IOException("Could not delete image file: " + fileName, e);
        }
    }
}
