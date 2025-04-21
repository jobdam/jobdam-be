package com.jobdam.jobdam_be.user.dao;

import com.jobdam.jobdam_be.user.mapper.UserMapper;
import com.jobdam.jobdam_be.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class UserDAO {
    private final UserMapper userMapper;
    @Transactional
    public void test() {
        try{
            User user = userMapper.findById(1);
            System.out.println(user);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
