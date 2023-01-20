package chat;

public class Notifying {

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    enum Type {JOIN, LEAVE}

    private Type type;

    private String username;
}
