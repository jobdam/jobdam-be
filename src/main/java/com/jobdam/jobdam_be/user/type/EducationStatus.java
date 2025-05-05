package com.jobdam.jobdam_be.user.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EducationStatus {
    IN_PROGRESS("재학 중"),
    GRADUATED("졸업"),
    LEAVE("휴학");

    private final String displayName;

}
