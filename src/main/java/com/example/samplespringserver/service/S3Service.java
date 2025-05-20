package com.example.samplespringserver.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 단일 파일 업로드
    public String uploadFile(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            amazonS3.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), metadata));

            return amazonS3.getUrl(bucket, fileName).toString();
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패: " + e.getMessage());
        }
    }


    // 다중 파일 업로드
    public List<String> uploadFiles(List<MultipartFile> files) throws IOException {
        List<String> uploadedUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            String url = uploadFile(file);
            uploadedUrls.add(url);
        }

        return uploadedUrls;
    }


    // 단일 파일 삭제
    public void deleteFile(String fileName) {
        if (amazonS3.doesObjectExist(bucket, fileName)) {
            amazonS3.deleteObject(bucket, fileName);
        } else {
            throw new RuntimeException("파일이 존재하지 않습니다. : " + fileName);
        }
    }

    // 다중 파일 삭제
    public void deleteFiles(List<String> fileNames) {
        List<DeleteObjectsRequest.KeyVersion> keys = fileNames.stream()
                .map(DeleteObjectsRequest.KeyVersion::new)
                .toList();

        DeleteObjectsRequest deleteRequest = new DeleteObjectsRequest(bucket).withKeys(keys);
        amazonS3.deleteObjects(deleteRequest);
    }
}
