package com.jobdam.jobdam_be.auth.dao;

import com.jobdam.jobdam_be.auth.mapper.TempTokenMapper;
import com.jobdam.jobdam_be.auth.model.OauthTempToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TempTokenDAO {
    private final TempTokenMapper tempTokenMapper;

    public void save(OauthTempToken token) {
        tempTokenMapper.save(token);
    }

    public Long findUserIdByToken(String token) {
        return tempTokenMapper.findUserIdByToken(token);
    }

    public void deleteByToken(String token) {
        tempTokenMapper.deleteByToken(token);
    }
}
