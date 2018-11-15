package com.xuexiang.xui;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.xuexiang.xui.logs.UILog;
import com.xuexiang.xui.utils.Utils;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;

/**
 * UI全局设置
 *
 * @author xuexiang
 * @since 2018/11/14 上午11:40
 */
public class XUI {

    private static volatile XUI sInstance = null;

    private static Application sContext;

    /**
     * 应用的图标
     */
    private Drawable mAppIcon;

    private XUI() {
        mAppIcon = Utils.getAppIcon(getContext());
    }

    /**
     * 获取单例
     *
     * @return
     */
    public static XUI getInstance() {
        if (sInstance == null) {
            synchronized (XUI.class) {
                if (sInstance == null) {
                    sInstance = new XUI();
                }
            }
        }
        return sInstance;
    }

    //=======================初始化设置===========================//
    /**
     * 初始化
     *
     * @param context
     */
    public static void init(Application context) {
        sContext = context;
    }

    /**
     * 设置默认字体
     */
    public XUI initFontStyle(String defaultFontAssetPath) {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(defaultFontAssetPath)
                .setFontAttrId(R.attr.fontPath)
                .build());
        return this;
    }

    public static Context getContext() {
        testInitialize();
        return sContext;
    }

    private static void testInitialize() {
        if (sContext == null) {
            throw new ExceptionInInitializerError("请先在全局Application中调用 XUI.init() 初始化！");
        }
    }

    //=======================日志调试===========================//
    /**
     * 设置调试模式
     *
     * @param tag
     * @return
     */
    public static void debug(String tag) {
        UILog.debug(tag);
    }

    /**
     * 设置调试模式
     *
     * @param isDebug
     * @return
     */
    public static void debug(boolean isDebug) {
        UILog.debug(isDebug);
    }

    //=======================字体===========================//
    /**
     * @return 获取默认字体
     */
    public static Typeface getDefaultTypeface() {
        return TypefaceUtils.load(getContext().getAssets(), CalligraphyConfig.get().getFontPath());
    }

    /**
     * @param fontPath 字体路径
     * @return 获取默认字体
     */
    public static Typeface getDefaultTypeface(String fontPath) {
        if (TextUtils.isEmpty(fontPath)) {
            fontPath = CalligraphyConfig.get().getFontPath();
        }
        if (!TextUtils.isEmpty(fontPath)) {
            return TypefaceUtils.load(getContext().getAssets(), fontPath);
        }
        return null;
    }

    //=======================================//

    public XUI setAppIcon(Drawable appIcon) {
        mAppIcon = appIcon;
        return this;
    }

    public Drawable getAppIcon() {
        return mAppIcon;
    }

}
