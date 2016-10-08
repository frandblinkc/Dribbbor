package com.tangyang.fribbble.utils.custom_behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.util.AttributeSet;
import android.view.View;

import com.tangyang.fribbble.utils.AnimatorUtils;

/**
 * Created by YangTang on 10/7/2016.
 */
public class ScrollUpShowBehavior extends FloatingActionButton.Behavior {

    private boolean isAnimatingOut;

    private ViewPropertyAnimatorListener viewPropertyAnimatorListener;

    public ScrollUpShowBehavior(Context context, AttributeSet attrs) {
        super();
        this.isAnimatingOut = false;
        this.viewPropertyAnimatorListener = new ViewPropertyAnimatorListener() {
            @Override
            public void onAnimationStart(View view) {
                isAnimatingOut = true;
            }

            @Override
            public void onAnimationEnd(View view) {
                isAnimatingOut = false;
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(View view) {
                isAnimatingOut = false;
            }
        };
    }




    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout,
                                       FloatingActionButton child,
                                       View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child,
                               View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        // scroll down, hide fab
        if (((dyConsumed > 0 && dyUnconsumed == 0) || (dyConsumed == 0 && dyUnconsumed > 0)) && child.getVisibility() != View.VISIBLE) {
            AnimatorUtils.scaleShow(child, null);
        }

        // scroll up, show fab
        if (((dyConsumed < 0 && dyUnconsumed == 0) || (dyConsumed == 0 && dyUnconsumed < 0)) && child.getVisibility() != View.GONE && !isAnimatingOut) {
            AnimatorUtils.scaleHide(child, viewPropertyAnimatorListener);
        }
    }



}
