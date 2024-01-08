package com.example.erpviewer;
import android.opengl.GLES30;

import java.nio.FloatBuffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
public class Plane
{
    // To do : Plane을 화면과 동일하게 구성, 각 클래스에 gl 초기화 코드 넣기
    float[] mVertices = {
            // X, Y, Z, U, V
            -1.0f,  1.0f, 0.0f, 0.0f, 0.0f,
            1.0f, -1.0f, 0.0f, 1.0f, 1.0f,
            1.0f,  1.0f, 0.0f, 1.0f, 0.0f,
            -1.0f, -1.0f, 0.0f, 0.0f, 1.0f
    };

    short[] mIndices = {
            0, 1, 2,
            0, 3, 1
    };

    private int mProgram;
    private int[] mVao;

    public Plane(String vertexCode, String fragCode)
    {
        mProgram = MyGLRenderer.createProgram(vertexCode, fragCode);
        initVao();
    }

    private void initVao()
    {
        mVao = new int[1];
        GLES30.glGenVertexArrays(1, mVao, 0);
        GLES30.glBindVertexArray(mVao[0]);

        FloatBuffer vertexBuffer = ByteBuffer.allocateDirect(mVertices.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(mVertices).position(0);

        int[] vbo = new int[1];
        GLES30.glGenBuffers(1, vbo, 0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vbo[0]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, mVertices.length * 4, vertexBuffer, GLES30.GL_STATIC_DRAW);

        int positionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        GLES30.glEnableVertexAttribArray(positionHandle);
        GLES30.glVertexAttribPointer(positionHandle, 3, GLES30.GL_FLOAT, false, 5 * 4, 0);

        int textureHandle = GLES30.glGetAttribLocation(mProgram, "aTexCoord");
        GLES30.glEnableVertexAttribArray(textureHandle);
        GLES30.glVertexAttribPointer(textureHandle, 2, GLES30.GL_FLOAT, false, 5 * 4, 12);

        ShortBuffer indexBuffer = ByteBuffer.allocateDirect(mIndices.length * 2)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        indexBuffer.put(mIndices).position(0);

        int[] ebo = new int[1];
        GLES30.glGenBuffers(1, ebo, 0);
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, ebo[0]);
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, 2 * mIndices.length, indexBuffer, GLES30.GL_STATIC_DRAW);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        GLES30.glBindVertexArray(0);
    }

    public void drawPlane(float[] mvpMatrix, int texture)
    {
        GLES30.glUseProgram(mProgram);
        GLES30.glBindVertexArray(mVao[0]);

        int textureUniformHandle = GLES30.glGetUniformLocation(mProgram, "uTexture");
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texture);
        GLES30.glUniform1i(textureUniformHandle, 0);

        GLES30.glDrawElements(GLES30.GL_TRIANGLES, mIndices.length, GLES30.GL_UNSIGNED_SHORT, 0);
    }
}
