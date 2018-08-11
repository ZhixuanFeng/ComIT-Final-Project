package com.fengzhixuan.timoc.config;

import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

public class WebSocketAuthorizationSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer
{
    @Override
    protected void configureInbound(final MessageSecurityMetadataSourceRegistry messages) {
        // You can customize your authorization mapping here.
        messages.anyMessage().authenticated();
        messages.simpDestMatchers("/app/room", "/app/game").authenticated()//.hasRole("ADMIN")
                .simpSubscribeDestMatchers("/topic/room", "/topic/game", "/user/").authenticated();
    }
}
