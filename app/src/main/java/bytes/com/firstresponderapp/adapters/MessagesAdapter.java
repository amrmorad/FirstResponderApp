package bytes.com.firstresponderapp.adapters;


import android.content.Context;
import android.view.View;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import bytes.com.firstresponderapp.holders.MessageHolder;
import bytes.com.firstresponderapp.model.ChatMessage;
import bytes.com.firstresponderapp.observer.DownloadCompletedListener;
import bytes.com.firstresponderapp.util.DateUtil;

public class MessagesAdapter extends FirebaseRecyclerAdapter<ChatMessage, MessageHolder> {

    private Context context;
    private DownloadCompletedListener listener;

    public MessagesAdapter(Class<ChatMessage> modelClass, int modelLayout, Class<MessageHolder> viewHolderClass, Query ref, Context context, DownloadCompletedListener listener) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.context = context;
        this.listener = listener;
    }

    public MessagesAdapter(Class<ChatMessage> modelClass, int modelLayout, Class<MessageHolder> viewHolderClass, DatabaseReference ref, Context context, DownloadCompletedListener listener) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void populateViewHolder(MessageHolder viewHolder, final ChatMessage model, int position) {
        viewHolder.messageText.setText(model.getMessageText());
        viewHolder.messageUser.setText(model.getMessageUser());
        if (model.getMessageUri() != null) {
            if (model.getMimeType() != null && !model.getMimeType().isEmpty()) {
                if (model.getMimeType().toLowerCase().contains("video")) {
                    viewHolder.linkTextView.setVisibility(View.VISIBLE);
                    viewHolder.messageText.setVisibility(View.GONE);
                    viewHolder.imageMessage.setVisibility(View.GONE);
                    viewHolder.linkTextView.setText(model.getAttachmentName());
                    viewHolder.linkTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.onDownloadFile(model);
                        }
                    });
                } else if (model.getMimeType().toLowerCase().contains("image")) {
                    viewHolder.imageMessage.setVisibility(View.VISIBLE);
                    viewHolder.linkTextView.setVisibility(View.GONE);
                    Glide.with(context).load(model.getMessageUri())
                            .into(viewHolder.imageMessage);
                }
            }
        } else {
            viewHolder.imageMessage.setVisibility(View.GONE);
            viewHolder.linkTextView.setVisibility(View.GONE);
            viewHolder.messageText.setVisibility(View.VISIBLE);
        }
//                imageView.setImageBitmap(model.getImageView());

        // Format the date before showing it
        viewHolder.messageTime.setText(DateUtil.getTimeAgo(model.getMessageTime()));
    }
}
