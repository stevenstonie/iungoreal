package com.stevenst.app.service.impl;

import org.springframework.stereotype.Service;

import com.stevenst.app.service.AmazonS3Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AmazonS3ServiceImpl implements AmazonS3Service {

    // public ResponseEntity<String> getPfpFromS3(String username) {
    //     String folder = username;
    //     String key = (folder != null && !folder.isEmpty() ? folder : "") + "/profile_picture/";
    //     String fileName = "Screenshot 2023-07-11 122122.png";
    //     String fullKey = key + fileName;

    //     String presignedUrl = generatePresignedUrl(bucketName, fullKey, Duration.ofMinutes(10));

    //     return ResponseEntity.ok(presignedUrl);
    // }

    

    // --------------------------------------------------------------------

    // private String generatePresignedUrl(String bucketName, String objectKey, Duration expirationTime) {
    //     try (S3Presigner presigner = S3Presigner.create()) {
    //         GetObjectRequest getObjectRequest = GetObjectRequest.builder()
    //                 .bucket(bucketName)
    //                 .key(objectKey)
    //                 .build();
    
    //         GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
    //                 .signatureDuration(expirationTime)
    //                 .getObjectRequest(getObjectRequest)
    //                 .build();
    
    //         PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
    
    //         return presignedRequest.url().toString();
    //     }
    // }
    
}
