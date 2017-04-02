package com.example.administrator.smartruler.navigationItems;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.example.administrator.smartruler.R;

/**
 * Created by popmusic on 17-3-9.
 */

public class SettingDialog extends Dialog {
    private OnDialogListener mListener;
    private Button btn_ok, btn_cancel;
    private EditText edit_h, edit_H;

    public interface OnDialogListener{
        void onInput(String inputStr_h, String inputStr_H);
    }

    public SettingDialog(Context context){
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.setting_dialog);
        btn_ok = (Button)findViewById(R.id.btn_ok);
        btn_cancel = (Button)findViewById(R.id.btn_cancel);
        edit_h = (EditText)findViewById(R.id.edit_h);
        edit_H = (EditText)findViewById(R.id.edit_H);

        btn_ok.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                dismiss();
                String inputStr_h = edit_h.getText().toString();
                String inputStr_H = edit_H.getText().toString();
                if(mListener != null){
                    if(!TextUtils.isEmpty(inputStr_h) && !TextUtils.isEmpty(inputStr_H)){
                        mListener.onInput(inputStr_h, inputStr_H);
                    }
                    if(TextUtils.isEmpty(inputStr_H) && !TextUtils.isEmpty(inputStr_h)){
                        mListener.onInput(inputStr_h, "0");
                    }
                    if(TextUtils.isEmpty(inputStr_h) && !TextUtils.isEmpty(inputStr_H)){
                        mListener.onInput("150", inputStr_H);
                    }

                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                dismiss();
            }
        });
    }

    public void setOnDialogListener(OnDialogListener listener){
        mListener = listener;
    }
}
