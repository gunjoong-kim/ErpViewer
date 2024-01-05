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
    private float mPreviousDistance;

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

        switch (e.getAction() & MotionEvent.ACTION_MASK) // Changed to handle multi-touch events
        {
            case MotionEvent.ACTION_MOVE:
            {
                if (e.getPointerCount() == 1) {
                    float dx = mPreviousX - x;
                    float dy = y - mPreviousY;

                    mRenderer.setTheta(mRenderer.getTheta() + dx * TOUCH_SCALE_FACTOR);
                    mRenderer.setPhi(mRenderer.getPhi() + dy * TOUCH_SCALE_FACTOR);
                }

                if (e.getPointerCount() == 2) {
                    float newDist = spacing(e);
                    if (newDist > 10f) {
                        float scale = newDist / mPreviousDistance;
                        mRenderer.setNear(mRenderer.getNear() * scale);
                    }
                    mPreviousDistance = newDist;
                }

                requestRender();
                break;
            }

            case MotionEvent.ACTION_POINTER_DOWN:
            {
                mPreviousDistance = spacing(e);
                break;
            }

            case MotionEvent.ACTION_POINTER_UP:
            {
                break;
            }
        }

        mPreviousX = x;
        mPreviousY = y;

        return true;
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }
}
