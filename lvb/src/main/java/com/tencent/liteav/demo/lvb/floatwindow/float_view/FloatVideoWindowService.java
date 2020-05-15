package com.tencent.liteav.demo.lvb.floatwindow.float_view;//package com.tencent.liteav.demo.lvb.float_window.float_view;
//
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Binder;
//import android.os.Build;
//import android.os.IBinder;
//import android.support.annotation.Nullable;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.TextureView;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.WindowManager;
//
//import com.tencent.liteav.demo.lvb.R;
//import com.tencent.liteav.renderer.TXCGLSurfaceView;
//import com.tencent.rtmp.ui.TXCloudVideoView;
//
///**
// * 视频悬浮窗服务
// */
//public class FloatVideoWindowService extends Service {
//    private WindowManager mWindowManager;
//    private WindowManager.LayoutParams wmParams;
//    private LayoutInflater inflater;
//    private String currentBigUserId;
//    //浮动布局view
//    private View mFloatingLayout;
//    //容器父布局
//    private TXCloudVideoView mTXCloudVideoView;
//
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        initWindow();//设置悬浮窗基本参数（位置、宽高等）
//
//    }
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        currentBigUserId = intent.getStringExtra("userId");
//        initFloating();//悬浮框点击事件的处理
//        return new MyBinder();
//    }
//
//    public class MyBinder extends Binder {
//        public FloatVideoWindowService getService() {
//            return FloatVideoWindowService.this;
//        }
//    }
//
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        return super.onStartCommand(intent, flags, startId);
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (mFloatingLayout != null) {
//            // 移除悬浮窗口
//            mWindowManager.removeView(mFloatingLayout);
//            mFloatingLayout = null;
//            Constents.isShowFloatWindow = false;
//        }
//    }
//
//    /**
//     * 设置悬浮框基本参数（位置、宽高等）
//     */
//    private void initWindow() {
//        mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
//        //设置好悬浮窗的参数
//        wmParams = getParams();
//        // 悬浮窗默认显示以左上角为起始坐标
//        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
//        //悬浮窗的开始位置，因为设置的是从左上角开始，所以屏幕左上角是x=0;y=0
//        wmParams.x = 70;
//        wmParams.y = 210;
//        //得到容器，通过这个inflater来获得悬浮窗控件
//        inflater = LayoutInflater.from(getApplicationContext());
//        // 获取浮动窗口视图所在布局
//        mFloatingLayout = inflater.inflate(R.layout.alert_float_video_layout, null);
//        // 添加悬浮窗的视图
//        mWindowManager.addView(mFloatingLayout, wmParams);
//    }
//
//
//    private WindowManager.LayoutParams getParams() {
//        wmParams = new WindowManager.LayoutParams();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
//        } else {
//            wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
//        }
//        //设置可以显示在状态栏上
//        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
//                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR |
//                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
//
//        //设置悬浮窗口长宽数据
//        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
//        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        return wmParams;
//    }
//
//    private void initFloating() {
//        mTXCloudVideoView = mFloatingLayout.findViewById(R.id.float_videoview);
//        TRTCVideoViewLayout mTRTCVideoViewLayout = Constents.mVideoViewLayout;
//        TXCloudVideoView mLocalVideoView = mTRTCVideoViewLayout.getCloudVideoViewByUseId(currentBigUserId);
//        if (mLocalVideoView == null) {
//            mLocalVideoView = mTRTCVideoViewLayout.getCloudVideoViewByIndex(0);
//        }
//        if (ConstData.userid.equals(currentBigUserId)) {
//            TXCGLSurfaceView mTXCGLSurfaceView = mLocalVideoView.getGLSurfaceView();
//            if (mTXCGLSurfaceView != null && mTXCGLSurfaceView.getParent() != null) {
//                ((ViewGroup) mTXCGLSurfaceView.getParent()).removeView(mTXCGLSurfaceView);
//                mTXCloudVideoView.addVideoView(mTXCGLSurfaceView);
//            }
//        } else {
//            TextureView mTextureView = mLocalVideoView.getVideoView();
//            if (mTextureView != null && mTextureView.getParent() != null) {
//                ((ViewGroup) mTextureView.getParent()).removeView(mTextureView);
//                mTXCloudVideoView.addVideoView(mTextureView);
//            }
//        }
//        Constents.isShowFloatWindow = true;
//        //悬浮框触摸事件，设置悬浮框可拖动
//        mTXCloudVideoView.setOnTouchListener(new FloatingListener());
//        //悬浮框点击事件
//        mTXCloudVideoView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //在这里实现点击重新回到Activity
//                Intent intent = new Intent(FloatVideoWindowService.this, TRTCVideoCallActivity.class);
//                startActivity(intent);
//            }
//        });
//
//    }
//
//    //开始触控的坐标，移动时的坐标（相对于屏幕左上角的坐标）
//    private int mTouchStartX, mTouchStartY, mTouchCurrentX, mTouchCurrentY;
//    //开始时的坐标和结束时的坐标（相对于自身控件的坐标）
//    private int mStartX, mStartY, mStopX, mStopY;
//    //判断悬浮窗口是否移动，这里做个标记，防止移动后松手触发了点击事件
//    private boolean isMove;
//
//    private class FloatingListener implements View.OnTouchListener {
//
//        @Override
//        public boolean onTouch(View v, MotionEvent event) {
//            int action = event.getAction();
//            switch (action) {
//                case MotionEvent.ACTION_DOWN:
//                    isMove = false;
//                    mTouchStartX = (int) event.getRawX();
//                    mTouchStartY = (int) event.getRawY();
//                    mStartX = (int) event.getX();
//                    mStartY = (int) event.getY();
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    mTouchCurrentX = (int) event.getRawX();
//                    mTouchCurrentY = (int) event.getRawY();
//                    wmParams.x += mTouchCurrentX - mTouchStartX;
//                    wmParams.y += mTouchCurrentY - mTouchStartY;
//                    mWindowManager.updateViewLayout(mFloatingLayout, wmParams);
//
//                    mTouchStartX = mTouchCurrentX;
//                    mTouchStartY = mTouchCurrentY;
//                    break;
//                case MotionEvent.ACTION_UP:
//                    mStopX = (int) event.getX();
//                    mStopY = (int) event.getY();
//                    if (Math.abs(mStartX - mStopX) >= 1 || Math.abs(mStartY - mStopY) >= 1) {
//                        isMove = true;
//                    }
//                    break;
//                default:
//                    break;
//            }
//            //如果是移动事件不触发OnClick事件，防止移动的时候一放手形成点击事件
//            return isMove;
//        }
//    }
//
//}
