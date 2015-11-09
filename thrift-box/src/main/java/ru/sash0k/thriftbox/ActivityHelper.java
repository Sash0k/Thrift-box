package ru.sash0k.thriftbox;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.view.animation.AccelerateDecelerateInterpolator;

import ru.sash0k.thriftbox.numpad.AnimatorListenerWrapper;
import ru.sash0k.thriftbox.numpad.RevealColorView;

/**
 * Анимации и т.п.
 * Created by sash0k on 27.10.15.
 */
public abstract class ActivityHelper extends FragmentActivity {
    private static Typeface roubleSupportedTypeface;

    private ViewGroup mDisplayView;
    private RevealColorView revealColorView;
    private Animator mCurrentAnimator;

    private void cancelAnimation() {
        if (mCurrentAnimator != null) {
            mCurrentAnimator.end();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        roubleSupportedTypeface = Typeface.createFromAsset(getAssets(), Utils.ROUBLE_FONT);
    }
    // ============================================================================

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mDisplayView = (ViewGroup) findViewById(R.id.activity_main);

        if (!Utils.hasLollipop()) {
            revealColorView = (RevealColorView)findViewById(R.id.reveal);
            revealColorView.bringToFront();
        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();

        // If there's an animation in progress, end it immediately to ensure the state is
        // up-to-date before the pending user interaction is handled.
        cancelAnimation();
    }

    /**
     * Отображение символа рубля из кастомного шрифта
     */
    public SpannableStringBuilder parseRouble(CharSequence value) {
        if (roubleSupportedTypeface == null) return null;
        else {
            SpannableStringBuilder resultSpan = new SpannableStringBuilder(value);
            for (int i = 0; i < resultSpan.length(); i++) {
                if (resultSpan.charAt(i) == Utils.ROUBLE) {
                    TypefaceSpan2 roubleTypefaceSpan = new TypefaceSpan2(roubleSupportedTypeface);
                    resultSpan.setSpan(roubleTypefaceSpan, i, i + 1, 0);
                }
            }
            return resultSpan;
        }
    }
    // ============================================================================

    /**
     * Анимация при стирании значения
     */
    public void reveal(View sourceView, int colorRes, final AnimatorListenerWrapper listener) {
        if (Utils.hasLollipop()) reveal5(sourceView, colorRes, listener);
        else reveal4(sourceView, colorRes, listener);
    }
    // ============================================================================

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void reveal5(View sourceView, int colorRes, final AnimatorListenerWrapper listener) {
        final ViewGroupOverlay groupOverlay =
                (ViewGroupOverlay) getWindow().getDecorView().getOverlay();

        final Rect displayRect = new Rect();
        mDisplayView.getGlobalVisibleRect(displayRect);

        // Make reveal cover the display and status bar.
        final View revealView = new View(this);
        revealView.setBottom(displayRect.bottom);
        revealView.setLeft(displayRect.left);
        revealView.setRight(displayRect.right);
        revealView.setBackgroundColor(getResources().getColor(colorRes));
        groupOverlay.add(revealView);

        final int[] clearLocation = new int[2];
        sourceView.getLocationInWindow(clearLocation);
        clearLocation[0] += sourceView.getWidth() / 2;
        clearLocation[1] += sourceView.getHeight() / 2;

        final int revealCenterX = clearLocation[0] - revealView.getLeft();
        final int revealCenterY = clearLocation[1] - revealView.getTop();

        final double x1_2 = Math.pow(revealView.getLeft() - revealCenterX, 2);
        final double x2_2 = Math.pow(revealView.getRight() - revealCenterX, 2);
        final double y_2 = Math.pow(revealView.getTop() - revealCenterY, 2);
        final float revealRadius = (float) Math.max(Math.sqrt(x1_2 + y_2), Math.sqrt(x2_2 + y_2));

        final Animator revealAnimator =
                ViewAnimationUtils.createCircularReveal(revealView,
                        revealCenterX, revealCenterY, 0.0f, revealRadius);
        revealAnimator.setDuration(
                getResources().getInteger(android.R.integer.config_longAnimTime));

        final Animator alphaAnimator = ObjectAnimator.ofFloat(revealView, View.ALPHA, 0.0f);
        alphaAnimator.setDuration(
                getResources().getInteger(android.R.integer.config_mediumAnimTime));
        alphaAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                listener.onAnimationStart();
            }
        });

        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(revealAnimator).before(alphaAnimator);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                groupOverlay.remove(revealView);
                mCurrentAnimator = null;
            }
        });

        mCurrentAnimator = animatorSet;
        animatorSet.start();
    }

    private void reveal4(View sourceView, int colorRes, final AnimatorListenerWrapper listener) {
        final int[] clearLocation = new int[2];
        sourceView.getLocationInWindow(clearLocation);
        clearLocation[0] += sourceView.getWidth() / 2;
        clearLocation[1] += sourceView.getHeight() / 2;

        final int revealCenterX = clearLocation[0] - revealColorView.getLeft();
        final int revealCenterY = clearLocation[1] - revealColorView.getTop();

        final Animator revealAnimator =
                revealColorView.createCircularReveal(
                        revealCenterX, revealCenterY, getResources().getColor(colorRes));
        revealAnimator.setDuration(
                getResources().getInteger(android.R.integer.config_longAnimTime));

        final Animator alphaAnimator = ObjectAnimator.ofFloat(revealColorView, "alpha", 1, 0);
        alphaAnimator.setDuration(
                getResources().getInteger(android.R.integer.config_mediumAnimTime));
        alphaAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                listener.onAnimationStart();
            }
        });

        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(revealAnimator).before(alphaAnimator);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                mCurrentAnimator = null;
            }
        });

        mCurrentAnimator = animatorSet;
        animatorSet.start();
    }

}
