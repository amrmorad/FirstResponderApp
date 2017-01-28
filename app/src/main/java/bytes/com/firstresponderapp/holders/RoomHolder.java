package bytes.com.firstresponderapp.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import bytes.com.firstresponderapp.R;

public class RoomHolder extends RecyclerView.ViewHolder {

    public TextView roomNameTextView;
    public LinearLayout roomLayout;

    public RoomHolder(View itemView) {
        super(itemView);
        roomNameTextView = (TextView) itemView.findViewById(R.id.room_name);
        roomLayout = (LinearLayout) itemView.findViewById(R.id.root);
    }
}
