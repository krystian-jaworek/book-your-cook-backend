package io.bookyourcook.bookyourcookbackend.service;

import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Service for handling file storage operations, including resizing and deletion.
 */
@Service
public class FileStorageService {

    private final Path profilePicLocation;
    private final Path menuPicLocation;

    public FileStorageService(@Value("${file.upload-dir}") String uploadDir,
                              @Value("${file.upload-dir.profile-pictures}") String profilePicDir,
                              @Value("${file.upload-dir.menu-pictures}") String menuPicDir) {
        this.profilePicLocation = Paths.get(uploadDir + profilePicDir);
        this.menuPicLocation = Paths.get(uploadDir + menuPicDir);
        try {
            Files.createDirectories(profilePicLocation);
            Files.createDirectories(menuPicLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage location", e);
        }
    }

    public String storeProfilePicture(MultipartFile file) {
        return storeAndResize(file, profilePicLocation);
    }

    public String storeMenuPicture(MultipartFile file) {
        return storeAndResize(file, menuPicLocation);
    }

    public void deleteProfilePicture(String filename) {
        deleteFile(filename, profilePicLocation);
    }

    public void deleteMenuPicture(String filename) {
        deleteFile(filename, menuPicLocation);
    }

    private String storeAndResize(MultipartFile file, Path location) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file.");
            }
            String filename = generateUniqueFilename(file.getOriginalFilename());
            Path destinationFile = location.resolve(filename);

            byte[] fileBytes = file.getBytes();
            if (fileBytes.length > 1024 * 1024) { // 1MB
                fileBytes = resizeImage(fileBytes, 1024);
            }

            try (InputStream inputStream = new ByteArrayInputStream(fileBytes)) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file.", e);
        }
    }

    private void deleteFile(String filename, Path location) {
        if (filename == null || filename.isBlank()) {
            return;
        }
        try {
            Path file = location.resolve(filename);
            Files.deleteIfExists(file);
        } catch (IOException e) {
            System.err.println("Failed to delete file: " + filename + " " + e.getMessage());
        }
    }

    private String generateUniqueFilename(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }

    private byte[] resizeImage(byte[] originalImageBytes, int targetSize) throws IOException {
        try (InputStream is = new ByteArrayInputStream(originalImageBytes)) {
            BufferedImage originalImage = ImageIO.read(is);
            if (originalImage == null) {
                throw new IOException("Could not read image from byte array.");
            }
            BufferedImage resizedImage = Scalr.resize(originalImage, Scalr.Method.QUALITY, Scalr.Mode.AUTOMATIC, targetSize, targetSize, Scalr.OP_ANTIALIAS);
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                ImageIO.write(resizedImage, "jpg", baos);
                return baos.toByteArray();
            }
        }
    }
}