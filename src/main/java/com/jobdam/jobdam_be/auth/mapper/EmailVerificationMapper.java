package com.jobdam.jobdam_be.auth.mapper;

import com.jobdam.jobdam_be.auth.model.EmailVerification;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface EmailVerificationMapper {
    void saveOrUpdateVerification(EmailVerification certification);

    Optional<EmailVerification> findByEmail(String email);

    Optional<EmailVerification> findByToken(String token);

    void deleteByEmail(String email);
}
