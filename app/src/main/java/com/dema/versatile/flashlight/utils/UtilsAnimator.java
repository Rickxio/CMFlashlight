package com.dema.versatile.flashlight.utils;

import android.animation.Animator;
import android.animation.ValueAnimator;

import com.airbnb.lottie.LottieAnimationView;

/**
 * Created by banana on 2019/4/25.
 */
public class UtilsAnimator {
    public static void releaseValueAnimator(ValueAnimator animator) {
        if (animator != null) {
            animator.removeAllListeners();
            animator.removeAllUpdateListeners();
            animator.cancel();
        }
    }

    public static void releaseAnimator(Animator animator) {
        if (animator != null) {
            animator.removeAllListeners();
            animator.cancel();
        }
    }

    public static void releaseLottieNim(LottieAnimationView animationView) {
        if (animationView != null) {
            animationView.removeAllAnimatorListeners();
            animationView.removeAllLottieOnCompositionLoadedListener();
            animationView.removeAllUpdateListeners();
            animationView.cancelAnimation();
        }
    }

    public static void startLottieAnim(LottieAnimationView lottieAnimationView, String folderName) {
        if (lottieAnimationView.isAnimating()) {
            lottieAnimationView.cancelAnimation();
        }
        lottieAnimationView.setImageAssetsFolder(folderName + "/images");
        lottieAnimationView.setAnimation(folderName + "/data.json");
        lottieAnimationView.setRepeatCount(0);
        lottieAnimationView.removeAllAnimatorListeners();
        lottieAnimationView.playAnimation();
    }
}
