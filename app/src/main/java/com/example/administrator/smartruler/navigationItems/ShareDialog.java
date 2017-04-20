package com.example.administrator.smartruler.navigationItems;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

import com.example.administrator.smartruler.R;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * Created by popmusic on 17-3-20.
 */

public class ShareDialog extends Dialog implements IWXAPIEventHandler {
    private final String APP_ID = "wxda5cdc6f805d5225";

    private IWXAPI wxApi;
    private Context context;

    public ShareDialog(Context context) {
        super(context);
        this.context =context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.share_dialog);

        wxApi = WXAPIFactory.createWXAPI(context.getApplicationContext(), "wxda5cdc6f805d5225");
        wxApi.registerApp("wxda5cdc6f805d5225");

        ImageButton friend_btn = (ImageButton)findViewById(R.id.sharefriend);
        friend_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                wechatShare(0);// 分享到微信好友
                dismiss();
            }
        });
        ImageButton quan_btn = (ImageButton)findViewById(R.id.sharefriendquan);
        quan_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                wechatShare(1);//分享到微信朋友圈
                dismiss();
            }
        });

    }
    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {

        String result = "";

        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = "errcode_success";
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "errcode_cancel";
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = "errcode_deny";
                break;
            default:
                result = "errcode_unknown";
                break;
        }

    }

    private void wechatShare(int flag) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = "https://smartruler.github.io/SmartRuler_ShowPage/";
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = "Smart Ruler";
        msg.description = "Based on Android";
        BitmapDrawable bmpDraw = (BitmapDrawable) context.getResources().getDrawable(
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
}
