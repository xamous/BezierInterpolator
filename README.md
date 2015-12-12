# Deprecated
Android Support Library already provided a [PathInterpolatorCompat](http://developer.android.com/intl/zh-tw/reference/android/support/v4/view/animation/PathInterpolatorCompat.html) with similar feature.
The utility app already been modified to support PathInterpolatorCompat, it's still a handy tool to communicate with motion designers.

# BezierInterpolator
An implementation of Android [Interpolator](http://developer.android.com/reference/android/view/animation/Interpolator.html) where the rate of change following given [Bézier curve](https://en.wikipedia.org/wiki/B%C3%A9zier_curve). Including an utility app allows motion designers to tune the curve easily.

#Usage

    Interpolator interpolator = new BezierInterpolator(0.101f, 0.765f, 0.293f, 0.957f);
    someAnimator.setInterpolator(interpolator);

#Utility app
![screenshot of sample app](https://github.com/xamous/BezierInterpolator/blob/master/images-folder/screenshot.png)  

Icon credit: [Jerry Low](https://www.iconfinder.com/jerrylow)

#License

Copyright 2015 William Shih (xamous)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
