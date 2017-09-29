package com.kst.funddemo;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by dell on 2017/2/20.
 */

public class GLSurfaceViewActivity extends Activity {
    private GLSurfaceView glSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setRenderer(new GLSurfaceViewRender());
        setContentView(glSurfaceView);
    }

    // Activity窗口暂停(pause)或恢复(resume)时，GLSurfaceView都会收到通知，此时它的onPause方法和 onResume方法应该被调用。
    // 这样做是为了让GLSurfaceView暂停或恢复它的渲染线程，以便它及时释放或重建OpenGL的资源。
    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView.onPause();
    }

    class GLSurfaceViewRender implements GLSurfaceView.Renderer {

        // 设置OpenGL的环境变量，或是初始化OpenGL的图形物体
        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            Log.v("xxx","Renderer-onSurfaceCreated");
            // 设置背景颜色
            gl.glClearColor(0.0f, 0f, 1f, 0.5f);
        }

        // 系统在GLSurfaceView的几何属性发生改变时调用该方法，包括大小或是设备屏幕的方向发生变化。
        // 例如，系统在屏幕从直立变为水平使调用它。这个方法主要用来对GLSurfaceView容器的变化进行响应
        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            Log.v("xxx","Renderer-onSurfaceChanged");
            // 设置输出屏幕大小
            gl.glViewport(0, 0, width, height);
        }

        // 主要完成绘制图形的操作
        @Override
        public void onDrawFrame(GL10 gl) {
            Log.v("xxx","Renderer-onDrawFrame");
            // 清除屏幕和深度缓存(如果不调用该代码, 将不显示glClearColor设置的颜色)
            // 同样如果将该代码放到 onSurfaceCreated 中屏幕会一直闪动
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        }
    }
}
