package com.round1.android.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

public class UIUtils {

    public static void showProgressDialog(ProgressDialog progress, boolean cancelable) {
        if (progress == null || progress.isShowing()) return;

        progress.setCancelable(cancelable);
        progress.show();
    }

    public static void hideProgressDialog(ProgressDialog progress) {
        if (progress != null && progress.isShowing()) {
            progress.cancel();
        }
    }

    public static void showToast(Context context, int resId) {
        if (context == null) return;
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }
}
