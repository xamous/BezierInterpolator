# BezierInterpolator
An implementation of Android [Interpolator](http://developer.android.com/reference/android/view/animation/Interpolator.html) where the rate of change following given [Bézier curve](https://en.wikipedia.org/wiki/B%C3%A9zier_curve). Including an utility app allows motion designers to tune the curve easily.

#Usage

    Interpolator interpolator = new BezierInterpolator(0.101f, 0.765f, 0.293f, 0.957f);
    someAnimator.setInterpolator(interpolator);

#Utility app
![screenshot of sample app](https://github.com/xamous/BezierInterpolator/blob/master/images-folder/screenshot.png)


