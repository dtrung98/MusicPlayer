package com.ldt.musicr.permission;

/**
 * Created by trung on 0005 05 Feb 2018.
 */

public interface PermissionListener {
    /**
 * Gets called each time we run Nammu.permissionCompare() and some Permission is revoke/granted to us
 *
 * @param permissionChanged
 */
void permissionsChanged(String permissionChanged);

    /**
     * Gets called each time we run Nammu.permissionCompare() and some Permission is granted
     *
     * @param permissionGranted
     */
    void permissionsGranted(String permissionGranted);

    /**
     * Gets called each time we run Nammu.permissionCompare() and some Permission is removed
     *
     * @param permissionRemoved
     */
    void permissionsRemoved(String permissionRemoved);
}
