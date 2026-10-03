package com.example.crs2025.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 * Utility class to apply realistic 3D touch tilt, 3D flip, and 3D floating animations to views.
 */
public class ThreeDCardHelper {

    private static final float MAX_TILT_ANGLE = 15.0f; // Maximum 3D rotation degrees

    /**
     * Enables interactive 3D Touch Tilt on any View/Card.
     * When touched and dragged, the view tilts towards the touch point in 3D space with perspective depth.
     */
    @SuppressLint("ClickableViewAccessibility")
    public static void enable3DTouchTilt(View view) {
        if (view == null) return;

        // Set camera distance for realistic 3D perspective
        float scale = view.getResources().getDisplayMetrics().density;
        view.setCameraDistance(8000 * scale);

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float width = v.getWidth();
                float height = v.getHeight();

                if (width == 0 || height == 0) return false;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE: {
                        float touchX = event.getX();
                        float touchY = event.getY();

                        // Normalize touch coordinates to range [-1, 1] relative to center
                        float normalizedX = (touchX - (width / 2.0f)) / (width / 2.0f);
                        float normalizedY = (touchY - (height / 2.0f)) / (height / 2.0f);

                        // Clamp values [-1, 1]
                        normalizedX = Math.max(-1.0f, Math.min(1.0f, normalizedX));
                        normalizedY = Math.max(-1.0f, Math.min(1.0f, normalizedY));

                        // Calculate 3D rotation angles
                        float rotY = normalizedX * MAX_TILT_ANGLE;
                        float rotX = -normalizedY * MAX_TILT_ANGLE;

                        v.animate()
                                .rotationX(rotX)
                                .rotationY(rotY)
                                .scaleX(1.03f)
                                .scaleY(1.03f)
                                .translationZ(16f)
                                .setDuration(80)
                                .setInterpolator(new DecelerateInterpolator())
                                .start();
                        return true;
                    }

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        // Smoothly spring back to flat 3D position
                        v.animate()
                                .rotationX(0f)
                                .rotationY(0f)
                                .scaleX(1.0f)
                                .scaleY(1.0f)
                                .translationZ(0f)
                                .setDuration(400)
                                .setInterpolator(new OvershootInterpolator(1.2f))
                                .start();
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            v.performClick();
                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }

    /**
     * Performs a 3D Flip animation on a card around its Y axis.
     */
    public static void perform3DFlip(final View cardView, final Runnable onHalfwayCallback) {
        if (cardView == null) return;

        float scale = cardView.getResources().getDisplayMetrics().density;
        cardView.setCameraDistance(8000 * scale);

        ObjectAnimator flip1 = ObjectAnimator.ofFloat(cardView, "rotationY", 0f, 90f);
        flip1.setDuration(250);
        flip1.setInterpolator(new DecelerateInterpolator());

        final ObjectAnimator flip2 = ObjectAnimator.ofFloat(cardView, "rotationY", -90f, 0f);
        flip2.setDuration(250);
        flip2.setInterpolator(new OvershootInterpolator(1.1f));

        flip1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (onHalfwayCallback != null) {
                    onHalfwayCallback.run();
                }
                flip2.start();
            }
        });

        flip1.start();
    }

    /**
     * Starts a continuous, gentle 3D floating & breathing effect on a view.
     */
    public static void start3DFloating(View view) {
        if (view == null) return;

        float scale = view.getResources().getDisplayMetrics().density;
        view.setCameraDistance(8000 * scale);

        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 0f, -12f, 0f);
        PropertyValuesHolder pvhRotX = PropertyValuesHolder.ofFloat(View.ROTATION_X, 0f, 2.5f, 0f);
        PropertyValuesHolder pvhRotY = PropertyValuesHolder.ofFloat(View.ROTATION_Y, 0f, -2.5f, 0f);

        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, pvhY, pvhRotX, pvhRotY);
        animator.setDuration(3500);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.start();
    }
}
