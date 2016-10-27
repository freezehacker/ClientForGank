package com.sysu.sjk.custom_view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import com.sysu.sjk.utils.Logger;
import com.sysu.sjk.view.R;

/**
 * Created by sjk on 16-10-23.
 */
public class RoundProgressView extends View {

    // Default value.
    public static final boolean SHOW_NUMBER = true;
    public static final int CUR_NUMBER = 0;
    public static final int INITIAL_NUMBER = 0;
    public static final float RADIUS = 15;
    public static final float STROKE_WIDTH = 3;
    public static final int TEXT_COLOR = Color.BLACK;
    public static final int TEXT_SIZE = 10;

    /* All attrs are public, convenient for user to modify directly. */
    public boolean showNumber;
    public int curNumber, initialNumber;
    public float radius;
    public float strokeWidth;
    public int textColor;
    public float textSize;

    private Paint mCirclePaint, mTextPaint;
    private RectF mRectF;
    private float mWidth, mHeight;

    public RoundProgressView(Context context) {
        super(context, null);
    }

    public RoundProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    // get value of attrs, which are assigned from user in XML.
    private void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.RoundProgressView);
        radius = typedArray.getDimension(R.styleable.RoundProgressView_radius, RADIUS);
        showNumber = typedArray.getBoolean(R.styleable.RoundProgressView_showNumber, SHOW_NUMBER);
        strokeWidth = typedArray.getDimension(R.styleable.RoundProgressView_strokeWidth, STROKE_WIDTH);
        textSize = typedArray.getDimension(R.styleable.RoundProgressView_textSize, TEXT_SIZE);
        curNumber = typedArray.getInt(R.styleable.RoundProgressView_curNumber, CUR_NUMBER);
        initialNumber = typedArray.getInt(R.styleable.RoundProgressView_initialNumber, INITIAL_NUMBER);
        textColor = typedArray.getColor(R.styleable.RoundProgressView_textColor, TEXT_COLOR);

        // never forget to recycle the typed array
        typedArray.recycle();

        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setDither(true);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(strokeWidth);
        mCirclePaint.setColor(textColor);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setColor(textColor);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // Get the width and height here,
        // not in measure/layout/draw process.
        // Better ways?
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Logger.log("width height: " + getWidth() + "," + getHeight());
                mWidth = getWidth();
                mHeight = getHeight();

                if (mRectF == null) {
                    float left = mWidth / 2 - radius;
                    float top = mHeight / 2 - radius;
                    float right = mWidth / 2 + radius;
                    float bottom = mHeight / 2 + radius;
                    mRectF = new RectF(left, top, right, bottom);
                }
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(
                measureWidth(widthMeasureSpec),
                measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int widthMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int ret = 720;
        if (mode == MeasureSpec.EXACTLY) {
            ret = size;
        } else {
            if (mode == MeasureSpec.AT_MOST) {
                ret = Math.min(ret, size);
            }
        }
        return ret;
    }

    private int measureHeight(int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        int ret = 1280;
        if (mode == MeasureSpec.EXACTLY) {
            ret = size;
        } else {
            if (mode == MeasureSpec.AT_MOST) {
                ret = Math.min(ret, size);
            }
        }
        return ret;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawView(canvas);
    }

    // main method to draw the whole view
    private void drawView(Canvas canvas) {
        /*int width = getWidth();
        int height = getHeight();*/
        if (showNumber) {
            String str = String.valueOf(curNumber);
            canvas.drawText(str, mWidth / 2 - mTextPaint.measureText(str) / 2, mHeight / 2 + textSize / 3, mTextPaint);
        }
        canvas.drawArc(mRectF, -90, 3.6f * curNumber, false, mCirclePaint);
    }

    public void setProgress(int progress) {
        if (curNumber != progress) {    // To save.
            curNumber = progress;
            invalidate();
        }
    }

    public int getProgress() {
        return curNumber;
    }
}
