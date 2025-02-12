package com.moz1mozi.chat.config

import com.sun.security.auth.UserPrincipal
import org.springframework.context.annotation.Configuration
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import org.springframework.web.socket.server.support.DefaultHandshakeHandler
import java.security.Principal

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig(
): WebSocketMessageBrokerConfigurer{

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry
            .addEndpoint("/chat")
            .setAllowedOrigins("http://localhost:3000")
            .setHandshakeHandler(object : DefaultHandshakeHandler() {
                override fun determineUser(
                    request: ServerHttpRequest,
                    wsHandler: WebSocketHandler,
                    attributes: Map<String, Any>
                ): Principal? {
                    if (request is ServletServerHttpRequest) {
                        val token = request.servletRequest.cookies?.find { it.name == "username" }?.value

                        return UserPrincipal(token);
                    }
                    return null
                }
            })
            .withSockJS()
    }

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.enableSimpleBroker("/sub", "/queue", "/topic")
        registry.setApplicationDestinationPrefixes("/pub")
        registry.setUserDestinationPrefix("/user")
    }
}