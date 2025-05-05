package com.jobdam.jobdam_be.user.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EducationLevel {
    HIGH_SCHOOL("고등학교"),
    COLLEGE_2_3("2~3년제"),
    UNIVERSITY_4("4년제"),
    GRADUATE("대학원");

    private final String displayName;
}
