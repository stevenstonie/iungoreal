package com.stevenst.app.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.stevenst.app.service.AmazonS3Service;
import com.stevenst.lib.exception.IgorIoException;
import com.stevenst.lib.payload.ResponsePayload;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
@RequiredArgsConstructor
public class AmazonS3ServiceImpl implements AmazonS3Service {
    private final S3Client s3Client;
    @Value("${aws.bucketName}")
    private String bucketName;

    public ResponseEntity<ResponsePayload> uploadPfpToS3(String username, MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String folder = username;
        String key = (folder != null && !folder.isEmpty() ? folder : "") + "/profile_picture/" + fileName;

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            return convertAndUploadToS3(putObjectRequest, folder, key, file);
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            throw e;
        }
    }

    // --------------------------------------------------------------------

    private ResponseEntity<ResponsePayload> convertAndUploadToS3(PutObjectRequest putObjectRequest, String folder, String key, MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, file.getSize()));
            
            return ResponseEntity.ok(ResponsePayload.builder()
                    .status(200)
                    .message("File uploaded to S3 bucket " + bucketName + " at key " + key)
                    .build());
        } catch (IOException e) {
            System.err.println("Unable to convert MultipartFile to InputStream: " + e.getMessage());
            throw new IgorIoException(e.getMessage());
        }
    }
}
