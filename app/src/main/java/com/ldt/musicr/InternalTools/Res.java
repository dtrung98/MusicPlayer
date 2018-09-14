package com.ldt.musicr.InternalTools;

import android.content.res.Resources;
import android.os.Build;

public class Res extends Resources {

    public Res(Resources original) {
        super(original.getAssets(), original.getDisplayMetrics(), original.getConfiguration());
    }

    @Override public int getColor(int id) throws NotFoundException {
        return getColor(id, null);
    }

    @Override public int getColor(int id, Theme theme) throws NotFoundException {
        switch (getResourceEntryName(id)) {
            case "SurfaceColor":
                // You can change the return value to an instance field that loads from SharedPreferences.
                return Tool.getSurfaceColor(); // used as an example. Change as needed.
            default:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    return super.getColor(id, theme);
                }
                return super.getColor(id);
        }
    }
}