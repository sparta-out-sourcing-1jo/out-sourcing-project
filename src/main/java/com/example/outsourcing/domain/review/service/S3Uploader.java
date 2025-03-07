package com.example.outsourcing.domain.review.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.UUID;

import static com.example.outsourcing.common.exception.ErrorCode.FILE_UPLOAD_FAILED;

@Service
@RequiredArgsConstructor
public class S3Uploader {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final AmazonS3 amazonS3;

    // 이미지 파일 업로드 로직
    public String uploadFile(MultipartFile file) {
        String fileName = generateFileName(file.getOriginalFilename());

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        try {
            amazonS3.putObject(bucket, fileName, file.getInputStream(), objectMetadata);
        } catch (IOException e) {
            throw new ResponseStatusException(
                    FILE_UPLOAD_FAILED.getStatus(),
                    FILE_UPLOAD_FAILED.getMessage()
            );
        }

        return amazonS3.getUrl(bucket, fileName).toString();
    }

    private String generateFileName(String originalFilename) {
        return UUID.randomUUID() + "_" + originalFilename;
    }
}
