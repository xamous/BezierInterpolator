package net.xamous.bezierinterpolatordemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import net.xamous.bezierinterpolator.BezierInterpolator;

public class BezierView extends View {

    private final static int BORDER_COLOR = 0xFF888888;
    private final static int BASELINE_COLOR = 0xFFCCCCCC;
    private final static int HANDLE_COLOR_START = 0x77FF4081;
    private final static int HANDLE_COLOR_END = 0x770099CC;

    private Path mPath = new Path();
    private Paint mPaint = new Paint();
    private BezierInterpolator mInterpolator;
    private int mStrokeWidth;
    private float mIndicatorX = 0;
    private PointF mPoint1 = new PointF();
    private PointF mPoint2 = new PointF();
    private PointF mDraggingPoint = null;

    public interface OnCurveChangedListener {
        void onCurveChanged(float x1, float y1, float x2, float y2);
    }
    private OnCurveChangedListener mListener;
    public void setOnCurveChangedListener(OnCurveChangedListener listener) {
        mListener = listener;
    }


    public BezierView(Context context) {
        super(context);
        init();
    }

    public BezierView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BezierView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mInterpolator = new BezierInterpolator(0, 0, 1, 1);
        mStrokeWidth = getContext().getResources().getDimensionPixelSize(R.dimen.stroke_width);
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setAntiAlias(true);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec * 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int range = getRange();
        canvas.translate((getWidth() - range) / 2, (getHeight() - range) / 2);

        drawBaseline(canvas, range, range);
        drawBorder(canvas, range, range);
        drawCurve(canvas, range, range);
        drawHandles(canvas, range, range);
    }

    private static PointF sPointTmp = new PointF();
    private void drawHandles(Canvas canvas, int width, int height) {
        mPaint.setStyle(Paint.Style.FILL);
        final int handleRadius = getHandleSize();

        mPaint.setColor(mDraggingPoint == mPoint1 ? 0xFF000000 | HANDLE_COLOR_START : HANDLE_COLOR_START);
        mapPoint(mPoint1, sPointTmp);
        canvas.drawCircle(sPointTmp.x, sPointTmp.y, handleRadius, mPaint);
        canvas.drawLine(sPointTmp.x, sPointTmp.y, 0, height, mPaint);

        mPaint.setColor(mDraggingPoint == mPoint2 ? 0xFF000000 | HANDLE_COLOR_END : HANDLE_COLOR_END);
        mapPoint(mPoint2, sPointTmp);
        canvas.drawCircle(sPointTmp.x, sPointTmp.y, handleRadius, mPaint);
        canvas.drawLine(sPointTmp.x, sPointTmp.y, width, 0, mPaint);

        mPaint.setStyle(Paint.Style.STROKE);
    }

    private void drawBaseline(Canvas canvas, int width, int height) {
        mPaint.setColor(BASELINE_COLOR);
        canvas.drawLine(0, height, width, 0, mPaint);
    }

    private void drawBorder(Canvas canvas, int width, int height) {
        mPaint.setColor(BORDER_COLOR);
        canvas.drawRect(0, 0, width, height, mPaint);
    }

    private void drawCurve(Canvas canvas, int width, int height) {
        mPaint.setStyle(Paint.Style.FILL);
        mPath.reset();
        int pointX = (int) (mIndicatorX * width);
        for (int x = 0; x <= width; x++) {
            int y = (int) ((1 - mInterpolator.getInterpolation((float) x / width)) * height);
            if (x == 0) mPath.moveTo(x, y);
            else mPath.lineTo(x, y);

            if (x == pointX) {
                canvas.drawLine(0, y, x, y, mPaint);
                canvas.drawCircle(x, y, getIndicatorSize(), mPaint);
                canvas.drawCircle(0, y, getIndicatorSize(), mPaint);
            }
        }
        mPaint.setShader(new LinearGradient(0, height, width, 0,
                0XFF000000 | HANDLE_COLOR_START, 0XFF000000 | HANDLE_COLOR_END,
                Shader.TileMode.MIRROR));
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(mPath, mPaint);
        mPaint.setShader(null);
    }

    private int getHandleSize() {
        return getWidth() / 30;
    }

    private int getIndicatorSize() {
        return getWidth() / 50;
    }

    private int getRange() {
        return getWidth() - mStrokeWidth * 2 - getHandleSize() * 2;
    }

    public void setCurve(float x1, float y1, float x2, float y2) {
        mPoint1.set(x1, y1);
        mPoint2.set(x2, y2);
        mInterpolator = new BezierInterpolator(x1, y1, x2, y2);
        invalidate();
    }

    public void setIndicatorPos(float x) {
        mIndicatorX = x;
        invalidate();
    }

    public float getIndicatorPos() {
        return mIndicatorX;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float x = event.getX() - (getWidth() - getRange()) / 2;
                float y = event.getY() - (getHeight() - getRange()) / 2;
                if (hitPoint(mPoint1, x, y)) {
                    mDraggingPoint = mPoint1;
                }
                else if (hitPoint(mPoint2, x, y)) {
                    mDraggingPoint = mPoint2;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mDraggingPoint != null) {
                    movePoint(event.getX(), event.getY());
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mDraggingPoint != null) {
                    if (mListener != null) {
                        mListener.onCurveChanged(mPoint1.x, mPoint1.y, mPoint2.x, mPoint2.y);
                    }
                    mDraggingPoint = null;
                }
                break;
        }
        return true;
    }

    private void movePoint(float x, float y) {
        int range = getRange();
        int leftOffset = (getWidth() - range) / 2;
        int topOffset = (getHeight() - range) / 2;
        float ptX = Math.max(0, Math.min((x - leftOffset) / range, 1));
        float ptY = 1 - Math.max(-.5f, Math.min((y - topOffset) / range, 1.5f));

        mDraggingPoint.set(ptX, ptY);

        mInterpolator = new BezierInterpolator(mPoint1.x, mPoint1.y, mPoint2.x, mPoint2.y);

        invalidate();
    }

    private void mapPoint(PointF interpolationPoint, PointF outViewPoint) {
        outViewPoint.set(interpolationPoint.x * getRange(), (1 - interpolationPoint.y) * getRange());
    }

    private static RectF sRect = new RectF();
    private boolean hitPoint(PointF interpolationPoint, float x, float y) {
        mapPoint(interpolationPoint, sPointTmp);
        int hitSize = getHandleSize() * 2;
        sRect.set(sPointTmp.x - hitSize, sPointTmp.y - hitSize,
                sPointTmp.x + hitSize, sPointTmp.y + hitSize);
        return sRect.contains(x, y);
    }
}
