package com.lx.lib.common.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import java.lang.reflect.Field;

/**
 * Created by glennli on 2018/12/26.<br/>
 */
public final class ToastUtil {
    private static Field sField_TN;
    private static Field sField_TN_Handler;

    static {
        try {
            sField_TN = Toast.class.getDeclaredField("mTN");
            sField_TN.setAccessible(true);
            sField_TN_Handler = sField_TN.getType().getDeclaredField("mHandler");
            sField_TN_Handler.setAccessible(true);
        } catch (Exception e) {}
    }

    /**
     * 避免Toast导致的crash，方案来自QQ空间技术团队
     *
     * @param toast
     */
    private static void hook(Toast toast) {
        try {
            Object tn = sField_TN.get(toast);
            Handler preHandler = (Handler) sField_TN_Handler.get(tn);
            sField_TN_Handler.set(tn, new SafelyHandlerWrapper(preHandler));
        } catch (Exception e) {}
    }

    /**
     * @param context 任意Context的子类都可以
     * @param str
     * @param shortDuration
     */
    public static void show(Context context, final String str, final boolean shortDuration) {
        if (context == null || TextUtils.isEmpty(str)) {
            return;
        }
        final Context appContext = context.getApplicationContext();
        RunUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                showToastInternal(appContext, str, duration);
                Toast.makeText(appContext, str, shortDuration ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
            }
        });
    }

    /*
    // 自定义toast布局
    private static void showToastInternal(Context context, String str, int duration) {
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER, 0, (int) (0.08 * ScreenUtil.getWindowHeight(context)));
        TextView textView = new TextView(context);
        textView.setBackground(context.getResources().getDrawable(R.drawable.shape_toast_bg));
        textView.setMinWidth((int) ScreenUtil.dip2px(context, 100));
        textView.setGravity(Gravity.CENTER);
        textView.setMinHeight((int) ScreenUtil.dip2px(context, 30));
        textView.setTextColor(Color.WHITE);
        textView.setIncludeFontPadding(false);
        int padding = (int) ScreenUtil.dip2px(context, 20);
        textView.setPadding(padding, padding / 2, padding, padding / 2);
        textView.setText(str);
        toast.setView(textView);
        toast.setDuration(duration);
        hook(toast);
        toast.show();
    }
    */

    private static class SafelyHandlerWrapper extends Handler {
        private Handler impl;

        public SafelyHandlerWrapper(Handler impl) {
            this.impl = impl;
        }

        @Override
        public void dispatchMessage(Message msg) {
            try {
                super.dispatchMessage(msg);
            } catch (Exception e) {}
        }

        @Override
        public void handleMessage(Message msg) {
            impl.handleMessage(msg);
        }
    }
}
