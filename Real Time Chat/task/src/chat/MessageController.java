package chat;


import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Objects;


@Controller
public class MessageController {
    public static final String PUBLIC = "PUBLIC";

    private final MessageService messageService;

    private final SimpMessageSendingOperations messagingTemplate;

    private final UserService userService;

    public MessageController(MessageService messageService, UserService userService, SimpMessageSendingOperations messagingTemplate) {
        this.messageService = messageService;
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
    }


    @GetMapping("/users")
    public ResponseEntity<List<UserItem>> fetchUsers() {
        return ResponseEntity.ok(userService.getAll());
    }

    @MessageMapping("/chat.addUser")
    @SendToUser("/topic/hello")
    public ResponseEntity<String> addUser(@RequestBody UserItem user, SimpMessageHeaderAccessor headerAccessor) {
        user.setUUID(headerAccessor.getUser().getName());

        userService.addUser(user);

        Notifying notify = new Notifying();

        notify.setType(Notifying.Type.JOIN);
        notify.setUsername(user.getUsername());

        messagingTemplate.convertAndSend("/topic/notify", notify);

        return ResponseEntity.ok("Ok");
    }


    @MessageMapping("/message")
    public void directMessage(@Payload Message message, SimpMessageHeaderAccessor headerAccessor) {
        if (Objects.equals(message.getReceiver(), "PUBLIC")) {
            messageService.addMessage(PUBLIC, message);
            System.out.println("Send to everyone");
            messagingTemplate.convertAndSend("/topic/PUBLIC", List.of(message));
        } else {
            String dialogID = userService.getDialogID(userService.getUUID(message.getSender()), userService.getUUID(message.getReceiver()));
            messageService.addMessage(dialogID, message);

            messagingTemplate.convertAndSendToUser(userService.getUUID(message.getReceiver()), "/topic/direct/" + message.getSender(), List.of(message));

            messagingTemplate.convertAndSendToUser(userService.getUUID(message.getReceiver()), "/topic/direct", message);

            messagingTemplate.convertAndSendToUser(userService.getUUID(message.getSender()), "/topic/direct/" + message.getReceiver(), List.of(message));
        }

    }

    @MessageMapping("/direct/{user}")
    public void getDialog(@RequestBody UserItem user, SimpMessageHeaderAccessor headerAccessor) {
        String UUID = headerAccessor.getUser().getName();
        if (Objects.equals(user.getUsername(), PUBLIC)) {
            System.out.println("send to: " + "/topic/" + user.getUsername());
            messagingTemplate.convertAndSendToUser(UUID, "/topic/" + user.getUsername(), messageService.getAllMessages(PUBLIC));
            return;
        }
        System.out.println(user + UUID);
        String dialogID = userService.getDialogID(userService.getUUID(user.getUsername()), UUID);
        messagingTemplate.convertAndSendToUser(UUID, "/topic/direct/" + user.getUsername(), messageService.getAllMessages(dialogID));
    }

}
