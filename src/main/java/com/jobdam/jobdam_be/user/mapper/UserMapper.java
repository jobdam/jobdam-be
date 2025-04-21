package com.jobdam.jobdam_be.user.mapper;

import com.jobdam.jobdam_be.user.dto.UserDTO;
import com.jobdam.jobdam_be.user.model.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User findById();
}
