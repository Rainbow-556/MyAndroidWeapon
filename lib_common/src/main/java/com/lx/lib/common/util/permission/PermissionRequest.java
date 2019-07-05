package com.lx.lib.common.util.permission;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.lx.lib.common.util.RunUtil;

import java.util.HashMap;

/**
 * Created by glennli on 2018/12/12.<br/>
 */
public final class PermissionRequest {
    private static final String REQUEST_PERMISSION_FRAGMENT_TAG = RequestPermissionFragment.class.getName();
    private static final HashMap<FragmentManager, RequestPermissionFragment> sPendingRequestPermissionFragments = new HashMap<>(5);
    private static int sRandomRequestCode = 8000;
    private FragmentActivity activity;
    private int requestCode = -1;
    private String[] permissions;
    private OnPermissionCallback callback;

    public static PermissionRequest obtain(FragmentActivity activity) {
        return new PermissionRequest(activity);
    }

    private PermissionRequest(FragmentActivity activity) {
        this.activity = activity;
    }

    /**
     * 如果不设置，默认有个静态int变量递增
     *
     * @param requestCode
     * @return
     */
    public PermissionRequest requestCode(int requestCode) {
        this.requestCode = requestCode;
        return this;
    }

    public PermissionRequest permissions(String... permissions) {
        this.permissions = permissions;
        return this;
    }

    public PermissionRequest callback(OnPermissionCallback callback) {
        this.callback = callback;
        return this;
    }

    private void startInternal() {
        if (permissions == null || permissions.length <= 0) {
            return;
        }
        final FragmentManager fragmentManager = activity.getSupportFragmentManager();
        RequestPermissionFragment current = (RequestPermissionFragment) fragmentManager
                .findFragmentByTag(REQUEST_PERMISSION_FRAGMENT_TAG);
        if (current == null) {
            current = sPendingRequestPermissionFragments.get(fragmentManager);
            if (current == null) {
                current = new RequestPermissionFragment();
                sPendingRequestPermissionFragments.put(fragmentManager, current);
                // add是个异步操作
                fragmentManager.beginTransaction()
                        .add(current, REQUEST_PERMISSION_FRAGMENT_TAG)
                        .commitAllowingStateLoss();
            }
        }
        final int realRequestCode;
        if (requestCode == -1) {
            realRequestCode = sRandomRequestCode;
            sRandomRequestCode++;
        } else {
            realRequestCode = requestCode;
        }
        if (current.isAdded()) {
            current.requestPermissions(permissions, realRequestCode, callback);
        } else {
            // post一个Runnable来执行权限请求，确保RequestPermissionFragment已经被add到Activity
            RunUtil.getUiHandler().post(new Runnable() {
                @Override
                public void run() {
                    sPendingRequestPermissionFragments.remove(fragmentManager);
                    RequestPermissionFragment fragment = (RequestPermissionFragment) fragmentManager
                            .findFragmentByTag(REQUEST_PERMISSION_FRAGMENT_TAG);
                    if (fragment != null) {
                        fragment.requestPermissions(permissions, realRequestCode, callback);
                    }
                }
            });
        }
    }

    public void start() {
        RunUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startInternal();
            }
        });
    }
}
