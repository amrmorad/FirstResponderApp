package bytes.com.firstresponderapp.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import bytes.com.firstresponderapp.R;
import bytes.com.firstresponderapp.model.ChatRoom;

public class CreateRoomDialog extends Dialog implements View.OnClickListener {

    private EditText roomNameEditText;

    public CreateRoomDialog(Context context) {
        super(context);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_create_room);
        roomNameEditText = (EditText) findViewById(R.id.edittext_room_name);
        findViewById(R.id.button_add_room).setOnClickListener(this);
        setTitle("Create Room");
    }


    @Override
    public void onClick(View view) {
        String roomName = roomNameEditText.getText().toString();
        ChatRoom chatRoom = new ChatRoom(roomName);
        ChatRoom.addRoom(chatRoom);
        cancel();
    }
}
