package bytes.com.firstresponderapp.ui.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import bytes.com.firstresponderapp.R;
import bytes.com.firstresponderapp.adapters.MessagesAdapter;
import bytes.com.firstresponderapp.async.DownloadFileFromURL;
import bytes.com.firstresponderapp.holders.MessageHolder;
import bytes.com.firstresponderapp.model.ChatMessage;
import bytes.com.firstresponderapp.observer.DownloadCompletedListener;
import bytes.com.firstresponderapp.observer.RoomListener;
import bytes.com.firstresponderapp.observer.ServerListener;
import bytes.com.firstresponderapp.ui.fragment.SideMenuFragment;
import bytes.com.firstresponderapp.util.RealPathUtil;
import bytes.com.firstresponderapp.util.UtilityMethods;

public class MainActivity extends BaseActivity implements ServerListener, RoomListener, DownloadCompletedListener {

    public final static int SIGN_IN_REQUEST_CODE = 100;
    private MessagesAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String selectedRoomKey;
    private DrawerLayout drawerLayout;
    private ChatMessage selectedChatMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Start sign in/sign up activity
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .build(),
                    SIGN_IN_REQUEST_CODE
            );
        } else {
            initRoomsAdapter();
//            displayChatMessages();
        }

        FloatingActionButton fab =
                (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText) findViewById(R.id.input);

                // Read the input field and push a new instance
                // of ChatMessage to the Firebase database
                FirebaseDatabase.getInstance()
                        .getReference().child("ChatMessage")
                        .push()
                        .setValue(new ChatMessage(input.getText().toString(),
                                FirebaseAuth.getInstance()
                                        .getCurrentUser()
                                        .getDisplayName(),
                                selectedRoomKey)
                        );

                // Clear the input
                input.setText("");
            }
        });
    }

    private void initRoomsAdapter(){
        SideMenuFragment sideMenuFragment = (SideMenuFragment) getFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        sideMenuFragment.initRoomsAdapter();
    }

    private void initUI() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeRefreshLayout.setEnabled(false);
//        swipeRefreshLayout.setRefreshing(true);

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                (Toolbar) findViewById(R.id.toolbar),
//                android.R.drawable.menuitem_background,//R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description */
                R.string.navigation_drawer_close  /* "close drawer" description */
        ) {

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                closeSoftKeypad();
            }
        };

        // Set the drawer toggle as the DrawerListener
        drawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_navigation_menu);
    }

    protected void closeSoftKeypad() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void displayChatMessages() {
//        swipeRefreshLayout.setRefreshing(true);
        final RecyclerView listOfMessages = (RecyclerView) findViewById(R.id.list_of_messages);
        Query query = FirebaseDatabase.getInstance().getReference().child("ChatMessage").orderByChild("roomKey").equalTo(selectedRoomKey);

        if (adapter != null)
            adapter.cleanup();

        adapter = new MessagesAdapter(ChatMessage.class, R.layout.message, MessageHolder.class, query, MainActivity.this, MainActivity.this);

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                listOfMessages.smoothScrollToPosition(adapter.getItemCount());
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
                super.onItemRangeChanged(positionStart, itemCount, payload);
                listOfMessages.smoothScrollToPosition(adapter.getItemCount());
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                listOfMessages.smoothScrollToPosition(adapter.getItemCount());
                if (!isFinishing())
                    swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                listOfMessages.smoothScrollToPosition(adapter.getItemCount());
            }
        });

        listOfMessages.setAdapter(adapter);
    }

    public void onFabAttachButtonClicked(View view) {
        startUpload();
    }

    private void startUpload() {
        if (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            if (hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                startCameraIntent();
            } else
                requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, "We need read permission", READ_FILE_REQUEST_CODE);
        } else {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, getString(R.string.wrtite_file_permission), WRITE_FILE_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_FILE_REQUEST_CODE || requestCode == READ_FILE_REQUEST_CODE)
            startUpload();
        else if (requestCode == WRITE_MODEL_FILE_REQUEST_CODE)
            downloadFile(selectedChatMessage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this,
                        "Successfully signed in. Welcome!",
                        Toast.LENGTH_LONG)
                        .show();
                displayChatMessages();

                initRoomsAdapter();
            } else {
                Toast.makeText(this,
                        "We couldn't sign you in. Please try again later.",
                        Toast.LENGTH_LONG)
                        .show();

                // Close the app
                finish();
            }
        }


        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                final boolean isCamera;
                if (data == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action.equals("inline data");
                    }
                }

                Uri selectedImageUri;

                if (isCamera) {
                    selectedImageUri = UtilityMethods.outputFileUri;
                } else {
                    selectedImageUri = data == null ? null : data.getData();
                }


                if (selectedImageUri == null) {

                } else {
                    String selectedImagePath = getPath(selectedImageUri);

                    File file = null;

                    if (selectedImagePath != null)
                        file = new File(selectedImagePath);
                    else
                        file = new File(getPath(selectedImageUri));

                    ContentResolver cr = this.getContentResolver();
                    String mime = cr.getType(selectedImageUri);
                    swipeRefreshLayout.setRefreshing(true);
                    ChatMessage.uploadFile(file, mime, MainActivity.this, selectedRoomKey);//data.getData());
                }
            }
        }

    }

    private void startCameraIntent() {
        UtilityMethods.createImage(this);
        int YOUR_SELECT_PICTURE_REQUEST_CODE = 1;

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, YOUR_SELECT_PICTURE_REQUEST_CODE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_sign_out) {
            AuthUI.getInstance().signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(MainActivity.this,
                                    "You have been signed out.",
                                    Toast.LENGTH_LONG)
                                    .show();

                            // Close activity
                            finish();
                        }
                    });
        }
        return true;
    }


    private Bitmap decodeFile(File f) {
        Log.d("file decodig ", "..." + f.getAbsolutePath());
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream is = new FileInputStream(f);
            Bitmap decodedBitmap = BitmapFactory.decodeStream(is, null, o);
            is.close();

            final int REQUIRED_SIZE = 560;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            is = new FileInputStream(f);
            decodedBitmap = BitmapFactory.decodeStream(is, null, o2);
            is.close();
            return decodedBitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getPath(Uri uri) {
        String path = RealPathUtil.getPath(this, uri);
        return path;
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;

        Cursor cursor = getApplicationContext().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            if (idx > -1)
                result = cursor.getString(idx);
            else
                result = null;

            cursor.close();
        }
        return result;
    }

    @Override
    public void notifySuccess() {
        if (!isFinishing())
            swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void notifyFailure() {
        if (!isFinishing())
            swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRoomSelected(String roomKey) {
        this.selectedRoomKey = roomKey;
        displayChatMessages();
        drawerLayout.closeDrawer(Gravity.LEFT);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT))
            drawerLayout.closeDrawer(Gravity.LEFT);
        else
            super.onBackPressed();
    }

    @Override
    public void onDownloadCompleted() {
        UtilityMethods.showSnackbar(getString(R.string.download_completed), drawerLayout, MainActivity.this);
    }

    @Override
    public void onDownloadFile(ChatMessage chatMessage) {
        if (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE))
            downloadFile(chatMessage);//new DownloadFileFromURL(this, chatMessage.getMessageUri(), chatMessage.getAttachmentName(), this).execute();
        else {
            this.selectedChatMessage = chatMessage;
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, getString(R.string.wrtite_file_permission), WRITE_MODEL_FILE_REQUEST_CODE);
        }
    }

    private void downloadFile(ChatMessage chatMessage) {
        new DownloadFileFromURL(this, chatMessage.getMessageUri(), chatMessage.getAttachmentName(), this).execute();
    }

    @Override
    protected View getRootView() {
        return drawerLayout;
    }
}
