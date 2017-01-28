package bytes.com.firstresponderapp.ui.fragment;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import bytes.com.firstresponderapp.ui.activity.MainActivity;
import bytes.com.firstresponderapp.R;
import bytes.com.firstresponderapp.adapters.RoomAdapter;
import bytes.com.firstresponderapp.holders.RoomHolder;
import bytes.com.firstresponderapp.model.ChatRoom;
import bytes.com.firstresponderapp.ui.CreateRoomDialog;

public class SideMenuFragment extends Fragment implements View.OnClickListener {

    private RecyclerView roomRecyclerView;
    private boolean isLoaded = false;
    private RoomAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View sidemenuView = inflater.inflate(R.layout.fragment_sidemenu, null, false);
        Log.i("", "query :");
        sidemenuView.findViewById(R.id.add_room).setOnClickListener(this);
        initUI(sidemenuView);
        return sidemenuView;
    }

    public void initRoomsAdapter() {
        try {
            if (roomRecyclerView == null)
                initUI(getView());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Query query = FirebaseDatabase.getInstance().getReference().child("ChatRoom");
        Log.i("", "query :" + query.toString());
        adapter = new RoomAdapter(ChatRoom.class, R.layout.room, RoomHolder.class, query, ((MainActivity) getActivity()), getActivity());
        roomRecyclerView.setAdapter(adapter);

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                if (!isLoaded) {
                    if (getActivity() != null && !getActivity().isFinishing())
                        ((MainActivity) getActivity()).onRoomSelected(adapter.getRef(0).getKey());
                }
            }
        });

    }

    private void initUI(View rootView) {
        roomRecyclerView = (RecyclerView) rootView.findViewById(R.id.list_of_rooms);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        Drawable horizontalDivider = ContextCompat.getDrawable(getActivity(), R.drawable.horizontal_divider);
        dividerItemDecoration.setDrawable(horizontalDivider);
        roomRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    public void onClick(View view) {
        CreateRoomDialog dialog = new CreateRoomDialog(getActivity());
        dialog.show();
    }
}
