package com.jobdam.jobdam_be.user.service;

import com.jobdam.jobdam_be.user.dao.UserDAO;
import com.jobdam.jobdam_be.user.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDAO userDAO;
    public void test(UserDTO userDTO) {
        userDAO.test(userDTO);
    }
}
