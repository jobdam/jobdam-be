package com.jobdam.jobdam_be.auth.mapper;

import com.jobdam.jobdam_be.auth.model.EmailVerification;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CertificationMapper {
    void saveOrUpdateCertification(EmailVerification certification);
}
