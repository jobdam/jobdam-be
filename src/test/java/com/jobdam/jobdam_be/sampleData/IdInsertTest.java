package com.jobdam.jobdam_be.sampleData;

import com.jobdam.jobdam_be.user.dao.UserDAO;
import com.jobdam.jobdam_be.user.model.User;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;


@SpringBootTest
public class IdInsertTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDAO userDAO;

    @Test
    @Transactional
    @Commit
    public void insertUser(){
        String password = "qweQWE12!";
        for(int i=5; i<6; i++) {
            String email = "test"+i+"@gmail.com";
            String encodedPassword = passwordEncoder.encode(password);
            User user = User.builder()
                    .email(email)
                    .name("잡담"+i)
                    .password(encodedPassword).build();
            userDAO.saveEmail(user);
        }
    }


}
