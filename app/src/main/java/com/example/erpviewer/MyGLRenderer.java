package com.example.erpviewer;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.media.MediaExtractor;
import android.net.Uri;

public class MyGLRenderer implements GLSurfaceView.Renderer {
    private ErpToPers mErpToPers;
    private Context mContext;
    private MediaExtractor mExtractor;
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mModelMatrix = new float[16];

    private volatile float mThetaAngle;
    private volatile float mPhiAngle;
    private volatile float mNear = 3.0f;
    private volatile float mFar = 10.0f;
    private volatile float mRatio;
    public MyGLRenderer(Context context)
    {
        super();
        mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        mErpToPers = new ErpToPers();
        mErpToPers.setTexture(loadTexture(R.drawable.warehouse));


        // Set Background Color Black
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    }

    public void onDrawFrame(GL10 unused)
    {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        Matrix.frustumM(mProjectionMatrix, 0, -mRatio, mRatio, -1, 1, mNear, mFar);
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 0, 0f, 0f, 3f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        float[] scratch = new float[16];

        Matrix.setRotateM(mModelMatrix, 0, mPhiAngle, 1.0f, 0, 0);
        Matrix.rotateM(mModelMatrix, 0, mThetaAngle, 0, 1.0f, 0);

        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mModelMatrix, 0);

        mErpToPers.draw(scratch);
    }

    public void onSurfaceChanged(GL10 unused, int width, int height)
    {
        mRatio = (float) width / height;
        GLES30.glViewport(0, 0, width, height);

        Matrix.frustumM(mProjectionMatrix, 0, -mRatio, mRatio, -1, 1, mNear, mFar);
    }

    public void changeTexture(int resourceId)
    {
        mErpToPers.setTexture(loadTexture(resourceId));
    }

    public int loadTexture(int resourceId)
    {
        final int[] textureHandle = new int[1];

        GLES30.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0)
        {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;
            final Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), resourceId, options);

            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureHandle[0]);

            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
            GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);
            bitmap.recycle();
        }

        return textureHandle[0];
    }

    public static int compileShader(String shaderCode, int type)
    {
        int shader = GLES30.glCreateShader(type);
        GLES30.glShaderSource(shader, shaderCode);
        GLES30.glCompileShader(shader);

        int[] compileStatus = new int[1];
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compileStatus, 0);

        if (compileStatus[0] == 0)
        {
            String compileLog = GLES30.glGetShaderInfoLog(shader);
            Log.e("Shader", "shader compilation failed:\n" + compileLog);
            GLES30.glDeleteShader(shader);
        }

        return shader;
    }

    public float getTheta()
    {
        return mThetaAngle;
    }

    public float getPhi()
    {
        return mPhiAngle;
    }
    public float getNear() { return mNear; }

    public void setTheta(float theta)
    {
        mThetaAngle =  theta;
    }

    public void setPhi(float phi)
    {
        mPhiAngle = phi;
    }
    public void setNear(float near)
    {
        // set the near value to zoom in/out
        if (near > mFar - 1.0f || near < 1)
            return ;
        mNear = near;
    }
}
