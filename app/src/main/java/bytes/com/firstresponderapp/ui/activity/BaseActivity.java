package bytes.com.firstresponderapp.ui.activity;

import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import bytes.com.firstresponderapp.util.UtilityMethods;

public abstract class BaseActivity extends AppCompatActivity {

    public final static int WRITE_FILE_REQUEST_CODE = 1000, READ_FILE_REQUEST_CODE = 2000, WRITE_MODEL_FILE_REQUEST_CODE = 3000;

    public void requestPermission(final String permission, String permissionExplanation, final int requestCode) {
        requestPermission(permission, permissionExplanation, requestCode, getRootView());
    }

    protected abstract View getRootView();

    public void requestPermission(final String permission, String permissionExplanation, final int requestCode, View view) {

        boolean explainPermission = ActivityCompat.shouldShowRequestPermissionRationale(this,
                permission);

        if (explainPermission) {
            //Explain to the user why we need this permission
            Snackbar snackbar = UtilityMethods.getSnackbar(permissionExplanation, view, this);
            snackbar.setAction("Ok", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestPermission(permission, requestCode);
                }
            });
            snackbar.show();
            return;
        }
        requestPermission(permission, requestCode);
    }

    private void requestPermission(String permission, int requestCode) {
        ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
    }
    public boolean hasPermission(String permission) {
        int permissionCheckState = ContextCompat.checkSelfPermission(this,
                permission);

        if (permissionCheckState == PackageManager.PERMISSION_GRANTED)
            return true;

        return false;
    }


}
