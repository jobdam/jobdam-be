package com.jobdam.jobdam_be.user.dao;

import com.jobdam.jobdam_be.user.dto.UserDTO;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UserDAO {
    @Transactional
    public void test(UserDTO userDTO) {
        try{

        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
