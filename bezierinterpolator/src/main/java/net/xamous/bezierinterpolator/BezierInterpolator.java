package net.xamous.bezierinterpolator;

import android.view.animation.Interpolator;

/**
 * An interpolator where the rate of change following a given cubic bezier Curve.
 */
public class BezierInterpolator implements Interpolator {

    private final float mX1;
    private final float mY1;
    private final float mX2;
    private final float mY2;

    public BezierInterpolator(float x1, float y1, float x2, float y2) {
        mX1 = x1;
        mY1 = y1;
        mX2 = x2;
        mY2 = y2;
    }

    @Override
    public float getInterpolation(float aX) {
        if (mX1 == mY1 && mX2 == mY2) return aX; // Optimize: linear
        return calcBezier(getTForX(aX), mY1, mY2);
    }

    float getTForX(float aX) {
        float aGuessT = aX;
        for (int i = 0; i < 4; ++i) {
            float currentSlope = getSlope(aGuessT, mX1, mX2);
            if (currentSlope == 0.0) return aGuessT;
            float currentX = calcBezier(aGuessT, mX1, mX2) - aX;
            aGuessT -= currentX / currentSlope;
        }
        return aGuessT;
    }

    float calcBezier(float aT, float aA1, float aA2) {
        return ((a(aA1, aA2)*aT + b(aA1, aA2))*aT + c(aA1))*aT;
    }

    float getSlope(float aT, float aA1, float aA2) {
        return (float) (3.0 * a(aA1, aA2)*aT*aT + 2.0 * b(aA1, aA2) * aT + c(aA1));
    }

    float a(float aA1, float aA2) { return (float) (1.0 - 3.0 * aA2 + 3.0 * aA1); }
    float b(float aA1, float aA2) { return (float) (3.0 * aA2 - 6.0 * aA1); }
    float c(float aA1)      { return (float) (3.0 * aA1); }
}
