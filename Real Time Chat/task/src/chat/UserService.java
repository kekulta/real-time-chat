package chat;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    private AtomicInteger counter = new AtomicInteger(0);
    private Map<String, UserItem> users = new ConcurrentHashMap<>();
    private Map<String, String> UUIDs = new ConcurrentHashMap<>();

    private void log() {
        System.out.println("List of users: --");
        for (String i : users.keySet()) {
            System.out.println(i);
        }
        System.out.println("--");
    }

    void addUser(UserItem user) {
        for (String i : users.keySet()) {
            var ID = UUID.randomUUID().toString();
            users.get(i).createDialog(user.getUUID(), ID);
            user.createDialog(users.get(i).getUUID(), ID);
        }
        UUIDs.put(user.getUsername(), user.getUUID());
        user.setOrder(counter.incrementAndGet());
        users.put(user.getUUID(), user);
        log();
    }

    void deleteUserByUUID(String UUID) {
        System.out.println("Disconnected username : \"" + users.get(UUID).getUsername() + "\"");
        for (String i : users.keySet()) {
            users.get(i).deleteDialog(UUID);
        }
        UUIDs.remove(users.get(UUID).getUsername());
        users.remove(UUID);
        log();
    }

    String getUUID(String username) {
        return UUIDs.get(username);
    }

    String getUsername(String UUID) {
        return users.get(UUID).getUsername();
    }

    List<UserItem> getAll() {

        var res = new ArrayList<UserItem>(users.values());
        res.sort(Comparator.comparingInt(UserItem::getOrder));
        return res;
    }


    Boolean isPresent(String UUID) {
        if (UUID == null) return false;
        return users.get(UUID) != null;
    }

    String getDialogID(String first, String second) {
        return users.get(first).getDialog(second);
    }
}
