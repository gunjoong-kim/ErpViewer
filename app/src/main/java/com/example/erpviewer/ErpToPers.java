package com.example.erpviewer;

import android.opengl.GLES20;
import java.nio.FloatBuffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.lang.Math;
import android.util.Log;


public class ErpToPers
{
    /*private final String mVertexCode =
                "uniform mat4 uMVPMatrix;" +
                "attribute vec4 aPosition;" +
                "attribute vec2 aTexCoord;" +
                "varying vec2 vTexCoord;" +
                "void main()" +
                "{" +
                "   gl_Position = uMVPMatrix * aPosition;" +
                "   vTexCoord = aTexCoord;" +
                "}";*/

    private final String mVertexCode =
            "#define PI 3.1415926535897932384626433832795\n" +
                    "uniform mat4 uMVPMatrix;\n" +
                    "attribute vec4 aPosition;\n" +
                    "attribute vec2 aTexCoord;\n" +
                    "varying vec2 vTexCoord;\n" +
                    "void main()\n" +
                    "{\n" +
                    "   gl_Position = uMVPMatrix * aPosition;\n" +
                    "   vec2 tmp;\n" +
                    "   float cosc = cos(PI / 2.0 - aPosition.y) - cos(aPosition.y) * cos(aPosition.x);\n" +
                    "   tmp.x = (cos(aPosition.y) * sin(aPosition.x)) / cosc;\n" +
                    "   tmp.y = (cos(PI / 2.0) * sin(aPosition.y) + sin(PI / 2.0) * cos(aPosition.y) * cos(aPosition.x)) / cosc;\n" +
                    "   vTexCoord = tmp;\n" +
                    "}";
    private final String mFragmentCode =
                "precision mediump float;" +
                "uniform vec4 aColor;" +
                "varying vec2 vTexCoord;" +
                "uniform sampler2D uTexture;" +
                "void main()" +
                "{" +
                "   gl_FragColor = texture2D(uTexture, vTexCoord);" +
                "}";
    private FloatBuffer mVertexBuffer;
    private FloatBuffer mTexBuffer;
    private ShortBuffer mIndexBuffer;

    private final int mProgram;
    private int mTexture;

    /*static float imageCoords[] = {
            -1.0f,  1.0f, 0.0f,
             1.0f, -1.0f, 0.0f,
             1.0f,  1.0f, 0.0f,
            -1.0f, -1.0f, 0.0f
    };*/

    static float imageCoords[] = {
            (float)Math.toRadians(0.0), (float)Math.toRadians(50.0), 0.0f,
            (float)Math.toRadians(100.0), (float)Math.toRadians(-50.0), 0.0f,
            (float)Math.toRadians(100.0), (float)Math.toRadians(50.0), 0.0f,
            (float)Math.toRadians(0.0), (float)Math.toRadians(-50.0), 0.0f,
    };

    static float texCoords[] = {
            1.0f, 0.0f,
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 1.0f
    };

    static short indices[] = {
            0, 1, 2,
            0, 3, 1
    };

    float defaultColor[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

    static final int COORDS_PER_VERTEX = 3;
    private final int vetextCount = imageCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4;
    private static final String TAG = "MyRenderer";

    public ErpToPers()
    {
        ByteBuffer bb = ByteBuffer.allocateDirect(imageCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        mVertexBuffer = bb.asFloatBuffer();
        mVertexBuffer.put(imageCoords);
        mVertexBuffer.position(0);

        ByteBuffer ib = ByteBuffer.allocateDirect(indices.length * 2);
        ib.order(ByteOrder.nativeOrder());
        mIndexBuffer = ib.asShortBuffer();
        mIndexBuffer.put(indices);
        mIndexBuffer.position(0);

        ByteBuffer tb = ByteBuffer.allocateDirect(texCoords.length * 4);
        tb.order(ByteOrder.nativeOrder());
        mTexBuffer = tb.asFloatBuffer();
        mTexBuffer.put(texCoords);
        mTexBuffer.position(0);

        int vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vertexShader, mVertexCode);
        GLES20.glCompileShader(vertexShader);

        int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(vertexShader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

        if (compileStatus[0] == 0) {
            String compileLog = GLES20.glGetShaderInfoLog(vertexShader);
            Log.e(TAG, "Vertex shader compilation failed:\n" + compileLog);
            GLES20.glDeleteShader(vertexShader);
        }

        int fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fragmentShader, mFragmentCode);
        GLES20.glCompileShader(fragmentShader);

        GLES20.glGetShaderiv(fragmentShader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

        if (compileStatus[0] == 0) {
            String compileLog = GLES20.glGetShaderInfoLog(fragmentShader);
            Log.e(TAG, "Fragment shader compilation failed:\n" + compileLog);
            GLES20.glDeleteShader(vertexShader);
            GLES20.glDeleteShader(fragmentShader);
        }

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }

    public void draw(float[] mvpMatrix, int texture)
    {
        GLES20.glUseProgram(mProgram);

        int positionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 12, mVertexBuffer);

        int textureHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoord");
        GLES20.glEnableVertexAttribArray(textureHandle);
        GLES20.glVertexAttribPointer(textureHandle, 2, GLES20.GL_FLOAT, false, 8, mTexBuffer);

        int colorHandle = GLES20.glGetUniformLocation(mProgram, "aColor");
        GLES20.glUniform4fv(colorHandle, 1, defaultColor, 0);
        int mvpMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        int textureUniformHandle = GLES20.glGetUniformLocation(mProgram, "uTexture");
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
        GLES20.glUniform1i(textureUniformHandle, 0);

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length, GLES20.GL_UNSIGNED_SHORT, mIndexBuffer);
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(textureHandle);
    }
}
