package com.kst.funddemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

/**
 * 快看看
 * Created by dell on 2017/8/2.
 */

public class ViewActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_view);
    }
}
