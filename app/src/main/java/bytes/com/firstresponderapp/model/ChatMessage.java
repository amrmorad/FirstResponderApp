package bytes.com.firstresponderapp.model;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Date;

import bytes.com.firstresponderapp.observer.ServerListener;

public class ChatMessage {
    private String messageText;
    private String messageUser;
    private long messageTime;
    private String mimeType;
    private String roomKey;
    private String attachmentName;
    private String senderImage;

    public String getMessageUri() {
        return messageUri;
    }

    public void setMessageUri(String messageUri) {
        this.messageUri = messageUri;
    }

    private String messageUri;
    private Bitmap imageView;

    public ChatMessage(String messageText, String messageUser, String roomKey) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        messageTime = new Date().getTime();
        this.roomKey = roomKey;
    }

    public ChatMessage(String messageText, String messageUri, String messageUser, String mimeType, String roomKey, String attachmentName) {
        this.messageUri = messageUri;
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.mimeType = mimeType;
        messageTime = new Date().getTime();
        this.roomKey = roomKey;
        this.attachmentName = attachmentName;
    }


    public ChatMessage() {

    }

    public static void uploadFile(final File generalFile, final String mimeType, final ServerListener serverListener, final String roomKey) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://firstresponderapp-ce01f.appspot.com");
        final StorageReference videoRef = storageRef.child("Videos").child(generalFile.getName());
// add File/URI
        videoRef.putFile(Uri.fromFile(generalFile)).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.i("", "");
                serverListener.notifyFailure();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                FirebaseDatabase.getInstance()
                        .getReference().child("ChatMessage")
                        .push()
                        .setValue(new ChatMessage("", downloadUrl.toString(),
                                FirebaseAuth.getInstance()
                                        .getCurrentUser()
                                        .getDisplayName(), mimeType, roomKey, generalFile.getName())
                        ).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        serverListener.notifySuccess();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        serverListener.notifyFailure();
                    }
                });
            }
        });
    }

    public static void uploadFile(Bitmap bitmap, final String roomKey) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://chat-953fc.appspot.com");
        StorageReference mountainImagesRef = storageRef.child("images/ahmed.jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = mountainImagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.i("", "");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                FirebaseDatabase.getInstance()
                        .getReference()
                        .push()
                        .setValue(new ChatMessage("", downloadUrl.toString(),
                                FirebaseAuth.getInstance()
                                        .getCurrentUser()
                                        .getDisplayName(), "image", roomKey, "")
                        );
            }
        });

    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public void setImageView(Bitmap image) {
        this.imageView = image;
    }

    public Bitmap getImageView() {
        return imageView;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getRoomKey() {
        return roomKey;
    }

    public void setRoomKey(String roomKey) {
        this.roomKey = roomKey;
    }


    public String getAttachmentName() {
        return attachmentName;
    }

    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }

    public String getSenderImage() {
        return senderImage;
    }

    public void setSenderImage(String senderImage) {
        this.senderImage = senderImage;
    }
}
