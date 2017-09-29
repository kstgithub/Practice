package com.kst.funddemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;

import com.kst.funddemo.customview.SurfacePraView;

/**
 * Created by dell on 2017/2/9.
 */

public class CircleViewActivity extends Activity{
    private String threadName = Thread.currentThread().getName();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Log.v("kst","handlerThread="+ Thread.currentThread().getName());
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.activity_circle_view);
//        Log.v("kst","onCreate");
//        Log.v("kst","thread="+Thread.currentThread().getName());
//        Log.v("kst","threadName="+threadName);
//
//        handler.sendEmptyMessage(1);

        setContentView(new SurfacePraView(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v("kst","onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v("kst","onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("kst","onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v("kst","onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v("kst","onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v("kst","onDestroy");
    }
}
