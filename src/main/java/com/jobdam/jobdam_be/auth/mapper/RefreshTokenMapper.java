package com.jobdam.jobdam_be.auth.mapper;

import com.jobdam.jobdam_be.auth.model.RefreshToken;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RefreshTokenMapper {
    int save(RefreshToken refreshToken);

    void deleteByUserId(long user_id);

    void deleteByRefreshToken(String refresh);

    boolean existsByRefreshToken(String refresh);
}
