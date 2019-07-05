package com.lx.lib.common.util.permission;

import android.content.pm.PackageManager;
import android.text.TextUtils;

/**
 * Created by glennli on 2019/6/20.<br/>
 */
public final class PermissionGrantResult {
    private String[] permissions;
    private int[] grantResults;

    PermissionGrantResult(String[] permissions, int[] grantResults) {
        this.permissions = permissions;
        this.grantResults = grantResults;
    }

    public boolean isGranted(String permission) {
        if (TextUtils.isEmpty(permission) || permissions == null || permissions.length <= 0
                || grantResults == null || grantResults.length <= 0) {
            return false;
        }
        int index = -1;
        for (int i = 0; i < permissions.length; i++) {
            if (permission.equals(permissions[i])) {
                index = i;
                break;
            }
        }
        if (index == -1 || index >= grantResults.length) {
            return false;
        }
        return grantResults[index] == PackageManager.PERMISSION_GRANTED;
    }
}
