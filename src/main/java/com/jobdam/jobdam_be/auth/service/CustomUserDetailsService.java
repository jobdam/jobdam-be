package com.jobdam.jobdam_be.auth.service;

import com.jobdam.jobdam_be.user.dao.UserDAO;
import com.jobdam.jobdam_be.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

// 로그인 요청 시 아이디(username) 을 기준으로 DB에서 유저를 조회하고, 이를 CustomUserDetails로 감싸서 반환
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserDAO userDAO;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        Long userId = Long.valueOf(id);
        Optional<User> findUser = userDAO.findById(userId);

        return findUser.map(user -> new CustomUserDetails(user)).orElse(null);

    }
}
