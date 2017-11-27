package com.yueding.puzzle.Utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by yueding on 2017/11/24.
 */

public class ScreenUtil {

    /**
     * 获取屏幕相关参数
     * @param context 上下文环境
     * @return metrics 参数
     */
    public static DisplayMetrics getScreenSize(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        display.getMetrics(metrics);
        return metrics;
    }

    /**
     * 获取像素密度
     * @param context
     * @return density
     */
    public static float getDeviceDensity(Context context) {
/*
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        return metrics.density;
*/
        return getScreenSize(context).density;
    }
}
