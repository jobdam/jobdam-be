package com.jobdam.jobdam_be.user.service;

import com.jobdam.jobdam_be.user.dao.UserDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDAO userDAO;
    public void test() {
        userDAO.findById(1L);
    }
}
