package com.example.erpviewer;
import android.opengl.GLES30;

import java.nio.FloatBuffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

public class Sphere {
    private float[] mVertices; // 정점 데이터
    private short[] mIndices; // 인덱스 데이터
    private FloatBuffer mVertexBuffer;
    private ShortBuffer mIndexBuffer;
    private int mVertexCnt;
    private int mIndiceCnt;
    private final int mVertexStride = COORDS_PER_VERTEX * 4;
    static final int COORDS_PER_VERTEX = 3;
    static final int COORDS_PER_TEXTURE = 2;
    static final int COORDS_PER_BUFFER = COORDS_PER_VERTEX + COORDS_PER_TEXTURE;
    private int mProgram;
    private int[] mVao;

    public Sphere(float radius, int stacks, int slices, String vertexCode, String fragCode) {
        createVertices(radius, stacks, slices);
        createIndices(stacks, slices);
        mProgram = MyGLRenderer.createProgram(vertexCode, fragCode);
        initVao();
    }

    public void initVao()
    {
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

    private void createVertices(float radius, int stacks, int slices) {
        int vertexCount = (stacks + 1) * (slices + 1);
        float[] vertices = new float[vertexCount * 5];

        for (int stackNumber = 0; stackNumber <= stacks; ++stackNumber) {
            float phi = (float) (Math.PI / stacks * stackNumber); // 위도
            for (int sliceNumber = 0; sliceNumber <= slices; ++sliceNumber) {
                float theta = (float) (2.0 * Math.PI / slices * sliceNumber); // 경도

                float x = (float) (Math.sin(phi) * Math.cos(theta) * radius);
                float y = (float) (Math.cos(phi) * radius);
                float z = (float) (Math.sin(phi) * Math.sin(theta) * radius);

                // vertex coord
                vertices[(stackNumber * (slices + 1) + sliceNumber) * 5] = x;
                vertices[(stackNumber * (slices + 1) + sliceNumber) * 5 + 1] = y;
                vertices[(stackNumber * (slices + 1) + sliceNumber) * 5 + 2] = z;
                // tex coord
                vertices[(stackNumber * (slices + 1) + sliceNumber) * 5 + 3] = (float)sliceNumber / slices;
                vertices[(stackNumber * (slices + 1) + sliceNumber) * 5 + 4] = (float)stackNumber / stacks;
            }
        }

        mVertexCnt = vertices.length;

        ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        mVertexBuffer = bb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);
    }

    private void createIndices(int stacks, int slices) {
        int indexCount = (stacks) * (slices) * 6;
        short[] indices = new short[indexCount];

        int index = 0;
        for (short stackNumber = 0; stackNumber < stacks; ++stackNumber) {
            for (short sliceNumber = 0; sliceNumber < slices; ++sliceNumber) {
                short first = (short) ((stackNumber * (slices + 1)) + sliceNumber);
                short second = (short) (first + slices + 1);

                indices[index++] = first;
                indices[index++] = second;
                indices[index++] = (short) (first + 1);

                indices[index++] = (short) (first + 1);
                indices[index++] = second;
                indices[index++] = (short) (second + 1);
            }
        }

        mIndiceCnt = indices.length;

        ByteBuffer ib = ByteBuffer.allocateDirect(indices.length * 2);
        ib.order(ByteOrder.nativeOrder());
        mIndexBuffer = ib.asShortBuffer();
        mIndexBuffer.put(indices);
        mIndexBuffer.position(0);
    }

    public void drawSphere(float[] mvpMatrix, int texture)
    {
        GLES30.glUseProgram(mProgram);
        GLES30.glBindVertexArray(mVao[0]);

        int mvpMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLES30.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

        int textureUniformHandle = GLES30.glGetUniformLocation(mProgram, "uTexture");
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texture);
        GLES30.glUniform1i(textureUniformHandle, 0);

        GLES30.glDrawElements(GLES30.GL_TRIANGLES, mIndiceCnt, GLES30.GL_UNSIGNED_SHORT, 0);
    }
}