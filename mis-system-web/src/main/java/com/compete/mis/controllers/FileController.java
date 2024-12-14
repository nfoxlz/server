package com.compete.mis.controllers;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

@RestController
@RequestMapping("/public")
public class FileController {

    private static final Path fileStorageLocation;

    static {
        Properties properties = new Properties();
        try (InputStream stream = FileController.class.getResourceAsStream("/application.properties")) {
            properties.load(stream);
        } catch (IOException e) {
//                LOGGER.error(String.format("【/database.%s.properties】文件打开失败。", key));
//                LOGGER.error(e.getMessage());
//                e.printStackTrace();
            throw new RuntimeException(e);
        }
        fileStorageLocation = Paths.get(properties.getProperty("publicPath", "/public"))//"D:/Projects/CompeteMIS/release/public"
                .toAbsolutePath().normalize();

        // 确保目录存在
        try {
            Files.createDirectories(fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        try {
            Path filePath = fileStorageLocation.resolve(fileName.replace('~', '/')).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_TYPE, Files.probeContentType(filePath));
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"");
                return ResponseEntity.ok()
                        .headers(headers)
                        .body(resource);
            } else {
                throw new RuntimeException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("File not found " + fileName, ex);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
