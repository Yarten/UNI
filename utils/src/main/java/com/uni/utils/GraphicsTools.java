package com.uni.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by tan on 2017/12/8.
 *
 * 用于
 * <ul>
 *     <li> 基本的图像单位转换 </li>
 *     <li> 文件中获取bitmap </li>
 * </ul>
 *
 */

public class GraphicsTools {

    /**
     * 根据给定的px值获取当前设备上对应的dip值
     * @param context 当前上下文
     * @param pxValue 需要进行转换的px值
     * @return 转换后的dip值
     */
    public static float px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 根据给定的dip值获取当前设备上对应的pc值
     * @param context 当前上下文
     * @param dpValue 需要进行转换的dip值
     * @return 转换后的px值
     */
    public static float dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    /**
     * 用于处理沉浸式状态栏的一个类
     */
    public static class statusBar{

        /**
         * 请求启用沉浸式状态栏
         * @param activity 需要启用沉浸式状态栏的activity
         */
        public static void immerseStatusBar(Activity activity)
        {
            // 1. 沉浸式状态栏 + dark模式
            boolean immerse = immerseStatusBar_(activity);
            boolean darkMode = setDarkMode(activity);
            if (immerse) {
                View positionView = new View(activity.getBaseContext());
                ViewGroup.LayoutParams l = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        getStatusBarHeight(activity.getBaseContext()));
                positionView.setX(0);
                positionView.setY(0);
                positionView.setLayoutParams(l);

                if (!darkMode) {
                    positionView.setBackgroundColor(Color.BLACK);
                }
            }
        }

        /**
         * 获取状态栏的高度
         * @param context 需要获取状态栏高度的activity
         * @return  状态栏的高度
         */
        public static int getStatusBarHeight(Context context) {
            Class<?> c = null;
            Object obj = null;
            Field field = null;
            int x = 0, statusBarHeight = 0;
            try {
                c = Class.forName("com.android.internal.R$dimen");
                obj = c.newInstance();
                field = c.getField("status_bar_height");
                x = Integer.parseInt(field.get(obj).toString());
                statusBarHeight = context.getResources().getDimensionPixelSize(x);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return statusBarHeight;
        }

        private static boolean immerseStatusBar_(Activity activity) {
            boolean success = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                success = true;
                activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
                activity.getWindow()
                        .getDecorView()
                        .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                success = true;
                activity.getWindow()
                        .setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
            return success;
        }

        private static boolean setDarkMode(Activity activity) {
            return setDarkMode(activity, true);
        }

        /**
         * darkMode设置
         *
         * @return 是否成功
         */
        private static boolean setDarkMode(Activity activity, boolean darkMode) {
            String brand = Build.BRAND;
            boolean success = false;
            if (brand.contains("Xiaomi")) {
                success = setXiaomiDarkMode(activity, darkMode);
            } else if (brand.contains("Meizu")) {
                success = setMeizuDarkMode(activity, darkMode);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                final int lFlags = activity.getWindow().getDecorView().getSystemUiVisibility();
                activity.getWindow().getDecorView().setSystemUiVisibility(darkMode ? (lFlags | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) : (lFlags & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR));
                success = true;
            }

            return success;
        }

        private static boolean setXiaomiDarkMode(Activity activity, boolean darkmode) {
            Class<? extends Window> clazz = activity.getWindow().getClass();
            try {
                int darkModeFlag = 0;
                Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                extraFlagField.invoke(activity.getWindow(), darkmode ? darkModeFlag : 0, darkModeFlag);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        private static boolean setMeizuDarkMode(Activity activity, boolean dark) {
            boolean result = false;
            if (activity != null) {
                try {
                    WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                    Field darkFlag = WindowManager.LayoutParams.class
                            .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                    Field meizuFlags = WindowManager.LayoutParams.class
                            .getDeclaredField("meizuFlags");
                    darkFlag.setAccessible(true);
                    meizuFlags.setAccessible(true);
                    int bit = darkFlag.getInt(null);
                    int value = meizuFlags.getInt(lp);
                    if (dark) {
                        value |= bit;
                    } else {
                        value &= ~bit;
                    }
                    meizuFlags.setInt(lp, value);
                    activity.getWindow().setAttributes(lp);
                    result = true;
                } catch (Exception e) {
                }
            }
            return result;
        }
    }


    /**
     * 用于处理图片转换等
     */
    public static class imgtool{
        /**
         * 将系统里面等 resource资源转换为bitmap
         * @param context resource 资源所在等上下文
         * @param resid   需要抓换等resource资源
         * @return <tt>Bitmap</tt> 返回转换后的 bitmap
         */
        public static Bitmap res2bitmap(Context context,Integer resid) {
            try {
                Bitmap bmp= BitmapFactory.decodeResource(context.getResources(),resid);
                return  bmp;
            }
            catch (Exception e) {
                return null;
            }
        }
    }


}
