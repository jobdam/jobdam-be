package com.jobdam.jobdam_be.s3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.jobdam.jobdam_be.s3.exception.S3ErrorCode;
import com.jobdam.jobdam_be.s3.exception.S3Exception;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 amazonS3;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * s3 서버에 이미지 업로드 (기존 값은 제거)
     *
     * @param image - 업로드할 이미지
     * @param profileImg - 기존의 프로필 이미지 주소
     * @param userId - 업로드할 대상의 아이디
     * @return 저장된 이미지의 퍼블릭 주소
     */
    public String uploadImage(MultipartFile image, String profileImg, Long userId) throws IOException {

        // 파일 확장자 추출
        String extension = getImageExtension(image);
        String fileName = UUID.randomUUID() + "_" + userId + "_profile" + extension;

        // 메타데이터 설정
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(image.getContentType());
        metadata.setContentLength(image.getSize());

        // S3에 파일 업로드 요청 생성
        PutObjectRequest putObjectRequest = null;
        try {
            putObjectRequest = new PutObjectRequest(bucket, fileName, image.getInputStream(), metadata);

            // S3에 파일 업로드
            amazonS3.putObject(putObjectRequest);
        } catch (IOException e) {
            throw new S3Exception(S3ErrorCode.IMAGE_UPLOAD_FAILED);
        }

        // 기존의 프로필 이미지가 있었다면 삭제
        if (profileImg != null)
            amazonS3.deleteObject(bucket, getImageKey(profileImg));

        return getPublicUrl(fileName);
    }

    /**
     * 확장자 추출
     *
     * @param image - Access 또는 Refresh
     */
    private String getImageExtension(MultipartFile image) {
        String extension = "";
        String originalFilename = image.getOriginalFilename();

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        }
        return extension;
    }

    /**
     * s3 url 에서 profile 키 값만 부분만 추출
     *
     * @param profileImg - 기존의 프로필 이미지 주소
     * @return 프로필 이미지 키 값
     */
    private String getImageKey(String profileImg) {
        return profileImg.substring(profileImg.lastIndexOf('/') + 1);
    }

    /**
     * 유저가 접근 가능한 url 제공
     *
     * @param fileName - 파일명
     * @return 접근 가능한 url
     */
    private String getPublicUrl(String fileName) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, amazonS3.getRegionName(), fileName);
    }
}
