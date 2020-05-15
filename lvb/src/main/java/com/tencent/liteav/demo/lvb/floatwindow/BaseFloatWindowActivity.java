package com.tencent.liteav.demo.lvb.floatwindow;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.tencent.liteav.demo.lvb.DetailActivity;
import com.tencent.liteav.demo.lvb.R;
import com.tencent.liteav.demo.lvb.floatwindow.event.MessageEvent;
import com.tencent.liteav.demo.lvb.floatwindow.float_view.FloatViewListener;
import com.tencent.liteav.demo.lvb.floatwindow.float_view.FloatWindowManager;
import com.tencent.liteav.demo.lvb.floatwindow.float_view.IFloatView;
import com.tencent.liteav.demo.lvb.floatwindow.fw_permission.FloatWinPermissionCompat;
import com.tencent.liteav.demo.lvb.liveplayer.LivePlayerActivity;

import org.greenrobot.eventbus.EventBus;


/**
 * Description:Activity基类
 *
 * @author 杜乾-Dusan,Created on 2018/2/9 - 15:52.
 * E-mail:duqian2010@gmail.com
 */
public abstract class BaseFloatWindowActivity extends AppCompatActivity {
    protected Context mContext;
    protected View rootView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        if (!isShowTitle()) {
            //隐藏标题栏
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        int layoutId = getLayoutResId();
        if (layoutId > 0) {
            rootView = LayoutInflater.from(mContext).inflate(layoutId, null);
            setContentView(rootView);
        }
        if (floatWindowManager == null) {
            floatWindowManager = new FloatWindowManager();
        }
        initData();
        initView();
    }

    protected abstract int getLayoutResId();

    protected abstract void initData();

    protected abstract void initView();

    @Override
    protected void onResume() {
        super.onResume();
        closeFloatWindow();
//        showFloatWindowDelay();
    }

    /**
     * 必须等activity创建后，view展示了再addView，否则可能崩溃
     * BadTokenException: Unable to add window --token null is not valid; is your activity running?
     */
    protected void showFloatWindowDelay() {
        if (rootView != null && isShowFloatWindow()) {
            rootView.removeCallbacks(floatWindowRunnable);
            rootView.post(floatWindowRunnable);
        }
    }

    protected boolean isShowFloatWindow() {
        return true;
    }

    protected boolean isShowTitle() {
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (floatWindowType != FloatWindowManager.FW_TYPE_ALERT_WINDOW) {
            if (isShowFloatWindow()) {
                //不要放在closeFloatWindow()中，可能会导致其他界面熄屏
                clearScreenOn();
                closeFloatWindow();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyWindow();
    }

    /*---------------------------float window start---------------------------*/

    protected int floatWindowType = 0;

    private FloatWindowManager floatWindowManager;
    private final Runnable floatWindowRunnable = new Runnable() {
        @Override
        public void run() {
            showFloatWindow();
        }
    };

    /**
     * 显示悬浮窗
     */
    protected void showFloatWindow() {
        //最小化Activity
//        moveTaskToBack(true);
        closeFloatWindow();//如果要显示多个悬浮窗，可以不关闭，这里只显示一个
        floatWindowManager.showFloatWindow(this, floatWindowType);
        addFloatWindowClickListener();

    }

    /**
     * 关闭悬浮窗
     */
    protected void closeFloatWindow() {
        if (rootView != null) {
            rootView.removeCallbacks(floatWindowRunnable);
        }
        if (floatWindowManager != null) {
            floatWindowManager.dismissFloatWindow();
        }
    }

    /**
     * 监听悬浮窗关闭和点击事件
     */
    private void addFloatWindowClickListener() {
        IFloatView floatView = floatWindowManager.getFloatView();
        if (floatView == null) {
            return;
        }
        //说明悬浮窗view创建了，增加屏幕常亮
        keepScreenOn();
        floatView.setFloatViewListener(new FloatViewListener() {
            @Override
            public void onClose() {
                Log.d("BaseFloatWindowActivity", "onClose");
//                Toast.makeText(mContext, "onClose", Toast.LENGTH_LONG).show();
                clearScreenOn();
                closeFloatWindow();
//                EventBus.getDefault().post(new MessageEvent());
                finish();
            }

            @Override
            public void onClick() {
//                Toast.makeText(mContext, "onClick", Toast.LENGTH_LONG).show();
//                onFloatWindowClick();
                mContext.startActivity(new Intent(mContext, LivePlayerActivity.class));
            }

            @Override
            public void onDoubleClick() {
//                Toast.makeText(mContext, "onDoubleClick", Toast.LENGTH_LONG).show();
            }
        });

        startActivity(new Intent(this, DetailActivity.class));
    }

    /**
     * 开启屏幕常量
     */
    private void keepScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * 清除常量模式
     */
    private void clearScreenOn() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * 悬浮窗点击事件，子类按需重写
     */
    protected void onFloatWindowClick() {
        Toast.makeText(mContext, "FloatWindow clicked", Toast.LENGTH_LONG).show();
    }

    protected void checkPermissionAndShow() {
        // 检查是否已经授权
        if (FloatWinPermissionCompat.getInstance().check(mContext)) {
            showFloatWindowDelay();
        } else {
            // 授权提示
            new AlertDialog.Builder(mContext).setTitle("悬浮窗权限未开启")
                    .setMessage("你的手机没有授权" + mContext.getString(R.string.app_name) + "获得悬浮窗权限，视频悬浮窗功能将无法正常使用")
                    .setPositiveButton("开启", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 显示授权界面
                            try {
                                FloatWinPermissionCompat.getInstance().apply(mContext);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .setNegativeButton("取消", null).show();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        destroyWindow();
    }

    private void destroyWindow() {
        if (floatWindowType != FloatWindowManager.FW_TYPE_ALERT_WINDOW) {
            closeFloatWindow();
        }
    }

    private final int mRequestCode = 1024;
    private RequestPermissionCallBack mRequestPermissionCallBack;


    /**
     * 权限请求结果回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasAllGranted = true;
        StringBuilder permissionName = new StringBuilder();
        for (String s : permissions) {
            permissionName = permissionName.append(s + "\r\n");
        }
        switch (requestCode) {
            case mRequestCode: {
                for (int i = 0; i < grantResults.length; ++i) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        hasAllGranted = false;
                        //在用户已经拒绝授权的情况下，如果shouldShowRequestPermissionRationale返回false则
                        // 可以推断出用户选择了“不在提示”选项，在这种情况下需要引导用户至设置页手动授权
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                            new AlertDialog.Builder(BaseFloatWindowActivity.this).setTitle("申请权限")
                                    //设置对话框标题
                                    .setMessage("获取相关权限失败:" + permissionName + "将导致部分功能无法正常使用，需要到设置页面手动授权")
                                    //设置显示的内容
                                    .setPositiveButton("去授权", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                                            intent.setData(uri);
                                            startActivity(intent);
                                            dialog.dismiss();
                                        }
                                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    mRequestPermissionCallBack.denied();
                                }
                            }).show();//在按键响应事件中显示此对话框
                        } else {
                            //用户拒绝权限请求，但未选中“不再提示”选项
                            mRequestPermissionCallBack.denied();
                        }
                        break;
                    }
                }
                if (hasAllGranted) {
                    mRequestPermissionCallBack.granted();
                }
            }
            break;
            default:
                break;
        }
    }

    /**
     * 发起权限请求
     */
    public void requestPermissions(final Context context, final String[] permissions,
                                   RequestPermissionCallBack callback) {
        this.mRequestPermissionCallBack = callback;
        StringBuilder permissionNames = new StringBuilder();
        for (String s : permissions) {
            permissionNames = permissionNames.append(s + "\r\n");
        }
        //如果所有权限都已授权，则直接返回授权成功,只要有一项未授权，则发起权限请求
        boolean isAllGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
                isAllGranted = false;
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission)) {
                    new AlertDialog.Builder(BaseFloatWindowActivity.this).setTitle("PermissionTest")
                            .setMessage(
                                    "您好，需要如下权限：" + permissionNames + " 请允许，否则将影响xit功能的正常使用。")//设置显示的内容
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                                    ActivityCompat.requestPermissions(((Activity) context), permissions, mRequestCode);
                                }
                            }).show();
                } else {
                    ActivityCompat.requestPermissions(((Activity) context), permissions, mRequestCode);
                }
                break;
            }
        }
        if (isAllGranted) {
            mRequestPermissionCallBack.granted();
        }
    }

    /**
     * 权限请求结果回调接口
     */
    public interface RequestPermissionCallBack {
        /**
         * 同意授权
         */
        void granted();

        /**
         * 取消授权
         */
        void denied();
    }

}
