package com.jobdam.jobdam_be.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OAuthUserDto {

    private String providerId;
    private Long id;


}
