package com.jobdam.jobdam_be.user.service;

import com.jobdam.jobdam_be.global.exception.type.CommonErrorCode;
import com.jobdam.jobdam_be.user.dao.UserDAO;
import com.jobdam.jobdam_be.user.dto.UserInitProfileDTO;
import com.jobdam.jobdam_be.user.dto.UserProfileDTO;
import com.jobdam.jobdam_be.user.exception.UserErrorCode;
import com.jobdam.jobdam_be.user.exception.UserException;
import com.jobdam.jobdam_be.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDAO userDAO;

    public void test() {
        throw new UserException(CommonErrorCode.USER_NOT_FOUND);
    }


    public void initProfile(Long userId, UserInitProfileDTO dto, String imgUrl) {
        User updateUser = User.builder()
                .id(userId)
                .name(dto.getName())
                .birthday(dto.getBirthday())
                .targetCompanySize(dto.getTargetCompanySize())
                .profileImgUrl(imgUrl)
                .jobCode(dto.getJobCode())
                .jobDetailCode(dto.getJobDetailCode())
                .experienceType(dto.getExperienceType())
                .educationLevel(dto.getEducationLevel())
                .educationStatus(dto.getEducationStatus())
                .build();
        try {
            if (!userDAO.initProfile(updateUser))
                throw new UserException(UserErrorCode.PROFILE_UPDATE_FAILED);
        } catch (UserException e) {
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new UserException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<UserProfileDTO> getUserProfile(Long userId) {
        Optional<User> optionalUser;
        try {
            optionalUser = userDAO.findById(userId);

        } catch (Exception e) {
            throw new UserException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }

        if (optionalUser.isEmpty())
            throw new UserException(CommonErrorCode.USER_NOT_FOUND);

        User user = optionalUser.get();
        UserProfileDTO dto = UserProfileDTO.builder()
                .name(user.getName())
                .birthday(user.getBirthday())
                .targetCompanySize(user.getTargetCompanySize())
                .profileImgUrl(user.getProfileImgUrl())
                .jobCode(user.getJobCode())
                .jobDetailCode(user.getJobDetailCode())
                .experienceType(user.getExperienceType())
                .educationLevel(user.getEducationLevel())
                .educationStatus(user.getEducationStatus())
                .build();

        return ResponseEntity.ok(dto);
    }
}
