package com.project.intellifit_trainer;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;

public class AutoFitTextureView extends TextureView {

    private int ratioWidth = 0;
    private int ratioHeight = 0;

    public AutoFitTextureView(Context context) {
        this(context, null);
    }

    public AutoFitTextureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoFitTextureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setAspectRatio(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }
        ratioWidth = width;
        ratioHeight = height;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d("CameraDebug", "onMeasure start: widthMeasureSpec = " + MeasureSpec.toString(widthMeasureSpec) + ", heightMeasureSpec = " + MeasureSpec.toString(heightMeasureSpec));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (ratioWidth == 0 || ratioHeight == 0) {
            setMeasuredDimension(width, height);
        } else {
            // Görüntüyü doğru oranda göstermek için ölçeklendirme
            if (width < height * ratioWidth / ratioHeight) {
                width = height * ratioWidth / ratioHeight;
            } else {
                height = width * ratioHeight / ratioWidth;
            }
            Log.d("CameraDebug", "onMeasure end: Measured width = " + width + ", Measured height = " + height);
            setMeasuredDimension(width, height);
        }
    }

}
