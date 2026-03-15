package org.example.porti.config.interceptor;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.example.porti.chat.room.ChatRoomService;
import org.example.porti.user.model.AuthUserDetails;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CheckRoomAuthInterceptor implements ChannelInterceptor {
    private final ChatRoomService chatRoomService;

    @Override
    public @Nullable Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if(StompCommand.SUBSCRIBE.equals(accessor.getCommand())){
            String path = accessor.getDestination();

            if(path!= null && path.startsWith("/sub/chat/room/")) {
                Long roomIdx = Long.parseLong(path.substring("/sub/chat/room/".length()));

                Map<String, Object> attributes = accessor.getSessionAttributes();
                if(attributes == null || !attributes.containsKey("user")) {
                    throw new IllegalArgumentException();
                }
                Authentication authentication = (Authentication) attributes.get("user");
                AuthUserDetails user = (AuthUserDetails) authentication.getPrincipal();

                if(!chatRoomService.isParticipant(roomIdx, user.getIdx())) {
                    throw new MessageDeliveryException("unAuthorized");
                }
            }
        }
        return message;
    }
}
