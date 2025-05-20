package com.example.samplespringserver.controller;

import com.example.samplespringserver.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    // 단일 파일 업로드
    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestPart MultipartFile file) throws IOException {
        String url = s3Service.uploadFile(file);

        return ResponseEntity.ok(url);
    }

    // 다중 파일 업로드
    @PostMapping("/upload-multiple")
    public ResponseEntity<List<String>> uploadMultipleFiles(@RequestParam("files") List<MultipartFile> files) throws IOException {
        List<String> urls = s3Service.uploadFiles(files);

        return ResponseEntity.ok(urls);
    }

    // 단일 파일 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFile(@RequestParam String fileName) {
        try {
            s3Service.deleteFile(fileName);

            return ResponseEntity.ok("삭제 완료: " + fileName);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 다중 파일 삭제
    @DeleteMapping("/delete-multiple")
    public ResponseEntity<String> deleteMultiple(@RequestBody List<String> fileNames) {
        s3Service.deleteFiles(fileNames);
        return ResponseEntity.ok("삭제 완료: " + fileNames);
    }
}
