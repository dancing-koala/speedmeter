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

    /**
     * Default color for the indicator's circle background
     */
    private static final int DEFAULT_BG_COLOR = Color.parseColor("#252525");
    /**
     * Default color for the needle
     */
    private static final int DEFAULT_NEEDLE_COLOR = Color.parseColor("#FF8800");
    /**
     * Default color for the indicator's outer ring
     */
    private static final int DEFAULT_OUTER_RING_COLOR = Color.parseColor("#D3D3D3");
    /**
     * Default maximum speed for the indicator
     */
    private static final float DEFAULT_MAX_SPEED = 150f;
    /**
     * Default initial angle of the needle
     */
    private static final float DEFAULT_INITIAL_ANGLE = -45f;
    /**
     * Default maximum rotation angle of the needle
     */
    private static final float DEFAULT_MAX_ROTATION_ANGLE = 270f;

    /**
     * Bitmap used as a cache for the static elements drawn in the view
     */
    private Bitmap mCacheBitmap;
    /**
     * Canvas used to draw on the cache bitmap
     */
    private Canvas mCacheCanvas;
    /**
     * Horizontal coordinate for the view's center
     */
    private float mCenterX;
    /**
     * Vertical coordinate for the view's center
     */
    private float mCenterY;
    /**
     * The current inclination of the needle
     */
    private float mCurrentAngle;
    /**
     * Radius of the background's circle
     */
    private float mRadius;
    /**
     * The view's calculated height
     */
    private int mHeight;
    /**
     * The view's calculated width
     */
    private int mWidth;
    /**
     * The matrix used to rotate the needle
     */
    private Matrix mMatrix;
    /**
     * Paint to draw the neelde
     */
    private Paint mNeedlePaint;
    /**
     * Paint to draw the background
     */
    private Paint mBgPaint;
    /**
     * Path to draw and rotate the needle
     */
    private Path mNeedlePath;

    /**
     * Constructor
     *
     * @see View#View(Context)
     */
    public SpeedMeterView(Context context) {
        super(context);
        init();
    }

    /**
     * Constructor
     *
     * @see View#View(Context, AttributeSet)
     */
    public SpeedMeterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * Constructor
     *
     * @see View#View(Context, AttributeSet, int)
     */
    public SpeedMeterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Constructor
     *
     * @see View#View(Context, AttributeSet, int, int)
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SpeedMeterView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    /**
     * Initializing the components of the view
     */
    private void init() {
        // We initialize the paint used to draw the needle
        mNeedlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mNeedlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mNeedlePaint.setStrokeWidth(4);
        mNeedlePaint.setColor(DEFAULT_NEEDLE_COLOR);

        // We initialize the paint used to draw te background
        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint.setStyle(Paint.Style.FILL);

        mNeedlePath = new Path();
        mMatrix = new Matrix();
        mCurrentAngle = 0f;
        mCacheCanvas = new Canvas();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // If the width and height changed, we need to recalculate some components
        // and redraw everything.
        if (mWidth != MeasureSpec.getSize(widthMeasureSpec) || mHeight != MeasureSpec.getSize(heightMeasureSpec)) {

            // We set the width, height and radius
            mWidth = MeasureSpec.getSize(widthMeasureSpec);
            mHeight = MeasureSpec.getSize(heightMeasureSpec);
            mRadius = Math.min(mWidth, mHeight) / 2f;

            // We set the center coordinates
            mCenterX = mWidth / 2f;
            mCenterY = mHeight / 2f;

            // Setting the cache bitmap to null means we must redraw the static content
            mCacheBitmap = null;

            // We draw the needle with the new center and radius values
            mNeedlePath.reset();
            mNeedlePath.moveTo(mCenterX, mCenterY);
            mNeedlePath.lineTo(mCenterX - (mRadius * 0.9f), mCenterY);

            // We rotate the needle to its initial inclination
            mMatrix.setRotate(DEFAULT_INITIAL_ANGLE, mCenterX, mCenterY);
            mNeedlePath.transform(mMatrix);
        }

        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // If there is no cache bitmap, we draw all the static content to
        // the cache canvas.
        if (mCacheBitmap == null) {
            mCacheBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
            mCacheCanvas.setBitmap(mCacheBitmap);

            mBgPaint.setColor(DEFAULT_OUTER_RING_COLOR);
            mCacheCanvas.drawCircle(mCenterX, mCenterY, mRadius, mBgPaint);

            mBgPaint.setColor(DEFAULT_BG_COLOR);
            mCacheCanvas.drawCircle(mCenterX, mCenterY, mRadius - 12, mBgPaint);

            mCacheCanvas.drawCircle(mCenterX, mCenterY, 10, mNeedlePaint);
        }

        super.onDraw(canvas);

        // We draw the cache bitmap to the main canvas of the view
        canvas.drawBitmap(mCacheBitmap, 0, 0, mBgPaint);

        canvas.drawPath(mNeedlePath, mNeedlePaint);
    }

    /**
     * Sets the current speed and animates the needle
     *
     * @param speed Speed to be set
     */
    public void updateSpeed(float speed) {
        // The speed value should not go over the DEFAULT_MAX_SPEED
        speed = (speed > DEFAULT_MAX_SPEED) ? DEFAULT_MAX_SPEED : speed;

        final float rotation = (speed * DEFAULT_MAX_ROTATION_ANGLE / DEFAULT_MAX_SPEED) - mCurrentAngle;

        mCurrentAngle += rotation;

        // The needle is rotated with a ValueAnimator to get a smooth animation
        ValueAnimator angleAnim = ValueAnimator.ofFloat(0, rotation);
        angleAnim.setDuration(600);
        angleAnim.setInterpolator(new OvershootInterpolator());
        angleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            private float lastAnimatedValue = 0;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // The rotation to apply is the delta between each animated value
                mMatrix.setRotate(((float) animation.getAnimatedValue() - lastAnimatedValue), mCenterX, mCenterY);
                lastAnimatedValue = (float) animation.getAnimatedValue();
                mNeedlePath.transform(mMatrix);
                // We redraw the view with the new needle rotation
                invalidate();
            }
        });

        angleAnim.start();
    }
}
