package bytes.com.firstresponderapp.observer;

import bytes.com.firstresponderapp.model.ChatMessage;

public interface DownloadCompletedListener {

    void onDownloadCompleted();

    void onDownloadFile(ChatMessage chatMessage);

}
