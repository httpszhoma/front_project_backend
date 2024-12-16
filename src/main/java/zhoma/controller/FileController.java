package zhoma.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import zhoma.service.AzureBlobService;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final AzureBlobService azureBlobService;

    // Эндпоинт для загрузки файла
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFiles(@RequestParam("files") MultipartFile[] files) {
        StringBuilder uploadedUrls = new StringBuilder();

        try {
            for (MultipartFile file : files) {
                InputStream inputStream = file.getInputStream();
                String imageUrl = azureBlobService.uploadImage(file.getOriginalFilename(), inputStream, file.getSize());
                uploadedUrls.append(imageUrl).append("\n"); // Добавляем URL каждого загруженного файла
            }

            return ResponseEntity.ok("Images uploaded successfully. URLs: \n" + uploadedUrls.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    // Эндпоинт для удаления файла
    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName) {
        try {
            azureBlobService.deleteFile(fileName);
            return ResponseEntity.ok("File deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to delete file: " + e.getMessage());
        }
    }
}

