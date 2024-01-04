package com.example.erpviewer;

import androidx.appcompat.app.AppCompatActivity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.FrameLayout;
import android.view.View;
import android.widget.Button;
import android.view.Gravity;

public class MainActivity extends AppCompatActivity
{
    private GLSurfaceView mErpView;
    private ImageView mImageView;
    private FrameLayout mContainer;
    private Button mToggleButton;
    private Button mChangeTexButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mContainer = new FrameLayout(this);
        mErpView = new MyGLSurfaceView(this);
        mErpView.setVisibility(View.GONE);
        mImageView = new ImageView(this);
        mImageView.setImageResource(R.drawable.warehouse);
        mImageView.setScaleType(ImageView.ScaleType.FIT_XY);

        mContainer.addView(mErpView);
        mContainer.addView(mImageView);
        mToggleButton = new Button(this);
        mToggleButton.setText("Toggle View");
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        mToggleButton.setLayoutParams(layoutParams);

        mToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                toggleView();
            }
        });

        mContainer.addView(mToggleButton);
        setContentView(mContainer);
    }

    public void toggleView()
    {
        if (mErpView.getVisibility() == View.VISIBLE)
        {
            mErpView.setVisibility(View.GONE);
            mImageView.setVisibility(View.VISIBLE);
        }
        else
        {
            mErpView.setVisibility(View.VISIBLE);
            mImageView.setVisibility(View.GONE);
        }
    }
}
