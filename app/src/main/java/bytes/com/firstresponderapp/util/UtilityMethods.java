package bytes.com.firstresponderapp.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import java.io.File;

import bytes.com.firstresponderapp.R;

public class UtilityMethods {
    public static Uri outputFileUri;

    public static void createImage(Context context) {
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "MyDir" + File.separator);
        root.mkdirs();

        final String fname = System.currentTimeMillis() + ".jpg";
        final File sdImageMainDirectory = new File(root, fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);
        sdImageMainDirectory.deleteOnExit();
    }

    public static Snackbar showSnackbar(final String snackbarMessage, View contentView, Context context) {
        Snackbar snackbar = Snackbar.make(contentView, snackbarMessage, Snackbar.LENGTH_SHORT);
        styleSnackbar(snackbar, context);
        hideKeypad(context);
        snackbar.show();
        return snackbar;
    }

    private static void styleSnackbar(Snackbar snackbar, Context context) {
        ViewGroup group = (ViewGroup) snackbar.getView();
        group.setBackgroundColor(ActivityCompat.getColor(context, R.color.colorPrimary));
        snackbar.setActionTextColor(Color.parseColor("#E6E6E6"));
        TextView actionTextView = (TextView) group.findViewById(R.id.snackbar_action);
        actionTextView.setTextSize(18);
        actionTextView.setTypeface(null, Typeface.BOLD);
        TextView tv = (TextView) group.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
    }

    private static void hideKeypad(Context context) {
        View view = ((Activity) context).getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static Snackbar getSnackbar(final String snackbarMessage, View contentView, Context context) {
        Snackbar snackbar = Snackbar.make(contentView, snackbarMessage, Snackbar.LENGTH_SHORT);
        styleSnackbar(snackbar, context);
        hideKeypad(context);
        return snackbar;
    }
}
