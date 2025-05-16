package com.jobdam.jobdam_be.user.service;

import com.jobdam.jobdam_be.global.exception.type.CommonErrorCode;
import com.jobdam.jobdam_be.user.dao.UserDAO;
import com.jobdam.jobdam_be.user.dto.UserInitProfileDTO;
import com.jobdam.jobdam_be.user.dto.UserMatchingProfileDTO;
import com.jobdam.jobdam_be.user.dto.UserProfileDTO;
import com.jobdam.jobdam_be.user.exception.UserErrorCode;
import com.jobdam.jobdam_be.user.exception.UserException;
import com.jobdam.jobdam_be.user.model.Resume;
import com.jobdam.jobdam_be.user.model.User;
import com.jobdam.jobdam_be.user.model.UserJobJoinModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDAO userDAO;
    private final ModelMapper modelMapper;

    public void initProfile(Long userId, UserInitProfileDTO dto, String imgUrl) {
        User updateUser = buildUser(userId, imgUrl, dto);

        try {
            if (!userDAO.initProfile(updateUser))
                throw new UserException(UserErrorCode.PROFILE_UPDATE_FAILED);
        } catch (UserException e) {
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new UserException(CommonErrorCode.INTERNAL_SERVER_ERROR, e);
        }
    }

    public ResponseEntity<UserProfileDTO> getUserProfile(Long userId) {
        Optional<User> optionalUser;
        try {
            optionalUser = userDAO.findById(userId);

        } catch (Exception e) {
            throw new UserException(CommonErrorCode.INTERNAL_SERVER_ERROR, e);
        }

        if (optionalUser.isEmpty())
            throw new UserException(CommonErrorCode.USER_NOT_FOUND);

        User user = optionalUser.get();
        UserProfileDTO dto = UserProfileDTO.builder()
                .email(user.getEmail())
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

    public String getUserProfileImage(Long userId) {
        return userDAO.findProfileImgUrlById(userId);
    }

    public String getUserResumeUrl(Long userId) {
        return userDAO.findResumeUrlById(userId);
    }

    public void updateProfile(Long userId, String imgUrl, UserInitProfileDTO dto) {
        User updateUser = buildUser(userId, imgUrl, dto);

        try {
            if (!userDAO.updateProfile(updateUser))
                throw new UserException(UserErrorCode.PROFILE_UPDATE_FAILED);
        } catch (UserException e) {
            throw e;
        } catch (Exception e) {
            throw new UserException(CommonErrorCode.INTERNAL_SERVER_ERROR, e);
        }
    }

    /**
     * 이력서 저장
     *
     * @param resume - 새롭게 생성된 이력서 모델
     */
    public void savePDF(Resume resume) {
        userDAO.saveOrUpdateResume(resume);
    }

    private User buildUser(Long userId, String imgUrl, UserInitProfileDTO dto) {
        return User.builder()
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
    }

    //매칭시 먼저 선택할 내정보가져오기
    public UserMatchingProfileDTO.Response getMyMatchingProfile(Long userId) {
        User user = userDAO.findById(userId)
                .orElseThrow(() -> new UserException(CommonErrorCode.USER_NOT_FOUND));
        return modelMapper.map(user, UserMatchingProfileDTO.Response.class);
    }

    //userJobJoin 가져오기(jobCode 말고 한글로) chat에서 사용할 줄 알았는데
    //생각해보니 UI에서 선택한거 보여줘야해서 사용 보류..
    public UserJobJoinModel getUserJobJoinModel(Long userId){
        return userDAO.findUserJobJoinById(userId)
                .orElseThrow(() -> new UserException(CommonErrorCode.USER_NOT_FOUND));
    }

    //유저정보 id로 가져오기
    public User getUserById(Long userId){
        return userDAO.findById(userId)
                .orElseThrow(() -> new UserException(CommonErrorCode.USER_NOT_FOUND));
    }
}
