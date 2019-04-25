/*
 * Copyright (C) 2017 Buglife, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.buglife.sdk.floatintinterface;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.LinearLayout;
import com.buglife.sdk.R;
import com.buglife.sdk.ViewUtils;

public class FloatingButton extends LinearLayout {


    private final Paint mRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF mRingBounds = new RectF();
//    private final AnimatorSet mInAnimator = new AnimatorSet();
//    private final AnimatorSet mOutAnimator = new AnimatorSet();
    private WindowManager mWindowManager;
    private FloatingButtonMovementHandler mMovementHandler;

    private ValueAnimator mRingAnimator;
    private float mCurrentRingAngle = 360;

    public FloatingButton(Context context) {
        this(context, null);

    }

    public FloatingButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        int ringColor = Color.parseColor("#66FFFFFF");
        mRingPaint.setColor(ringColor);
        mRingPaint.setStyle(Paint.Style.STROKE);
        mRingPaint.setStrokeWidth(ViewUtils.dpToPx(3, getResources()));
        mRingAnimator = ValueAnimator.ofFloat(360, 0);
        mRingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentRingAngle = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

        setBackgroundResource(R.drawable.bg_circle);
        int backgroundColor = Color.parseColor("#cc1890FF");
        ViewCompat.setBackgroundTintList(this, ColorStateList.valueOf(backgroundColor));
        ViewCompat.setElevation(this, ViewUtils.dpToPx(4, getResources()));


        mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        mMovementHandler = new FloatingButtonMovementHandler(this, mWindowManager);
    }

    @Override public boolean onTouchEvent(MotionEvent event) {
        boolean handled = mMovementHandler.onTouchEvent(event);
        return handled || super.onTouchEvent(event);
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = (int) ViewUtils.dpToPx(52, getResources());
        setMeasuredDimension(size, size);
    }

    @Override protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        int leftMovementBound = -(getMeasuredWidth() / 2);
        int topMovementBound = 0;
        int rightMovementBound = dm.widthPixels - (getMeasuredWidth() / 2);
        int bottomMovementBound = dm.heightPixels - ViewUtils.navigationBarHeight(getResources()) - ViewUtils.statusBarHeight(getResources());
        mMovementHandler.setBounds(leftMovementBound, topMovementBound, rightMovementBound, bottomMovementBound);
    }

    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override public void onGlobalLayout() {
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
                DisplayMetrics dm = getResources().getDisplayMetrics();
                WindowManager.LayoutParams params = (WindowManager.LayoutParams) getLayoutParams();
                params.x = (dm.widthPixels / 2) - (getMeasuredWidth() / 2);
                params.y = (dm.heightPixels / 2) - (getMeasuredHeight() / 2);
                mWindowManager.updateViewLayout(FloatingButton.this, params);

            }
        });
    }

    @Override protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mMovementHandler.recycle();
    }


    public interface HideCallback {
        void onViewHidden();
    }
}
