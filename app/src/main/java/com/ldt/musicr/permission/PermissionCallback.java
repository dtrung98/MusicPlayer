package com.ldt.musicr.permission;

/**
 * Created by trung on 0005 05 Feb 2018.
 */

public interface PermissionCallback {
    void permissionGranted();

    void permissionRefused();
}
