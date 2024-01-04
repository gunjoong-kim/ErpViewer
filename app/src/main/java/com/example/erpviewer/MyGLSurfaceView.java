package com.example.erpviewer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class MyGLSurfaceView extends GLSurfaceView
{
    private final MyGLRenderer mRenderer;
    private final float TOUCH_SCALE_FACTOR = 0.1f;
    private float mPreviousX;
    private float mPreviousY;

    public MyGLSurfaceView(Context context)
    {
        super(context);

        mPreviousX = 0;
        mPreviousY = 0;

        setEGLContextClientVersion(2);
        mRenderer = new MyGLRenderer(context);

        setRenderer(mRenderer);

        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e)
    {
        float x = e.getX();
        float y = e.getY();

        switch (e.getAction())
        {
            case MotionEvent.ACTION_MOVE:
                float dx = mPreviousX - x;
                float dy = y - mPreviousY;

                mRenderer.setTheta(mRenderer.getTheta() + dx * TOUCH_SCALE_FACTOR);
                mRenderer.setPhi(mRenderer.getPhi() + dy * TOUCH_SCALE_FACTOR);

                requestRender();
        }

        mPreviousX = x;
        mPreviousY = y;

        return true;
    }
}
