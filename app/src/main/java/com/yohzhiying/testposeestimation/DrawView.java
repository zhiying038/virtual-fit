package com.yohzhiying.testposeestimation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;

public class DrawView extends View {

    static Outfit currentOutfit;
    private int mRatioWidth = 0;
    private int mRatioHeight = 0;
    private ArrayList<PointF> mDrawPoint = new ArrayList<>();
    private int mWidth = 0;
    private int mHeight = 0;
    private float mRatioX = 0;
    private float mRatioY = 0;
    private int mImgWidth = 0;
    private int mImgHeight = 0;
    private Paint mPaint = new Paint();

    private static final String TAG = "DrawView";

    //Constructors
    public DrawView(Context context){ super(context); }
    public DrawView(Context context, AttributeSet attrs){ super(context,attrs); }
    public DrawView(Context context, AttributeSet attrs, int defStyleAttr)
    { super(context,attrs,defStyleAttr); }


    public void setImgSize(int width, int height) {
        mImgWidth = width;
        mImgHeight = height;
        requestLayout();
    }

    public void setDrawPoint(float[][] point, float ratio) {
        mDrawPoint.clear();

        float tempX;
        float tempY;

        for(int index = 0; index < 14; index++) {
            tempX = point[0][index] /ratio/mRatioX;
            tempY = point[1][index] /ratio/mRatioY;
            mDrawPoint.add(new PointF(tempX, tempY));
        }
    }

    public void setAspectRatio(int width, int height) {
        if(width < 0 || height < 0) {
            throw new IllegalArgumentException("Size can not be negative");
        }

        mRatioWidth = width;
        mRatioHeight = height;
        requestLayout();
    }


    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDrawPoint.isEmpty()) {
            return;
        }

        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(dipToFloat(2));

        Bitmap outfit_bmp = currentOutfit.getImage();

        //Coordinates to fit "Top" outfit
        int top_left = (int) (mDrawPoint.get(2).x-60); //The X coordinate of the left side of the rectangle
        int top_top  = (int) (mDrawPoint.get(1).y-10); //The Y coordinate of the top of the rectangle
        int top_right = (int) (mDrawPoint.get(5).x+60); //The X coordinate of the right side of the rectangle
        int top_bottom = (int) (mDrawPoint.get(8).y+10); //The Y coordinate of the bottom of the rectangle
        Rect rect_top = new Rect(top_left,top_top,top_right,top_bottom);

        //Coordinates to fit "LONG WEAR" outfit
        int long_left = (int) (mDrawPoint.get(2).x-60); //The X coordinate of the left side of the rectangle
        int long_top  = (int) (mDrawPoint.get(1).y-10); //The Y coordinate of the top of the rectangle
        int long_right = (int) (mDrawPoint.get(5).x+60); //The X coordinate of the right side of the rectangle
        int long_bottom = (int) (mDrawPoint.get(9).y+10); //The Y coordinate of the bottom of the rectangle
        Rect rect_long = new Rect(long_left,long_top,long_right,long_bottom);

        //Coordinates to fit "TROUSERS" outfit
        int trousers_left = (int) (mDrawPoint.get(8).x-60); //The X coordinate of the left side of the rectangle
        int trousers_top  = (int) (mDrawPoint.get(8).y-10); //The Y coordinate of the top of the rectangle
        int trousers_right = (int) (mDrawPoint.get(11).x+60); //The X coordinate of the right side of the rectangle
        int trousers_bottom = (int) (mDrawPoint.get(10).y+10); //The Y coordinate of the bottom of the rectangle
        Rect rect_trousers = new Rect(trousers_left,trousers_top,trousers_right,trousers_bottom);

        //Coordinates to fit "SHORTS N SKIRTS" outfit
        int short_left = (int) (mDrawPoint.get(8).x-60); //The X coordinate of the left side of the rectangle
        int short_top  = (int) (mDrawPoint.get(8).y-10); //The Y coordinate of the top of the rectangle
        int short_right = (int) (mDrawPoint.get(11).x+60); //The X coordinate of the right side of the rectangle
        int short_bottom = (int) (mDrawPoint.get(9).y+10); //The Y coordinate of the bottom of the rectangle
        Rect rect_short = new Rect(short_left,short_top,short_right,short_bottom);

        Rect dst_rect = rect_top;
        if(currentOutfit.getOutfitCategory().equals("Top")) {
            dst_rect = rect_top;
        }

        if(currentOutfit.getOutfitCategory().equals("Long Wears")) {
            dst_rect = rect_long;
        }

        if(currentOutfit.getOutfitCategory().equals("Trousers")) {
            dst_rect = rect_trousers;
        }

        if(currentOutfit.getOutfitCategory().equals("Shorts & Skirts")) {
            dst_rect = rect_short;
        }

        canvas.drawBitmap(outfit_bmp, null, dst_rect, null);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if(mRatioWidth == 0 || mRatioHeight == 0){setMeasuredDimension(width, height);}
        else {
            if (width < height * mRatioWidth / mRatioHeight) {
                mWidth = width;
                mHeight = width * mRatioHeight / mRatioWidth;
            } else {
                mWidth = height * mRatioWidth / mRatioHeight;
                mHeight = height;
            }
        }

        setMeasuredDimension(mWidth, mHeight);

        try {
            mRatioX = (float) mImgWidth / mWidth;
            mRatioY = (float) mImgHeight / mHeight;
        } catch(ArithmeticException e) {
            mRatioX =1;
            mRatioY=1;
        }
    }

    //Convert a dip value into a float
    private float dipToFloat(float val) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, val,
                getContext().getResources().getDisplayMetrics());
    }
}
