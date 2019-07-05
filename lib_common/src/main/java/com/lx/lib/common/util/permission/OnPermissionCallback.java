package com.lx.lib.common.util.permission;

import android.support.annotation.NonNull;

/**
 * Created by glennli on 2019/6/20.<br/>
 */
public interface OnPermissionCallback {
    void onPermissionCallback(@NonNull PermissionGrantResult result);
}
