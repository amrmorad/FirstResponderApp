package bytes.com.firstresponderapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import bytes.com.firstresponderapp.R;
import bytes.com.firstresponderapp.holders.RoomHolder;
import bytes.com.firstresponderapp.model.ChatRoom;
import bytes.com.firstresponderapp.observer.RoomListener;

public class RoomAdapter extends FirebaseRecyclerAdapter<ChatRoom, RoomHolder> {

    private RoomListener roomListener;
    private int selectedRoomPosition;
    private Context context;

    public RoomAdapter(Class<ChatRoom> modelClass, int modelLayout, Class<RoomHolder> viewHolderClass, Query ref,RoomListener roomListener, Context context) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.roomListener = roomListener;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    protected void populateViewHolder(RoomHolder viewHolder, ChatRoom model, final int position) {
        viewHolder.roomNameTextView.setText(model.getName());
        if (selectedRoomPosition == position)
            viewHolder.roomLayout.setBackgroundColor(ActivityCompat.getColor(context, R.color.selected_color));
        else
            viewHolder.roomLayout.setBackgroundColor(Color.WHITE);

        viewHolder.roomLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key = RoomAdapter.this.getRef(position).getKey();
                roomListener.onRoomSelected(key);
                selectedRoomPosition = position;
                notifyDataSetChanged();
            }
        });
    }
}
