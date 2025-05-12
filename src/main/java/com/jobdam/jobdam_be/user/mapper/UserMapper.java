package com.jobdam.jobdam_be.user.mapper;

import com.jobdam.jobdam_be.user.model.Resume;
import com.jobdam.jobdam_be.user.model.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface UserMapper {
    int saveEmail(User user);

    void saveSocial(User user);

    void updateCreatedAtByEmail(String email);

    void updateSocialByEmail(User user);

    int initProfile(User user);

    int updateProfile(User updateUser);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    Optional<User> findByProviderId(String providerId);

    Long findIdByEmail(String email);

    String findProfileImgUrlById(Long id);

    String findResumeUrlById(Long id);

    String findNameById(Long id);

    boolean existsByEmail(String email);

    boolean existsJobById(Long id);

    void saveOrUpdateResume(Resume resume);
}
