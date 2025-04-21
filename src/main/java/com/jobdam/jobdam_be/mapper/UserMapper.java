package com.jobdam.jobdam_be.mapper;

import com.jobdam.jobdam_be.user.dto.UserDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    UserDTO getUserById(int id);
}
