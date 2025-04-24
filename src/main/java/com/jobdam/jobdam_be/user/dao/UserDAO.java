package com.jobdam.jobdam_be.user.dao;

import com.jobdam.jobdam_be.user.mapper.UserMapper;
import com.jobdam.jobdam_be.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserDAO {
    private final UserMapper userMapper;

    public void findById(String id) {
            User user = userMapper.findById();
            System.out.println(user);
    }

    public boolean existsByEmail(String email) {
        return userMapper.existsByEmail(email);
    }

    public boolean save(User user) {
        return userMapper.save(user) > 0;
    }

    public User findByEmail(String email) {
        return userMapper.findByEmail(email);
    }
}
