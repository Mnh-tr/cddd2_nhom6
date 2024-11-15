package com.example.cddd2_nhom6.model;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;

import com.example.cddd2_nhom6.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AnimatedBottomNavigationView extends BottomNavigationView {

    private static final long ANIMATION_DURATION = 300;
    private int selectedItemId = -1;
    private View indicator;

    public AnimatedBottomNavigationView(@NonNull Context context) {
        super(context);
        init();
    }

    public AnimatedBottomNavigationView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnimatedBottomNavigationView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (selectedItemId != item.getItemId()) {
                    animateIcon(item);
                    selectedItemId = item.getItemId();
                }
                return true;
            }
        });

        post(new Runnable() {
            @Override
            public void run() {
                indicator = ((ViewGroup) getParent()).findViewById(R.id.indicator);
                updateIndicatorPosition(getMenu().getItem(0));
            }
        });
    }

    private void animateIcon(MenuItem item) {
        View icon = findViewById(item.getItemId());
        if (icon != null) {
            // Scale animation
            icon.animate()
                    .scaleX(1.2f)
                    .scaleY(1.2f)
                    .setDuration(ANIMATION_DURATION)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();

            // Color animation
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), Color.GRAY, Color.BLUE);
            colorAnimation.setDuration(ANIMATION_DURATION);
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    int color = (int) animator.getAnimatedValue();
                    Drawable drawable = item.getIcon();
                    if (drawable != null) {
                        drawable = DrawableCompat.wrap(drawable).mutate();
                        DrawableCompat.setTint(drawable, color);
                        item.setIcon(drawable);
                    }
                }
            });
            colorAnimation.start();

            // Move indicator
            updateIndicatorPosition(item);

            // Reset other icons
            for (int i = 0; i < getMenu().size(); i++) {
                MenuItem menuItem = getMenu().getItem(i);
                if (menuItem.getItemId() != item.getItemId()) {
                    View otherIcon = findViewById(menuItem.getItemId());
                    if (otherIcon != null) {
                        otherIcon.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(ANIMATION_DURATION)
                                .setInterpolator(new DecelerateInterpolator())
                                .start();
                        Drawable drawable = menuItem.getIcon();
                        if (drawable != null) {
                            drawable = DrawableCompat.wrap(drawable).mutate();
                            DrawableCompat.setTint(drawable, Color.GRAY);
                            menuItem.setIcon(drawable);
                        }
                    }
                }
            }
        }
    }

    private void updateIndicatorPosition(MenuItem item) {
        if (indicator != null) {
            float itemWidth = getWidth() / getMenu().size();
            float translationX = itemWidth * item.getOrder();

            ViewGroup.LayoutParams params = indicator.getLayoutParams();
            params.width = Math.round(itemWidth);
            indicator.setLayoutParams(params);

            indicator.animate()
                    .translationX(translationX)
                    .setDuration(ANIMATION_DURATION)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
        }
    }
}