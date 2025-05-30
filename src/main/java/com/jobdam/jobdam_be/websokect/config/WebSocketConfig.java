package com.jobdam.jobdam_be.websokect.config;

import com.jobdam.jobdam_be.websokect.interceptor.StompChannelInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${frontEnd.url}")
    private String frontendUrl;

    private final StompChannelInterceptor stompChannelInterceptor;
    //app은 프론트에서 메세지보낼때 prefix 설정
    //프론트는 /app/sendMessage로 메세지 보내면  @MessageMapping("/sendMessage")로 구분함
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic","/queue");//서버-> 클라 경로선언
        registry.setApplicationDestinationPrefixes("/app"); //클라->서버응답용
        registry.setUserDestinationPrefix("/user");//서버->개인 1:1응답용
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOriginPatterns(frontendUrl).withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration){
        registration.interceptors(stompChannelInterceptor);
    }
}
