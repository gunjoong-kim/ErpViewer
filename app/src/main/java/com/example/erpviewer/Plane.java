package com.example.erpviewer;

public class Plane
{
    // To do : Plane을 화면과 동일하게 구성, 각 클래스에 gl 초기화 코드 넣기
    float[] mVertices = {
            // X, Y, Z, U, V
            -1.0f,  1.0f, 0.0f, 1.0f, 0.0f,
            1.0f, -1.0f, 0.0f, 0.0f, 1.0f,
            1.0f,  1.0f, 0.0f, 0.0f, 0.0f,
            -1.0f, -1.0f, 0.0f, 1.0f, 1.0f
    };

    short[] mIndices = {
            0, 1, 2,
            0, 3, 1
    };

    public Plane() {}
}
