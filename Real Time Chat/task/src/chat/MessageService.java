package chat;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class MessageService {
    private Map<String, List<Message>> messages = new HashMap<>();

    void addMessage(String ID, Message message) {
        messages.computeIfAbsent(ID, k -> new ArrayList<Message>());
        messages.get(ID).add(message);
    }

    List<Message> getAllMessages(String ID) {
        return messages.getOrDefault(ID, Collections.emptyList());
    }
}


