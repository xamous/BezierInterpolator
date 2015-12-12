package net.xamous.bezierinterpolatordemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import net.xamous.bezierinterpolator.BezierInterpolator;



public class SampleActivity extends AppCompatActivity implements BezierView.OnCurveChangedListener, SeekBar.OnSeekBarChangeListener {

    protected ImageView mActor;
    protected BezierView mBezierView;
    protected SeekBar mSeekBar;
    protected TextView mDurationText;
    protected TextView mParamText;

    protected ValueAnimator mActorAnim;
    protected ValueAnimator mIndicatorAnim;
    protected AnimatorSet mAnimatorSet;
    protected BezierInterpolator mInterpolator;
    protected long mDuration;

    protected final static float DEFAULT_X1 = 0;
    protected final static float DEFAULT_Y1 = .5f;
    protected final static float DEFAULT_X2 = .5f;
    protected final static float DEFAULT_Y2 = 1;
    protected final static int DEFAULT_DURATION = 1500;

    protected final static int REPEAT_DELAY = 500;
    protected final static int MIN_DURATION = 100;
    protected final static int MAX_DURATION = 10000;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        mActor = (ImageView) findViewById(R.id.actor);
        mDurationText = (TextView) findViewById(R.id.duration);
        mParamText = (TextView) findViewById(R.id.parameters);

        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mSeekBar.setOnSeekBarChangeListener(this);
        mSeekBar.setMax(MAX_DURATION - MIN_DURATION);
        mSeekBar.setProgress(DEFAULT_DURATION - MIN_DURATION);

        mBezierView = (BezierView) findViewById(R.id.bezierView);
        mBezierView.setCurve(DEFAULT_X1, DEFAULT_Y1, DEFAULT_X2, DEFAULT_Y2);
        mBezierView.setOnCurveChangedListener(this);

        onCurveChanged(DEFAULT_X1, DEFAULT_Y1, DEFAULT_X2, DEFAULT_Y2);

        int actorWidth = mActor.getDrawable().getIntrinsicWidth();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        mActorAnim = ObjectAnimator.ofFloat(mActor, "translationX", actorWidth,
                metrics.widthPixels - actorWidth * 2);
        mActorAnim.setInterpolator(mInterpolator);

        mIndicatorAnim = ObjectAnimator.ofFloat(mBezierView, "indicatorPos", 0, 1);
        mIndicatorAnim.setInterpolator(new LinearInterpolator());

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playTogether(mActorAnim, mIndicatorAnim);


        mAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mActor.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startAnimation();
                    }
                }, REPEAT_DELAY);
            }
        });
        startAnimation();
    }

    @Override
    public void onCurveChanged(float x1, float y1, float x2, float y2) {
        mInterpolator = new BezierInterpolator(x1, y1, x2, y2);
        mParamText.setText(String.format(getString(R.string.parameters), x1, y1, x2, y2));
    }

    private void setDuration(int duration) {
        mDuration = duration;
        mDurationText.setText(String.format(getString(R.string.duration), mDuration));
    }

    private void startAnimation() {
        mActorAnim.setInterpolator(mInterpolator);
        mAnimatorSet.setDuration(mDuration);
        mAnimatorSet.start();
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        setDuration(MIN_DURATION + progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) { }
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) { }
}
