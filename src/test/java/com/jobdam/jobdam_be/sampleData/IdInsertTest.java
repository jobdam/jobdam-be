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

@Disabled
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
        for(int i=1; i<4; i++) {
            String email = "test"+i+"@gmail.com";
            String encodedPassword = passwordEncoder.encode(password);
            User user = User.builder()
                    .email(email)
                    .password(encodedPassword)
                    .build();
            userDAO.saveEmail(user);
        }
    }


}
