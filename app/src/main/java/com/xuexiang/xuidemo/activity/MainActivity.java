package com.xuexiang.xuidemo.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;

import com.xuexiang.xpage.base.XPageActivity;
import com.xuexiang.xui.XUI;
import com.xuexiang.xuidemo.fragment.ComponentsFragment;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * 项目壳工程
 *
 * @author xuexiang
 * @since 2018/11/13 下午5:20
 */
public class MainActivity extends XPageActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        //注入字体
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        XUI.initTheme(this);

        openPage(ComponentsFragment.class);
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
//            onUserInteraction();
//        }
//        if (getWindow().superDispatchTouchEvent(ev)) {
//            return true;
//        }
//        return onTouchEvent(ev);
//    }
}
