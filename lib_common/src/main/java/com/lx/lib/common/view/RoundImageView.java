package com.lx.lib.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.lx.lib.common.R;

/**
 * Created by glennli on 2018/12/21.<br/>
 * 圆角图片，支持圆形、自定义四个圆角
 */
public final class RoundImageView extends AppCompatImageView {
    private float mBorderWidth;
    private boolean mRoundAsCircle, showMask;
    private int mBorderColor, mMaskColor;
    private Paint mBorderPaint, mPathPaint, mMaskPaint;
    private Path mCirclePath = new Path(), mCompatCirclePath = new Path();
    private PorterDuffXfermode mDstInMode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    private PorterDuffXfermode mDstOutMode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
    private float[] mRoundRadius = new float[8];
    private RectF mViewBounds = new RectF();

    public RoundImageView(Context context) {
        this(context, null);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView);
        mRoundAsCircle = array.getBoolean(R.styleable.RoundImageView_riv_roundAsCircle, false);
        mBorderColor = array.getColor(R.styleable.RoundImageView_riv_borderColor, 0);
        mMaskColor = array.getColor(R.styleable.RoundImageView_riv_maskColor, 0);
        showMask = array.getBoolean(R.styleable.RoundImageView_riv_showMask, false);
        mBorderWidth = array.getDimension(R.styleable.RoundImageView_riv_borderWidth, 0);
        if (!mRoundAsCircle) {
            float roundCorner = array.getDimension(R.styleable.RoundImageView_riv_roundCorner, 0);
            float topLeft = array.getDimension(R.styleable.RoundImageView_riv_cornerTopLeft, roundCorner);
            float topRight = array.getDimension(R.styleable.RoundImageView_riv_cornerTopRight, roundCorner);
            float bottomLeft = array.getDimension(R.styleable.RoundImageView_riv_cornerBottomLeft, roundCorner);
            float bottomRight = array.getDimension(R.styleable.RoundImageView_riv_cornerBottomRight, roundCorner);
            setRoundRadius(topLeft, topRight, bottomRight, bottomLeft);
        }
        array.recycle();
        init();
    }

    private void init() {
        mPathPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPathPaint.setStyle(Paint.Style.FILL);
        mMaskPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mMaskPaint.setStyle(Paint.Style.FILL);
        mMaskPaint.setColor(mMaskColor);
        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(mBorderWidth);
        mBorderPaint.setColor(mBorderColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int width = getWidth(), height = getHeight();
        mViewBounds.set(0, 0, width, height);
        preparePath(width, height);
        canvas.saveLayer(mViewBounds, null, Canvas.ALL_SAVE_FLAG);
        super.onDraw(canvas);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
            mPathPaint.setXfermode(mDstInMode);
            canvas.drawPath(mCirclePath, mPathPaint);
        } else {
            // 兼容9.0及以上裁剪圆角失效的问题
            mCompatCirclePath.reset();
            mCompatCirclePath.addRect(mViewBounds, Path.Direction.CW);
            mCompatCirclePath.op(mCirclePath, Path.Op.DIFFERENCE);
            mPathPaint.setXfermode(mDstOutMode);
            canvas.drawPath(mCompatCirclePath, mPathPaint);
        }
        drawMask(canvas);
        mPathPaint.setXfermode(null);
        canvas.restore();
        drawBorder(canvas, width, height);
    }

    private void drawMask(Canvas canvas) {
        if (mMaskColor == 0 || !showMask) {
            return;
        }
        canvas.drawPath(mCirclePath, mMaskPaint);
    }

    private void drawBorder(Canvas canvas, int width, int height) {
        if (!mRoundAsCircle || mBorderWidth <= 0) {
            return;
        }
        canvas.drawCircle(width / 2, height / 2, Math.min(width, height) / 2 - mBorderWidth / 2, mBorderPaint);
    }

    private void preparePath(int width, int height) {
        mCirclePath.reset();
        if (mRoundAsCircle) {
            mCirclePath.addCircle(width / 2, height / 2, Math.min(width, height) / 2, Path.Direction.CW);
        } else {
            mCirclePath.addRoundRect(mViewBounds, mRoundRadius, Path.Direction.CW);
        }
    }

    public void setRoundAsCircle(boolean roundAsCircle) {
        mRoundAsCircle = roundAsCircle;
        postInvalidate();
    }

    public void setBorder(int borderColor, float borderWidth) {
        mBorderColor = borderColor;
        mBorderWidth = borderWidth;
        mBorderPaint.setStrokeWidth(mBorderWidth);
        mBorderPaint.setColor(mBorderColor);
        postInvalidate();
    }

    public void setRoundRadius(float topLeft, float topRight, float bottomRight, float bottomLeft) {
        mRoundAsCircle = false;
        mRoundRadius[0] = topLeft;
        mRoundRadius[1] = topLeft;
        mRoundRadius[2] = topRight;
        mRoundRadius[3] = topRight;
        mRoundRadius[4] = bottomRight;
        mRoundRadius[5] = bottomRight;
        mRoundRadius[6] = bottomLeft;
        mRoundRadius[7] = bottomLeft;
        postInvalidate();
    }

    public void setMaskColor(int maskColor) {
        mMaskColor = maskColor;
        mMaskPaint.setColor(mMaskColor);
        postInvalidate();
    }

    public void showMask(boolean show) {
        showMask = show;
        postInvalidate();
    }
}
