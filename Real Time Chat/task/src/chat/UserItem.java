package chat;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserItem {
    private String username;

    private String UUID;
    private int order;
    @JsonIgnore
    final private Map<String, String> dialogsIDs = new ConcurrentHashMap<>();

    public UserItem() {

    }

    public UserItem(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    @JsonIgnore
    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public void deleteDialog(String UUID) {
        dialogsIDs.remove(UUID);
    }

    public void createDialog(String UUID, String ID) {
        dialogsIDs.put(UUID, ID);
    }

    public String getDialog(String UUID) {
        return dialogsIDs.get(UUID);
    }

    @JsonIgnore
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
