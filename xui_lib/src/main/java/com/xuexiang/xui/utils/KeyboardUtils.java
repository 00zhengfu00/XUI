/*
 * Copyright (C) 2018 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.xuexiang.xui.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.xuexiang.xui.XUI;

import java.util.HashMap;

/**
 * 软键盘工具
 * @author xuexiang
 * @date 2017/12/8 下午3:35
 */
public class KeyboardUtils implements ViewTreeObserver.OnGlobalLayoutListener {
    private final static int MAGIC_NUMBER = 200;

    private SoftKeyboardToggleListener mCallback;
    private View mRootView;
    private Boolean prevValue = null;
    private float mScreenDensity = 1;
    private static HashMap<SoftKeyboardToggleListener, KeyboardUtils> sListenerMap = new HashMap<>();

    public interface SoftKeyboardToggleListener {
        void onToggleSoftKeyboard(boolean isVisible);
    }

    @Override
    public void onGlobalLayout() {
        Rect r = new Rect();
        mRootView.getWindowVisibleDisplayFrame(r);

        int heightDiff = mRootView.getRootView().getHeight() - (r.bottom - r.top);
        float dp = heightDiff / mScreenDensity;
        boolean isVisible = dp > MAGIC_NUMBER;

        if (mCallback != null && (prevValue == null || isVisible != prevValue)) {
            prevValue = isVisible;
            mCallback.onToggleSoftKeyboard(isVisible);
        }
    }

    /**
     * Add a new keyboard listener
     *
     * @param act      calling activity
     * @param listener callback
     */
    public static void addKeyboardToggleListener(Activity act, SoftKeyboardToggleListener listener) {
        removeKeyboardToggleListener(listener);
        sListenerMap.put(listener, new KeyboardUtils(act, listener));
    }

    /**
     * Add a new keyboard listener
     *
     * @param act      calling activity
     * @param listener callback
     */
    public static void addKeyboardToggleListener(ViewGroup act, SoftKeyboardToggleListener listener) {
        removeKeyboardToggleListener(listener);
        sListenerMap.put(listener, new KeyboardUtils(act, listener));
    }

    /**
     * Remove a registered listener
     *
     * @param listener {@link SoftKeyboardToggleListener}
     */
    public static void removeKeyboardToggleListener(SoftKeyboardToggleListener listener) {
        if (sListenerMap.containsKey(listener)) {
            KeyboardUtils k = sListenerMap.get(listener);
            k.removeListener();

            sListenerMap.remove(listener);
        }
    }

    /**
     * Remove all registered keyboard listeners
     */
    public static void removeAllKeyboardToggleListeners() {
        for (SoftKeyboardToggleListener l : sListenerMap.keySet()) {
            sListenerMap.get(l).removeListener();
        }
        sListenerMap.clear();
    }

    /**
     * Manually toggle soft keyboard visibility
     *
     * @param context calling context
     */
    public static void toggleKeyboardVisibility(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    /**
     * Force closes the soft keyboard
     *
     * @param activeView the view with the keyboard focus
     */
    public static void forceCloseKeyboard(View activeView) {
        InputMethodManager inputMethodManager = (InputMethodManager) activeView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activeView.getWindowToken(), 0);
    }

    private void removeListener() {
        mCallback = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mRootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        } else {
            mRootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
    }

    private KeyboardUtils(Activity act, SoftKeyboardToggleListener listener) {
        mCallback = listener;

        mRootView = ((ViewGroup) act.findViewById(android.R.id.content)).getChildAt(0);
        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(this);

        mScreenDensity = act.getResources().getDisplayMetrics().density;
    }

    private KeyboardUtils(ViewGroup viewGroup, SoftKeyboardToggleListener listener) {
        mCallback = listener;

        mRootView = viewGroup;
        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(this);

        mScreenDensity = viewGroup.getResources().getDisplayMetrics().density;
    }

    /**
     * 点击屏幕空白区域隐藏软键盘
     * <p>根据 EditText 所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘</p>
     * <p>需重写 dispatchTouchEvent</p>
     * @param ev
     * @param activity 窗口
     * @return
     */
    public static void dispatchTouchEvent(MotionEvent ev, Activity activity) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = activity.getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                hideSoftInput(v);
            }
        }
    }

    /**
     * 点击屏幕空白区域隐藏软键盘
     * <p>根据 EditText 所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘</p>
     * <p>需重写 dispatchTouchEvent</p>
     * @param ev
     * @param dialog 窗口
     * @return
     */
    public static void dispatchTouchEvent(MotionEvent ev, Dialog dialog) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = dialog.getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                hideSoftInput(v);
            }
        }
    }

    /**
     * 点击屏幕空白区域隐藏软键盘
     * <p>根据 EditText 所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘</p>
     * <p>需重写 dispatchTouchEvent</p>
     * @param ev
     * @param focusView 聚焦的view
     * @return
     */
    public static void dispatchTouchEvent(MotionEvent ev, View focusView) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (isShouldHideKeyboard(focusView, ev)) {
                hideSoftInput(focusView);
            }
        }
    }

    /**
     * 点击屏幕空白区域隐藏软键盘
     * <p>根据 EditText 所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘</p>
     * <p>需重写 dispatchTouchEvent</p>
     * @param ev
     * @param window 窗口
     * @return
     */
    public static void dispatchTouchEvent(MotionEvent ev, Window window) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = window.getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                hideSoftInput(v);
            }
        }
    }

    /**
     * 根据 EditText 所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘
     *
     * @param v
     * @param event
     * @return
     */
    public static boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            return !(event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom);
        }
        return false;
    }

    /**
     * 动态隐藏软键盘
     *
     * @param view 视图
     */
    public static void hideSoftInput(final View view) {
        InputMethodManager imm =
                (InputMethodManager) XUI.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    /**
     * 切换软键盘显示与否状态
     */
    public static void toggleSoftInput() {
        InputMethodManager imm =
                (InputMethodManager) XUI.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

}
