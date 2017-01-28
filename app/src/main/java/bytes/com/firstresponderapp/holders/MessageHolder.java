package bytes.com.firstresponderapp.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import bytes.com.firstresponderapp.R;

public class MessageHolder extends RecyclerView.ViewHolder {

    public TextView messageText, messageUser, messageTime;
    public ImageView imageMessage;
    public TextView linkTextView;

    public MessageHolder(View itemView) {
        super(itemView);
        messageText = (TextView) itemView.findViewById(R.id.message_text);
        messageUser = (TextView) itemView.findViewById(R.id.message_user);
        messageTime = (TextView) itemView.findViewById(R.id.message_time);
        imageMessage = (ImageView) itemView.findViewById(R.id.image_view);
        linkTextView = (TextView) itemView.findViewById(R.id.download_text_view);
    }
}
