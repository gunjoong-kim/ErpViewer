package com.example.erpviewer;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.util.Log;
import java.nio.FloatBuffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

public class ErpToPers
{
    private final String mVertexCode =
                    "#version 300 es\n" +
                    "uniform mat4 uMVPMatrix;" +
                    "layout(location = 0) in vec4 aPosition;" +
                    "layout(location = 1) in vec2 aTexCoord;" +
                    "out vec2 vTexCoord;" +
                    "void main()" +
                    "{" +
                    "   gl_Position = uMVPMatrix * aPosition;" +
                    "   vTexCoord = aTexCoord;" +
                    "}";

    private final String mVertexErpCode =
                    "#version 300 es\n" +
                    "layout(location = 0) in vec4 aPosition;" +
                    "layout(location = 1) in vec2 aTexCoord;" +
                    "out vec2 vTexCoord;" +
                    "void main()" +
                    "{" +
                    "   gl_Position = aPosition;" +
                    "   vTexCoord = aTexCoord;" +
                    "}";
    private final String mFragmentCode =
                    "#version 300 es\n" +
                    "precision mediump float;" +
                    "in vec2 vTexCoord;" +
                    "out vec4 fragColor;" +
                    "uniform sampler2D uTexture;" +
                    "void main()" +
                    "{" +
                    "   fragColor = texture(uTexture, vTexCoord);" +
                    "}";


    private int mTexture;
    private static final String TAG = "ErpToPers";
    private Sphere mSphere;
    private Plane mPlane;

    public ErpToPers()
    {
        mSphere = new Sphere(10.0f, 100, 100, mVertexCode, mFragmentCode);
        mPlane = new Plane(mVertexErpCode, mFragmentCode);
    }

    public void draw(float[] mvpMatrix)
    {
        mSphere.drawSphere(mvpMatrix, mTexture);
        //mPlane.drawPlane(mvpMatrix, mTexture);
    }

    private void checkGLError(String operation)
    {
        int error;
        if ((error = GLES30.glGetError()) != GLES30.GL_NO_ERROR)
        {
            Log.e("MyGLRenderer", operation + ": glError " + error);
            throw new RuntimeException(operation + ": glError " + error);
        }
    }

    public void setTexture(int texture)
    {
        mTexture = texture;
    }
}
