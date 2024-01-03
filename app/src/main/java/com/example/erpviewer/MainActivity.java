package com.example.erpviewer;

import androidx.appcompat.app.AppCompatActivity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity
{
    private GLSurfaceView mErpView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mErpView = new MyGLSurfaceView(this);
        setContentView(mErpView);
    }
}
