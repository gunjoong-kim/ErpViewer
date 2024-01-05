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

    static final int COORDS_PER_VERTEX = 3;
    static final int COORDS_PER_TEXTURE = 2;
    static final int COORDS_PER_BUFFER = COORDS_PER_VERTEX + COORDS_PER_TEXTURE; // vertex 3 + tex 2
    private FloatBuffer mVertexBuffer;
    private ShortBuffer mIndexBuffer;
    private final int mProgram;
    private int mTexture;

    private final int mVertexCnt;
    private final int mIndiceCnt;
    private final int mVertexStride = COORDS_PER_VERTEX * 4;
    private static final String TAG = "ErpToPers";
    private int[] mVao;

    private Sphere mSphere;

    public ErpToPers()
    {
        mSphere = new Sphere(10.0f, 100, 100);
        mVertexCnt = mSphere.getVertices().length;
        mIndiceCnt = mSphere.getIndices().length;

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

        int vertexShader = MyGLRenderer.compileShader(mVertexCode, GLES30.GL_VERTEX_SHADER);
        int fragmentShader = MyGLRenderer.compileShader(mFragmentCode, GLES30.GL_FRAGMENT_SHADER);

        mProgram = GLES30.glCreateProgram();
        GLES30.glAttachShader(mProgram, vertexShader);
        GLES30.glAttachShader(mProgram, fragmentShader);
        GLES30.glLinkProgram(mProgram);

        // vao
        mVao = new int[1];
        GLES30.glGenVertexArrays(1, mVao, 0);
        GLES30.glBindVertexArray(mVao[0]);

        // vbo
        int[] vbo = new int[1];
        GLES30.glGenBuffers(1, vbo, 0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vbo[0]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, 4 * mVertexCnt, mVertexBuffer, GLES30.GL_STATIC_DRAW);

        int positionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        GLES30.glEnableVertexAttribArray(positionHandle);
        GLES30.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES30.GL_FLOAT, false, COORDS_PER_BUFFER * 4, 0);

        int textureHandle = GLES30.glGetAttribLocation(mProgram, "aTexCoord");
        GLES30.glEnableVertexAttribArray(textureHandle);
        GLES30.glVertexAttribPointer(textureHandle, COORDS_PER_TEXTURE, GLES30.GL_FLOAT, false, COORDS_PER_BUFFER * 4, COORDS_PER_VERTEX * 4);

        int[] ebo = new int[1];
        GLES30.glGenBuffers(1, ebo, 0);
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, ebo[0]);
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, 2 * mIndiceCnt, mIndexBuffer, GLES30.GL_STATIC_DRAW);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        GLES30.glBindVertexArray(0);
    }

    public void draw(float[] mvpMatrix)
    {
        GLES30.glUseProgram(mProgram);

        GLES30.glBindVertexArray(mVao[0]);

        int mvpMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLES30.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

        int textureUniformHandle = GLES30.glGetUniformLocation(mProgram, "uTexture");
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTexture);
        GLES30.glUniform1i(textureUniformHandle, 0);

        GLES30.glDrawElements(GLES30.GL_TRIANGLES, mIndiceCnt, GLES30.GL_UNSIGNED_SHORT, 0);
    }

    private void checkGLError(String operation) {
        int error;
        while ((error = GLES30.glGetError()) != GLES30.GL_NO_ERROR) {
            Log.e("MyGLRenderer", operation + ": glError " + error);
            throw new RuntimeException(operation + ": glError " + error);
        }
    }

    public void setTexture(int texture)
    {
        mTexture = texture;
    }
}
