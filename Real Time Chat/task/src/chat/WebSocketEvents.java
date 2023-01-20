package chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;
import java.util.Optional;

@Component
public class WebSocketEvents {

    final
    UserService userService;

    private final SimpMessageSendingOperations messagingTemplate;

    public WebSocketEvents(UserService userService, SimpMessageSendingOperations messagingTemplate) {
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
    }


    @EventListener
    public void connect(SessionSubscribeEvent event) {
        System.out.println("Someone subscribed");

    }

    @EventListener
    public void disconnect(SessionDisconnectEvent event) {
        System.out.println("Someone disconnected");


        Principal user = Optional.ofNullable(event.getUser()).orElse(new StompPrincipal(null));

        String UUID = user.getName();

        if (UUID != null) {


            Notifying notify = new Notifying();

            notify.setType(Notifying.Type.LEAVE);
            notify.setUsername(userService.getUsername(UUID));

            userService.deleteUserByUUID(UUID);

            messagingTemplate.convertAndSend("/topic/notify", notify);
        }

    }
}