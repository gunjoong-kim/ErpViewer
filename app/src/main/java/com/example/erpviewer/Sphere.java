package com.example.erpviewer;

public class Sphere {
    private float[] mVertices; // 정점 데이터
    private float[] mTexCoords;
    private short[] mIndices; // 인덱스 데이터

    public Sphere(float radius, int stacks, int slices) {
        createVertices(radius, stacks, slices);
        createIndices(stacks, slices);
    }

    private void createVertices(float radius, int stacks, int slices) {
        int vertexCount = (stacks + 1) * (slices + 1);
        mVertices = new float[vertexCount * 3];
        mTexCoords = new float[vertexCount * 2];

        for (int stackNumber = 0; stackNumber <= stacks; ++stackNumber) {
            float phi = (float) (Math.PI / stacks * stackNumber); // 위도
            for (int sliceNumber = 0; sliceNumber <= slices; ++sliceNumber) {
                float theta = (float) (2.0 * Math.PI / slices * sliceNumber); // 경도

                float x = (float) (Math.sin(phi) * Math.cos(theta) * radius);
                float y = (float) (Math.cos(phi) * radius);
                float z = (float) (Math.sin(phi) * Math.sin(theta) * radius);

                mVertices[(stackNumber * (slices + 1) + sliceNumber) * 3 + 0] = x;
                mVertices[(stackNumber * (slices + 1) + sliceNumber) * 3 + 1] = y;
                mVertices[(stackNumber * (slices + 1) + sliceNumber) * 3 + 2] = z;

                mTexCoords[(stackNumber * (slices + 1) + sliceNumber) * 2 + 0] = (float)sliceNumber / slices;
                mTexCoords[(stackNumber * (slices + 1) + sliceNumber) * 2 + 1] = (float)stackNumber / stacks;
            }
        }
    }

    private void createIndices(int stacks, int slices) {
        int indexCount = (stacks) * (slices) * 6;
        mIndices = new short[indexCount];

        int index = 0;
        for (short stackNumber = 0; stackNumber < stacks; ++stackNumber) {
            for (short sliceNumber = 0; sliceNumber < slices; ++sliceNumber) {
                short first = (short) ((stackNumber * (slices + 1)) + sliceNumber);
                short second = (short) (first + slices + 1);

                mIndices[index++] = first;
                mIndices[index++] = second;
                mIndices[index++] = (short) (first + 1);

                mIndices[index++] = (short) (first + 1);
                mIndices[index++] = second;
                mIndices[index++] = (short) (second + 1);
            }
        }
    }

    // 정점 및 인덱스 데이터에 접근하기 위한 getter 메서드
    public float[] getVertices() {
        return mVertices;
    }

    public short[] getIndices() {
        return mIndices;
    }

    public float[] getTexCoords() {
        return mTexCoords;
    }
}