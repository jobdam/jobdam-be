//package com.jobdam.jobdam_be.websokect.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.Message;
//import org.springframework.security.authorization.AuthorizationManager;
//import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
//import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
//
//@Configuration
//@EnableWebSocketSecurity
//public class WebSocketSecurityConfig {
//
//    @Bean
//    AuthorizationManager<Message<?>> messageAuthorizationManager(
//            MessageMatcherDelegatingAuthorizationManager.Builder messages) {
//        return messages
//                .nullDestMatcher().permitAll() // 목적지 없는 메시지도 인증 허용
//                .anyMessage().permitAll()      // 나머지 모든 메시지도 인증 허용
//                .build();
//    }
//}
