package com.tencent.liteav.demo.lvb.liveplayer;

import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import com.tencent.liteav.demo.lvb.R;

import com.tencent.liteav.demo.lvb.floatwindow.BaseFloatWindowActivity;
import com.tencent.liteav.demo.lvb.floatwindow.event.MessageEvent;
import com.tencent.liteav.demo.lvb.floatwindow.float_view.FloatWindowManager;
import com.tencent.liteav.demo.lvb.floatwindow.uitls.Contents;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class LivePlayerActivity extends BaseFloatWindowActivity {
    private static final String TAG = LivePlayerActivity.class.getSimpleName();
    private TXLivePlayer mLivePlayer = null;
    private TXCloudVideoView mPlayerView;
    private FrameLayout videoContainer;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_play;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        setContentView();
    }


    private void setContentView() {
        Button btn_floatWindow = (Button) findViewById(R.id.btn_floatWindow);
        btn_floatWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatWindow();
            }
        });

        getWindow().addFlags(WindowManager.LayoutParams.
                FLAG_KEEP_SCREEN_ON);
        if (mLivePlayer == null) {
            mLivePlayer = new TXLivePlayer(this);
        }
        mPlayerView = new TXCloudVideoView(this);
        videoContainer = (FrameLayout) findViewById(R.id.videoContainer);
        videoContainer.addView(mPlayerView);

//        mPlayerView = (TXCloudVideoView) findViewById(R.id.video_view);
//        mPlayerView.setLogMargin(12, 12, 110, 60);
//        mPlayerView.showLog(false);
        Contents.mPlayerView = mPlayerView;
        String playUrl = "http://5815.liveplay.myqcloud.com/live/5815_89aad37e06ff11e892905cb9018cf0d4_900.flv";
        int mPlayType = TXLivePlayer.PLAY_TYPE_LIVE_FLV;
        mLivePlayer.setPlayerView(mPlayerView);
        mLivePlayer.startPlay(playUrl, mPlayType);
        // 设置填充模式:将图像等比例铺满整个屏幕，多余部分裁剪掉，此模式下画面不会留黑边，但可能因为部分区域被裁剪而显示不全。
        mLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
        //将图像等比例缩放，适配最长边，缩放后的宽和高都不会超过显示区域，居中显示，画面可能会留有黑边。
//        mLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION);
    }


    private void stopPlay() {
        if (mLivePlayer != null) {
            mLivePlayer.stopRecord();
            mLivePlayer.setPlayListener(null);
            mLivePlayer.stopPlay(true);
        }

    }

    @Override
    public void onBackPressed() {
        stopPlay();
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLivePlayer != null) {
            mLivePlayer.stopPlay(true);
            mLivePlayer = null;
        }
        if (mPlayerView != null) {
            mPlayerView.onDestroy();
            mPlayerView = null;
        }

        Log.d(TAG, "vrender onDestroy");
    }


    ///////////////////////////////////////////////悬浮框

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "onRestart============================================================");
        TXCloudVideoView cloudVideoView = Contents.mPlayerView;

        TXCloudVideoView txCloudVideoView = new TXCloudVideoView(this);
        TextureView mTextureView = cloudVideoView.getVideoView();
        if (mTextureView != null && mTextureView.getParent() != null) {
            ((ViewGroup) mTextureView.getParent()).removeView(mTextureView);
            txCloudVideoView.addVideoView(mTextureView);
        }

        videoContainer.addView(txCloudVideoView);
        mPlayerView = txCloudVideoView;
    }

    private void floatWindow() {
        //最小化Activity
//        moveTaskToBack(true);
        floatWindowType = FloatWindowManager.FW_TYPE_ALERT_WINDOW;
        checkPermissionAndShow();
    }


}