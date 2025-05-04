package com.jobdam.jobdam_be.websokect.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class WebSocketDebugController {

    private final SimpUserRegistry simpUserRegistry;

    @GetMapping("/debug/websocket/users")
    public List<String> getConnectedUsers() {
        simpUserRegistry.getUsers().forEach(user -> {
            log.info("유저이름 = {}", user.getName()); // principal.getName()이랑 비교
        });
        return simpUserRegistry.getUsers().stream()
                .flatMap(user -> user.getSessions().stream()
                        .flatMap(session -> session.getSubscriptions().stream()
                                .map(subscription -> String.format(
                                        "User: %s | Session: %s | Destination: %s",
                                        user.getName(), session.getId(), subscription.getDestination()
                                ))
                        )
                )
                .collect(Collectors.toList());
    }
}