package com.jobdam.jobdam_be.websokect.config;

import com.jobdam.jobdam_be.websokect.interceptor.StompChannelInterceptor;
import lombok.RequiredArgsConstructor;
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

    private final StompChannelInterceptor stompChannelInterceptor;
    //app은 프론트에서 메세지보낼때 prefix 설정
    //프론트는 /app/sendMessage로 메세지 보내면  @MessageMapping("/sendMessage")로 구분함
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic/signal");//서버-> 클라 전체응답용
        registry.setApplicationDestinationPrefixes("/app"); //클라->서버응답용
        registry.setUserDestinationPrefix("/user/queue");//서버->개인 1:1응답용
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOrigins("http://localhost:5173").withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration){
        registration.interceptors(stompChannelInterceptor);
    }
}
