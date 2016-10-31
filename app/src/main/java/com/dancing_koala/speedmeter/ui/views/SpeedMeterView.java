package com.dancing_koala.speedmeter.ui.views;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

public class SpeedMeterView extends View {

    private static final int DEFAULT_OUTER_RING_COLOR = Color.parseColor("#D3D3D3");
    private static final int DEFAULT_BG_COLOR = Color.parseColor("#252525");
    private static final int DEFAULT_NEEDLE_COLOR = Color.parseColor("#FF8800");

    private static final float DEFAULT_MAX_SPEED = 180f;
    private static final float DEFAULT_INITIAL_ANGLE = -45f;
    private static final float DEFAULT_MAX_ROTATION_ANGLE = 270f;

    private Bitmap mCacheBitmap;
    private Canvas mCacheCanvas;
    private float mCenterX;
    private float mCenterY;
    private float mCurrentAngle;
    private float mRadius;
    private float mSpeed;
    private int mHeight;
    private int mWidth;
    private Matrix mMatrix;
    private Paint mNeedlePaint;
    private Paint mBgPaint;
    private Path mNeedlePath;
    private ValueAnimator angleAnim;

    public SpeedMeterView(Context context) {
        super(context);
        init(context);
    }

    public SpeedMeterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SpeedMeterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SpeedMeterView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        mNeedlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mNeedlePaint.setStyle(Paint.Style.STROKE);
        mNeedlePaint.setStrokeWidth(4);
        mNeedlePaint.setColor(DEFAULT_NEEDLE_COLOR);

        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint.setStyle(Paint.Style.FILL);
        mBgPaint.setColor(DEFAULT_OUTER_RING_COLOR);

        mNeedlePath = new Path();
        mMatrix = new Matrix();
        mCurrentAngle = 0f;
        mCacheCanvas = new Canvas();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mWidth != MeasureSpec.getSize(widthMeasureSpec) || mHeight != MeasureSpec.getSize(heightMeasureSpec)) {

            mWidth = MeasureSpec.getSize(widthMeasureSpec);
            mHeight = MeasureSpec.getSize(heightMeasureSpec);
            mRadius = Math.min(mWidth, mHeight) / 2f;

            mCenterX = mWidth / 2f;
            mCenterY = mHeight / 2f;

            mCacheBitmap = null;

            mNeedlePath.reset();
            mNeedlePath.moveTo(mCenterX, mCenterY);
            mNeedlePath.lineTo(mCenterX - mRadius, mCenterY);

            mMatrix.setRotate(DEFAULT_INITIAL_ANGLE, mCenterX, mCenterY);
            mNeedlePath.transform(mMatrix);

            setMeasuredDimension(mWidth, mHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mCacheBitmap == null) {
            mCacheBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
            mCacheCanvas.setBitmap(mCacheBitmap);

            mBgPaint.setColor(DEFAULT_OUTER_RING_COLOR);
            mCacheCanvas.drawCircle(mCenterX, mCenterY, mRadius, mBgPaint);
            mBgPaint.setColor(DEFAULT_BG_COLOR);
            mCacheCanvas.drawCircle(mCenterX, mCenterY, mRadius - 12, mBgPaint);

            mNeedlePaint.setStyle(Paint.Style.STROKE);
            mCacheCanvas.drawCircle(mCenterX, mCenterY, 10, mNeedlePaint);
        }

        super.onDraw(canvas);
        canvas.drawBitmap(mCacheBitmap, 0, 0, mBgPaint);
        canvas.drawPath(mNeedlePath, mNeedlePaint);
    }

    public void setSpeed(float speed) {
        mSpeed = (speed > DEFAULT_MAX_SPEED) ? DEFAULT_MAX_SPEED : speed;

        final float rotation = (mSpeed * DEFAULT_MAX_ROTATION_ANGLE / DEFAULT_MAX_SPEED) - mCurrentAngle;

        mCurrentAngle += rotation;

        angleAnim = ValueAnimator.ofFloat(0, rotation);
        angleAnim.setDuration(600);
        angleAnim.setInterpolator(new OvershootInterpolator());
        angleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            private float lastAnimatedValue = 0;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mMatrix.setRotate(((float) animation.getAnimatedValue() - lastAnimatedValue), mCenterX, mCenterY);
                lastAnimatedValue = (float) animation.getAnimatedValue();
                mNeedlePath.transform(mMatrix);
                invalidate();
            }
        });

        angleAnim.start();
    }
}
