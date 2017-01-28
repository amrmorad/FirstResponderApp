package bytes.com.firstresponderapp.model;

import com.google.firebase.database.FirebaseDatabase;

public class ChatRoom {

    private String name;

    private String type;

    public ChatRoom() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ChatRoom(String name) {
        this.name = name;
    }

    public static void addRoom(ChatRoom chatRoom) {
        FirebaseDatabase.getInstance()
                .getReference().child("ChatRoom")
                .push()
                .setValue(chatRoom);
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
