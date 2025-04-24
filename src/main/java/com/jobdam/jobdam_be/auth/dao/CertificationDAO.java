package com.jobdam.jobdam_be.auth.dao;

import com.jobdam.jobdam_be.auth.mapper.CertificationMapper;
import com.jobdam.jobdam_be.auth.model.EmailVerification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CertificationDAO {
    private final CertificationMapper certificationMapper;

    public void saveOrUpdateCertification(EmailVerification certification) {
        certificationMapper.saveOrUpdateCertification(certification);
    }
}
