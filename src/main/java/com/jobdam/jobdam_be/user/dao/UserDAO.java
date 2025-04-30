package com.jobdam.jobdam_be.user.dao;

import com.jobdam.jobdam_be.user.mapper.UserMapper;
import com.jobdam.jobdam_be.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserDAO {
    private final UserMapper userMapper;

    public Optional<User> findById(Long id) {
        return userMapper.findById(id);
    }

    public boolean existsByEmail(String email) {
        return userMapper.existsByEmail(email);
    }

    public boolean save(User user) {
        return userMapper.save(user) > 0;
    }

    public Optional<User> findByEmail(String email) {
        return userMapper.findByEmail(email);
    }

    public void updateCreatedAtByEmail(String email) {
        userMapper.updateCreatedAtByEmail(email);
    }
}
