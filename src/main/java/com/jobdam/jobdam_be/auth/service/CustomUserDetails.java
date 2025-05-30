package com.jobdam.jobdam_be.auth.service;

import com.jobdam.jobdam_be.user.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// 로그인 성공 시, 또는 토큰 필터를 통해 SecurityContext 에 저장되는 유저 정보를 담은 객체
public class CustomUserDetails implements UserDetails {

    private final Long userId;
    private final String name;
    private final String profileImgUrl;
    private final String password;

    public CustomUserDetails(User user) {
        this.userId = user.getId();
        this.name = user.getName();
        this.profileImgUrl = user.getProfileImgUrl();
        this.password = user.getPassword();
    }

    // 계정이 만료되지 않았는가?
    // JWT는 로그인 시 발급된 토큰만으로 인증을 진행하며, 서버에서 계정 만료 상태를 체크하지 않기 때문에 무의미함.
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정이 잠기지 않았는가?
    // JWT는 stateless 인증 방식이라, 잠금 상태를 서버 세션에 유지하지 않음. 별도 체크 필요 시 토큰 발급 시점에 검증해야 함.
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 비밀번호가 만료되지 않았는가?
    // 비밀번호 만료 여부도 토큰 발급 전에 처리하는 것이 일반적. JWT 발급 이후에는 비밀번호 상태를 확인하지 않음.
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정이 활성화 상태인가?
    // 계정 활성화 여부도 JWT를 발급할 때 검증하며, 이후에는 서버에 요청을 하지 않기 때문에 런타임에 체크할 일이 없음.
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();

        // HACK: 현재는 USER 로 고정해두지만, 추후 관리자 권한을 추가하면 변경해야 함
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
//        authorities.add(new SimpleGrantedAuthority(user.getRole));
//        또는
//        authorities.add(new GrantedAuthority() {
//            @Override
//            public String getAuthority() {
//                return user.getRole();
//            }
//        });

        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return String.valueOf(userId);
    }

    public String getRealName() { return name; }
    public String getProfileImageUrl() { return profileImgUrl; }
}
