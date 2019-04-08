package com.pertamina.brightgas;

import android.app.Application;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class GlobalActivity extends Application {

    public static final int LEGACY = 0;
    public static final int FIREBASE = 1;
    public static final int SWAGGER = 2;

    public static int mBackendServiceProvider;

    @Override
    public void onCreate() {
        super.onCreate();

        // Change backend service provider configuration here
        // as this global variable will be called everytime backend API called
        mBackendServiceProvider = SWAGGER;
    }

    public static int getBackEndServiceProvider() {
        return mBackendServiceProvider;
    }

    public static String getCalculatedPrice(long input) {

        String millionString = "";
        String thousandString = "";
        String unitString = "";

        long million = input / 1000000;
        if (million > 0) {
            millionString = million + "";
        }

        long thousand = (input % 1000000) / 1000;
        if (thousand > 0) {
            if (million > 0 && thousand < 100) {
                thousandString = "0" + thousand;
            } else {
                thousandString = thousand + "";
            }
        } else {
            if (million != 0) {
                thousandString = "000";
            }
        }

        long unit = (input % 1000);
        if (unit > 0) {
            if (thousand > 0 || million > 0) {
                if (unit < 100) {
                    unitString = "0" + unit;
                } else {
                    unitString = unit + "";
                }
            } else {
                unitString = unit + "";
            }
        } else {
            if (thousand > 0 || million > 0) {
                unitString = "000";
            }
        }

        if (millionString.length() > 0) {
            return millionString + "." + thousandString + "." + unitString;

        } else if (thousandString.length() > 0) {
            return thousandString + "." + unitString;

        } else if (unitString.length() > 0) {
            return unitString;

        } else {
            return "0";
        }
    }

    public static AnimationSet createAnimationSet(Animation[] listAnimation) {
        return createAnimationSet(listAnimation, false);
    }

    public static AnimationSet createAnimationSet(Animation[] listAnimation, boolean isRepeat) {
        return createAnimationSet(listAnimation, isRepeat, new AccelerateInterpolator());
    }

    public static AnimationSet createAnimationSet(Animation[] listAnimation, boolean isRepeat, Interpolator interpolator) {
        return createAnimationSet(listAnimation, isRepeat, interpolator, null);
    }

    public static AnimationSet createAnimationSet(Animation[] listAnimation, boolean isRepeat, Interpolator interpolator, Animation.AnimationListener animationListener) {
        AnimationSet animationSet = new AnimationSet(true);
        if (interpolator != null) {
            animationSet.setInterpolator(interpolator);
        }
        animationSet.setFillAfter(true);
        for (Animation anim : listAnimation) {
            animationSet.addAnimation(anim);
        }
        if (isRepeat) {
            animationSet.setRepeatCount(Animation.INFINITE);
        }
        if (animationListener != null) {
            animationSet.setAnimationListener(animationListener);
        }
        return animationSet;
    }

    public static ScaleAnimation createScaleAnimation(float fromX, float toX, float fromY, float toY, float pivotX, float pivotY, long duration, Animation.AnimationListener listener) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(fromX, toX, fromY, toY, Animation.RELATIVE_TO_SELF, pivotX, Animation.RELATIVE_TO_SELF, pivotY);
        scaleAnimation.setFillAfter(true);
        if (duration > 0)
            scaleAnimation.setDuration(duration);
        if (listener != null)
            scaleAnimation.setAnimationListener(listener);
        return scaleAnimation;
    }

    public static ScaleAnimation createScaleAnimation(float fromX, float toX, float fromY, float toY, float pivotX, float pivotY, long duration) {
        return createScaleAnimation(fromX, toX, fromY, toY, pivotX, pivotY, duration, null);
    }

    public static AlphaAnimation createAlphaAnimation(float fromAlpha, float toAlpha, long duration, Animation.AnimationListener listener) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(fromAlpha, toAlpha);
        alphaAnimation.setFillAfter(true);
        alphaAnimation.setDuration(duration);
        if (listener != null)
            alphaAnimation.setAnimationListener(listener);
        return alphaAnimation;
    }

    public static AlphaAnimation createAlphaAnimation(float fromAlpha, float toAlpha, long duration) {
        return createAlphaAnimation(fromAlpha, toAlpha, duration, null);
    }

    public static String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date);
    }

}
