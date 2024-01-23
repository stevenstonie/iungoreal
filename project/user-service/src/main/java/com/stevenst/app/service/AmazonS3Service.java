package com.stevenst.app.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.stevenst.lib.payload.ResponsePayload;

@Service
public interface AmazonS3Service {
	ResponseEntity<ResponsePayload> uploadPfpToS3(String username, MultipartFile file);
}
