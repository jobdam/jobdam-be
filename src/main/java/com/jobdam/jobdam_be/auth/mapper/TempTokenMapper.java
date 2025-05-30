package com.jobdam.jobdam_be.auth.mapper;

import com.jobdam.jobdam_be.auth.model.OauthTempToken;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TempTokenMapper {
    void save(OauthTempToken token);

    Long findUserIdByToken(String token);

    void deleteByToken(String token);
}
