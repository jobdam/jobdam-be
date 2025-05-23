package com.jobdam.jobdam_be.s3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.jobdam.jobdam_be.s3.exception.S3ErrorCode;
import com.jobdam.jobdam_be.s3.exception.S3Exception;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 amazonS3;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * s3 서버에 이미지 업로드 (기존 값은 제거)
     *
     * @param image         - 업로드할 이미지
     * @param profileImgUrl - 기존의 프로필 이미지 주소
     * @param userId        - 업로드할 대상의 아이디
     * @return 저장된 이미지의 퍼블릭 주소
     */
    public String uploadImage(MultipartFile image, String profileImgUrl, Long userId) {

        // 파일 확장자 추출
        String extension = getExtension(image);
        if (extension == null) {
            throw new S3Exception(S3ErrorCode.INVALID_FILE);
        }
        String dirName = "profile/" + userId + "/";
        String fileName = dirName + UUID.randomUUID() + "_" + userId + "_profile" + extension;

        uploadFileToS3(image, fileName);
        String fileKey = getFileKey(profileImgUrl);
        // 기존의 프로필 이미지가 있었다면 삭제
        if (profileImgUrl != null && fileKey != null)
            amazonS3.deleteObject(bucket, fileKey);

        return getPublicUrl(fileName);
    }

    public String uploadResume(MultipartFile pdf, String pdfUrl, Long userId) {

        // 파일 확장자 추출
        String extension = getExtension(pdf);
        log.info("extension: {}", extension);
        if (extension == null || !extension.equals(".pdf")) {
            throw new S3Exception(S3ErrorCode.INVALID_FILE);
        }
        String dirName = "resume/" + userId + "/";
        String fileName = dirName + UUID.randomUUID() + "_" + userId + "_resume" + extension;

        uploadFileToS3(pdf, fileName);
        String fileKey = getFileKey(pdfUrl);
        // 기존의 이력서가 있었다면 삭제
        if (pdfUrl != null && fileKey != null) {
            amazonS3.deleteObject(bucket, fileKey);
        }

        return getPublicUrl(fileName);
    }

    /**
     * 확장자 추출
     *
     * @param file - 파일
     */
    private String getExtension(MultipartFile file) {
        String extension = "";
        String originalFilename = file.getOriginalFilename();

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        }
        return extension;
    }

    /**
     * s3 url 에서 파일 키 값만 부분만 추출
     *
     * @param url 기존의 파일 주소
     * @return 파일 키 값
     */
    private String getFileKey(String url) {
        String[] split = url.split(".com/");
        if (split.length < 2) {
            return null;
        }
        return split[1];
    }

    /**
     * 유저가 접근 가능한 url 제공
     *
     * @param fileName - 파일명
     * @return 접근 가능한 url
     */
    private String getPublicUrl(String fileName) {
        return "https://d25aj80izrl3th.cloudfront.net/" + fileName;
    }

    private void uploadFileToS3(MultipartFile file, String fileName) {
        // 메타데이터 설정
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        try {
            // S3에 파일 업로드
            amazonS3.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), metadata));
        } catch (IOException e) {
            throw new S3Exception(S3ErrorCode.FILE_UPLOAD_FAILED, e);
        }
    }
}
