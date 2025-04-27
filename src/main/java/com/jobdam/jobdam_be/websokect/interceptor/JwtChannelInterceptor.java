package com.jobdam.jobdam_be.websokect.interceptor;

import com.jobdam.jobdam_be.auth.provider.JwtProvider;
import com.jobdam.jobdam_be.auth.service.CustomUserDetails;
import com.jobdam.jobdam_be.auth.service.CustomUserDetailsService;
import com.jobdam.jobdam_be.websokect.exception.WebSocketException;
import com.jobdam.jobdam_be.websokect.exception.type.WebSocketErrorCode;
import com.jobdam.jobdam_be.websokect.sessionTracker.WebSocketSessionTracker;
import com.jobdam.jobdam_be.websokect.sessionTracker.domain.model.BaseSessionInfo;
import com.jobdam.jobdam_be.websokect.sessionTracker.registry.SessionTrackerRegistry;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.util.StringUtil;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtProvider jwtProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final SessionTrackerRegistry trackerRegistry;


    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);//불변객체를 수정할수 있게함.

        if(StompCommand.CONNECT.equals(accessor.getCommand())) {//연결 메세지인지 확인!
            String jwtToken = accessor.getFirstNativeHeader("Authorization");
            Long userId = validateJwtToken(jwtToken); //토큰 검증

            String purpose = accessor.getFirstNativeHeader("purpose");//match,chat,signal구분
            validatePurpose(purpose);

            WebSocketSessionTracker tracker = trackerRegistry.getTracker(purpose);
            //단계 2 roomID가져오기(클라가 chatroomId : 135ab나 matchroomId : raa3 이런식으로 보내는데)
            //tracker에서 일치하는 roomkey(chatroomId)를 가져와서 클라헤더에서 id(135ab)를 꺼냄
            String roomId = accessor.getFirstNativeHeader(tracker.getRoomKeyHeader());
            validateRoomId(roomId);
            //유저 정보를 스프링시큐리티랑 결합해서 웹소켓에 넣음
            //현재는 db까지 조회해와서 전부넣는구조. 유저id만 넣을것인가. 전부넣을것인가 판단해야함
            CustomUserDetails customUserDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(userId.toString());
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
            accessor.setUser(authenticationToken); //유저 정보
            Objects.requireNonNull(accessor.getSessionAttributes()).put("baseSessionInfo", BaseSessionInfo.builder()//세션구분정보
                    .purpose(purpose)
                    .roomId(roomId)
                    .build());
        }
        return message;
    }

    private Long validateJwtToken(String jwtToken){
        if(Objects.isNull(jwtToken))
            throw new WebSocketException(WebSocketErrorCode.TOKEN_MISSING);

        if(!jwtToken.startsWith("Bearer ")){
            throw new WebSocketException(WebSocketErrorCode.JWT_INVALID);
        }
        jwtToken = jwtToken.substring(7);

        try{
           Long userId = jwtProvider.getUserId(jwtToken);//검증+유저 아이디 추출!
            if(jwtProvider.isExpired(jwtToken)){//jwt 만료체크!
                throw new WebSocketException(WebSocketErrorCode.JWT_EXPIRED);
            }
            return userId;
        }catch (JwtException e){
            throw new WebSocketException(WebSocketErrorCode.JWT_INVALID);
        }catch (Exception e){
            throw new WebSocketException(WebSocketErrorCode.JWT_UNKNOWN_ERROR);
        }
    }

    private void validatePurpose(String purpose){
        if(Objects.isNull(purpose)){
            throw new WebSocketException(WebSocketErrorCode.MISSING_PURPOSE);
        }
        if(!trackerRegistry.checkKey(purpose)){
            throw new WebSocketException(WebSocketErrorCode.INVALID_PURPOSE);
        }
    }

    private void validateRoomId(String roomId){
        if(Objects.isNull(roomId)){
            throw new WebSocketException(WebSocketErrorCode.MISSING_ROOM_ID);
        }
    }
}
