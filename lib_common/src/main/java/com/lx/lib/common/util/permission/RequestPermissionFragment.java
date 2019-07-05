package com.lx.lib.common.util.permission;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.SparseArray;

/**
 * Created by glennli on 2018/12/12.<br/>
 */
public final class RequestPermissionFragment extends Fragment {
    private SparseArray<OnPermissionCallback> mOnPermissionCallbacks = new SparseArray<>(5);

    public RequestPermissionFragment() {
        super();
        setRetainInstance(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (!isFragmentActive()) {
            return;
        }
        OnPermissionCallback callback = mOnPermissionCallbacks.get(requestCode);
        if (callback != null) {
            mOnPermissionCallbacks.remove(requestCode);
            callback.onPermissionCallback(new PermissionGrantResult(permissions, grantResults));
        }
    }

    public void requestPermissions(@NonNull String[] permissions, int requestCode, OnPermissionCallback callback) {
        if (isFragmentActive()) {
            mOnPermissionCallbacks.put(requestCode, callback);
            requestPermissions(permissions, requestCode);
        }
    }

    private boolean isFragmentActive() {
        return getActivity() != null && isAdded();
    }
}
