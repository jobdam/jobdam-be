package com.jobdam.jobdam_be.websokect.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    //메시지 브로커 설정 추가가능("/topic/chat, /topic/signal")
    //app은 프론트에서 메세지보낼때 prefix 설정
    //프론트는 /app/sendMessage로 메세지 보내면  @MessageMapping("/sendMessage")로 구분함
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic/signal");
        registry.setApplicationDestinationPrefixes("/app");
    }

    //각 웹소켓 연결마다 다른 엔드포인트 설정(프론트에서 서버로 연결시)
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //registry.addEndpoint("/ws/chat").setAllowedOrigins("http://localhost:5173").withSockJS();
        registry.addEndpoint("/ws/signal").setAllowedOrigins("http://localhost:5173").withSockJS();
    }
}
