package com.example.administrator.smartruler.navigationItems;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.administrator.smartruler.R;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.util.ArrayList;

public class ShareActivity extends Activity {

    private final String APP_ID = "wxda5cdc6f805d5225";
    private IWXAPI wxApi;

    private Tencent mTencent;
    private String QQ_ID = "1106099966";
    Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        wxApi = WXAPIFactory.createWXAPI(getApplicationContext(), "wxda5cdc6f805d5225");
        wxApi.registerApp("wxda5cdc6f805d5225");

        ImageButton friend_btn = (ImageButton)findViewById(R.id.weixin);
        friend_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                wechatShare(0);// 分享到微信好友
            }
        });
        ImageButton quan_btn = (ImageButton)findViewById(R.id.pyq);
        quan_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                wechatShare(1);//分享到微信朋友圈
            }
        });


        mTencent = Tencent.createInstance(QQ_ID, getApplicationContext());

        ImageButton qq_btn = (ImageButton)findViewById(R.id.qqq);
        qq_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onClickShare();
            }
        });
        ImageButton qzone_btn = (ImageButton)findViewById(R.id.qz);
        qzone_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                shareToQzone();
            }
        });

        back = (Button)findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });
    }

    private void wechatShare(int flag) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = "https://smartruler.github.io/SmartRuler_ShowPage/";
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = "Smart Ruler";
        msg.description = "Based on Android";
        BitmapDrawable bmpDraw = (BitmapDrawable) getResources().getDrawable(
                R.drawable.logom);

        Bitmap thumb = bmpDraw.getBitmap();
        msg.setThumbImage(thumb);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession
                : SendMessageToWX.Req.WXSceneTimeline;
        wxApi.sendReq(req);
    }

    private void onClickShare() {
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, "SmartRuler");
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY,"Based on Android");
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,"https://smartruler.github.io/SmartRuler_ShowPage/");
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,"https://avatars3.githubusercontent.com/u/26999387?v=3&s=200");
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "SmartRuler");
        mTencent.shareToQQ(ShareActivity.this, params, new BaseUiListener( ));
    }

    private void shareToQzone () {
        //分享类型
        final Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE,QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, "SmartRuler");//必填
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, "Based on Android");//选填
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, "https://smartruler.github.io/SmartRuler_ShowPage/");//必填
        ArrayList<String> imgUrlList = new ArrayList<>();
        imgUrlList.add("https://avatars3.githubusercontent.com/u/26999387?v=3&s=200");
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL,imgUrlList);// 图片地址
        mTencent.shareToQzone(ShareActivity.this, params, new BaseUiListener( ));
    }

    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object o) {
            Toast.makeText(ShareActivity.this, o.toString(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(UiError uiError) {
            Toast.makeText(ShareActivity.this, uiError.errorMessage + "--" + uiError.errorCode + "---" + uiError.errorDetail, Toast.LENGTH_LONG).show();

        }

        @Override
        public void onCancel() {
            Toast.makeText(ShareActivity.this, "取消", Toast.LENGTH_SHORT).show();
        }
    }
}
