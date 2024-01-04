package com.example.erpviewer;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.util.Log;

public class MyGLRenderer implements GLSurfaceView.Renderer {
    private ErpToPers mErpToPers;
    private Context mContext;
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mModelMatrix = new float[16];

    private volatile float mThetaAngle;
    private volatile float mPhiAngle;

    public MyGLRenderer(Context context)
    {
        super();
        mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        mErpToPers = new ErpToPers();
        mErpToPers.setTexture(loadTexture());

        // Set Background Color Black
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    }

    public void onDrawFrame(GL10 unused)
    {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

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
        float ratio = (float) width / height;
        GLES20.glViewport(0, 0, width, height);
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 40);
    }

    public int loadTexture()
    {
        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;
            final Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.image2, options);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            bitmap.recycle();
        }

        return textureHandle[0];
    }

    public static int compileShader(String shaderCode, int type)
    {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

        if (compileStatus[0] == 0) {
            String compileLog = GLES20.glGetShaderInfoLog(shader);
            Log.e("Shader", "shader compilation failed:\n" + compileLog);
            GLES20.glDeleteShader(shader);
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

    public void setTheta(float theta)
    {
        mThetaAngle =  theta;
    }

    public void setPhi(float phi)
    {
        mPhiAngle = phi;
    }
}
