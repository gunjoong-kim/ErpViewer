package com.example.erpviewer;

import android.opengl.GLES20;
import android.util.Log;
import java.nio.FloatBuffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

public class ErpToPers
{
    private final String mVertexCode =
            "#define PI 3.1415926535897932384626433832795\n" +
                    "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 aPosition;" +
                    "attribute vec2 aTexCoord;" +
                    "varying vec2 vTexCoord;" +
                    "void main()" +
                    "{" +
                    "   gl_Position = uMVPMatrix * aPosition;" +
                    "   vTexCoord = aTexCoord;" +
                    "}";
    private final String mFragmentCode =
                "precision mediump float;" +
                "varying vec2 vTexCoord;" +
                "uniform sampler2D uTexture;" +
                "void main()" +
                "{" +
                "   gl_FragColor = texture2D(uTexture, vTexCoord);" +
                "}";

    static final int COORDS_PER_VERTEX = 3;
    static final int COORDS_PER_TEXTURE = 2;
    private FloatBuffer mVertexBuffer;
    private FloatBuffer mTexBuffer;
    private ShortBuffer mIndexBuffer;

    private final int mProgram;
    private int mTexture;

    private final int mVertexCnt;
    private final int mTexCoordCnt;
    private final int mIndiceCnt;
    private final int mVertexStride = COORDS_PER_VERTEX * 4;
    private final int mTexCoordStride = COORDS_PER_TEXTURE * 4;
    private static final String TAG = "ErpToPers";

    private Sphere mSphere;

    public ErpToPers()
    {
        mSphere = new Sphere(10.0f, 50, 50);
        mVertexCnt = mSphere.getVertices().length;
        mIndiceCnt = mSphere.getIndices().length;
        mTexCoordCnt = mSphere.getTexCoords().length;

        ByteBuffer bb = ByteBuffer.allocateDirect(mVertexCnt * 4);
        bb.order(ByteOrder.nativeOrder());
        mVertexBuffer = bb.asFloatBuffer();
        mVertexBuffer.put(mSphere.getVertices());
        mVertexBuffer.position(0);

        ByteBuffer ib = ByteBuffer.allocateDirect(mIndiceCnt * 2);
        ib.order(ByteOrder.nativeOrder());
        mIndexBuffer = ib.asShortBuffer();
        mIndexBuffer.put(mSphere.getIndices());
        mIndexBuffer.position(0);

        ByteBuffer tb = ByteBuffer.allocateDirect(mTexCoordCnt * 4);
        tb.order(ByteOrder.nativeOrder());
        mTexBuffer = tb.asFloatBuffer();
        mTexBuffer.put(mSphere.getTexCoords());
        mTexBuffer.position(0);

        int vertexShader = MyGLRenderer.compileShader(mVertexCode, GLES20.GL_VERTEX_SHADER);
        int fragmentShader = MyGLRenderer.compileShader(mFragmentCode, GLES20.GL_FRAGMENT_SHADER);

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }

    public void draw(float[] mvpMatrix)
    {
        GLES20.glUseProgram(mProgram);

        int positionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, mVertexStride, mVertexBuffer);

        int textureHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoord");
        GLES20.glEnableVertexAttribArray(textureHandle);
        GLES20.glVertexAttribPointer(textureHandle, COORDS_PER_TEXTURE, GLES20.GL_FLOAT, false, mTexCoordStride, mTexBuffer);

        int mvpMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

        int textureUniformHandle = GLES20.glGetUniformLocation(mProgram, "uTexture");
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture);
        GLES20.glUniform1i(textureUniformHandle, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, mIndiceCnt, GLES20.GL_UNSIGNED_SHORT, mIndexBuffer);
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(textureHandle);
    }

    public void setTexture(int texture)
    {
        mTexture = texture;
    }
}
