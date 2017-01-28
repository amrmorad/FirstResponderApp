package bytes.com.firstresponderapp.async;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;

import bytes.com.firstresponderapp.observer.DownloadCompletedListener;

public class DownloadFileFromURL extends AsyncTask {

    private Context context;
    private String fileUrl;
    private String fileName;
    private DownloadCompletedListener listener;

    public DownloadFileFromURL(Context context, String fileUrl, String fileName, DownloadCompletedListener listener){
        this.context = context;
        this.fileUrl = fileUrl;
        this.fileName = fileName;
        this.listener = listener;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        File direct = new File(Environment.getDataDirectory()
                + "/FirstResponderApp");

        if (!direct.exists()) {
            direct.mkdirs();
        }

        DownloadManager mgr = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        Uri downloadUri = Uri.parse(fileUrl);
        DownloadManager.Request request = new DownloadManager.Request(
                downloadUri);

        request.setVisibleInDownloadsUi(true);

        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false).setTitle("File Download")
                .setDescription("Downloading files.")
                .setDestinationInExternalPublicDir("/FirstResponderApp", fileName);

        mgr.enqueue(request);

        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                listener.onDownloadCompleted();
            }
        };

        context.registerReceiver(receiver, intentFilter);
        return null;
    }
}
