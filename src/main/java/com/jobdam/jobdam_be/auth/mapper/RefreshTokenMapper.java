package com.jobdam.jobdam_be.auth.mapper;

import com.jobdam.jobdam_be.auth.model.RefreshToken;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RefreshTokenMapper {
    void save(RefreshToken refreshToken);
}
