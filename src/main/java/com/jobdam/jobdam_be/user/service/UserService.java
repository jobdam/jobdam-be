package com.jobdam.jobdam_be.user.service;

import com.jobdam.jobdam_be.global.exception.type.CommonErrorCode;
import com.jobdam.jobdam_be.user.dao.UserDAO;
import com.jobdam.jobdam_be.user.exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDAO userDAO;
    public void test() {
        throw new UserException(CommonErrorCode.USER_NOT_FOUND);
    }
}
