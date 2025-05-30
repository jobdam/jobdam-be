package com.jobdam.jobdam_be.auth.dao;

import com.jobdam.jobdam_be.auth.mapper.EmailVerificationMapper;
import com.jobdam.jobdam_be.auth.model.EmailVerification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EmailVerificationDAO {
    private final EmailVerificationMapper verificationMapper;

    public void saveOrUpdateVerification(EmailVerification certification) {
        verificationMapper.saveOrUpdateVerification(certification);
    }

    public Optional<EmailVerification> findByToken(String token) {
        return verificationMapper.findByToken(token);
    }

    public void deleteByEmail(String email) {
        verificationMapper.deleteByEmail(email);
    }
}
