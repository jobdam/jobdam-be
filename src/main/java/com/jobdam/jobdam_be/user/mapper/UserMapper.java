package com.jobdam.jobdam_be.user.mapper;

import com.jobdam.jobdam_be.user.model.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User findById();

    User findByEmail(String email);

    boolean existsByEmail(String email);

    int save(User user);
}
