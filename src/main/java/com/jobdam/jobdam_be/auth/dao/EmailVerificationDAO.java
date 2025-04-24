package com.jobdam.jobdam_be.auth.dao;

import com.jobdam.jobdam_be.auth.mapper.VerificationMapper;
import com.jobdam.jobdam_be.auth.model.EmailVerification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EmailVerificationDAO {
    private final VerificationMapper verificationMapper;

    public void saveOrUpdateVerification(EmailVerification certification) {
        verificationMapper.saveOrUpdateVerification(certification);
    }

    public EmailVerification findByEmail(String email) {
        return verificationMapper.findByEmail(email);
    }
}
