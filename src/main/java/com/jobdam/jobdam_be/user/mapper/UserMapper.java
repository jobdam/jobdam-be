package com.jobdam.jobdam_be.user.mapper;

import com.jobdam.jobdam_be.user.model.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface UserMapper {
    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    int save(User user);

    void updateCreatedAtByEmail(String email);

    Optional<User> findByProviderId(String providerId);

    Long findIdByEmail(String email);

    void updateSocialByEmail(User user);

    void saveSocial(User user);
}
