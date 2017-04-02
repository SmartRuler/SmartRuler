package com.example.administrator.smartruler.navigationItems;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import com.example.administrator.smartruler.R;

/**
 * Created by popmusic on 17-3-20.
 */

public class ShareDialog extends Dialog {
    public ShareDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.share_dialog);
    }
}
