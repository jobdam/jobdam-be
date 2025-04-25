package com.jobdam.jobdam_be.auth.dao;

import com.jobdam.jobdam_be.auth.mapper.RefreshTokenMapper;
import com.jobdam.jobdam_be.auth.model.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RefreshDAO {
    private final RefreshTokenMapper refreshTokenMapper;
    public void save(RefreshToken refreshToken) {
        refreshTokenMapper.save(refreshToken);
    }

    public void deleteByUserId(long user_id) {
        refreshTokenMapper.deleteByUserId(user_id);
    }

    public void deleteByRefreshToken(String refresh) {
        refreshTokenMapper.deleteByRefreshToken(refresh);
    }

    public boolean existsByRefreshToken(String refresh) {
        return refreshTokenMapper.existsByRefreshToken(refresh);
    }
}
