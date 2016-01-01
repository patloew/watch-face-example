/* Watch face drawing code is from Android Studio examples. License:
 *
 * Copyright (C) 2014 The Android Open Source Project
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
 */

package com.patloew.commons;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.Calendar;

public class WatchFaceDrawer {
    private boolean mIsMobilePreview = false;

    private float mHourOuterOffset;
    private float mMinuteOuterOffset;
    private float mSecondOuterOffset;

    private float mPreviewSquareRadius;

    private Paint mBackgroundPaint;
    private Paint mPreviewBorderPaint;
    private Paint mSecondHandPaint;
    private Paint mMinuteHandPaint;
    private Paint mHourHandPaint;

    // put your resources here (Paint objects, dimensions, colors, …)

    public WatchFaceDrawer(Context context) {
        Resources res = context.getResources();

        // initialize your resources

        mHourOuterOffset = res.getDimension(R.dimen.hour_outer_offset);
        mMinuteOuterOffset = res.getDimension(R.dimen.minute_outer_offset);
        mSecondOuterOffset = res.getDimension(R.dimen.second_outer_offset);

        mPreviewSquareRadius = res.getDimension(R.dimen.watchface_preview_square_radius);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(res.getColor(R.color.watchface_background));

        mSecondHandPaint = new Paint();
        mSecondHandPaint.setColor(res.getColor(R.color.second_hand));
        mSecondHandPaint.setStrokeWidth(res.getDimension(R.dimen.seconds_hand_stroke));
        mSecondHandPaint.setAntiAlias(true);
        mSecondHandPaint.setStrokeCap(Paint.Cap.ROUND);

        mMinuteHandPaint = new Paint(mSecondHandPaint);
        mMinuteHandPaint.setColor(res.getColor(R.color.minute_hand));
        mMinuteHandPaint.setStrokeWidth(res.getDimension(R.dimen.minute_hand_stroke));

        mHourHandPaint = new Paint(mSecondHandPaint);
        mHourHandPaint.setColor(res.getColor(R.color.hour_hand));
        mHourHandPaint.setStrokeWidth(res.getDimension(R.dimen.hour_hand_stroke));
    }

    public void setMobilePreview(Context context, boolean isMobilePreview) {
        Resources res = context.getResources();

        // here you can change specific resources, based on whether the watch face
        // is drawn in the phone app or on the watch

        mIsMobilePreview = isMobilePreview;
        mBackgroundPaint.setAntiAlias(isMobilePreview ? true : false);

        if(mIsMobilePreview && mPreviewBorderPaint == null) {
            mPreviewBorderPaint = new Paint();
            mPreviewBorderPaint.setColor(res.getColor(R.color.watchface_preview_border));
            mPreviewBorderPaint.setAntiAlias(true);
        }
    }

    public void onAmbientModeChanged(Context context, IWatchFaceConfig config) {
        if(config.isLowBitAmbient()) {
            Resources res = context.getResources();

            final boolean inAmbientMode = config.isAmbient();

            mSecondHandPaint.setAntiAlias(!inAmbientMode);
            mMinuteHandPaint.setAntiAlias(!inAmbientMode);
            mHourHandPaint.setAntiAlias(!inAmbientMode);

            mSecondHandPaint.setColor(res.getColor(inAmbientMode ? R.color.low_bit_ambient_hand : R.color.second_hand));
            mMinuteHandPaint.setColor(res.getColor(inAmbientMode ? R.color.low_bit_ambient_hand : R.color.minute_hand));
            mHourHandPaint.setColor(res.getColor(inAmbientMode ? R.color.low_bit_ambient_hand : R.color.hour_hand));
        }
    }

    public void onDraw(Context context, IWatchFaceConfig config, Canvas canvas, Rect bounds) {
        final Calendar calendar = config.getCalendar();
        final boolean isAmbient = config.isAmbient();
        final boolean isRound = config.isRound();
        final boolean useLightTheme = !isAmbient && config.isLightTheme();

        mBackgroundPaint.setColor(context.getResources().getColor(useLightTheme ? R.color.watchface_background_light : R.color.watchface_background));

        /////////////////////////////////////////////////////////////////////
        // Draw your watch face here, using the provided canvas and bounds //
        /////////////////////////////////////////////////////////////////////

        final int width = bounds.width();
        final int height = bounds.height();

        // Find the center. Ignore the window insets so that, on round
        // watches with a "chin", the watch face is centered on the entire
        // screen, not just the usable portion.
        float centerX = width / 2f;
        float centerY = height / 2f;


        // Draw the background.
        if(mIsMobilePreview) {
            if(isRound) {
                canvas.drawCircle(centerX, centerY, centerX, mPreviewBorderPaint);
            } else {
                float radius = mPreviewSquareRadius;
                RectF rectF = new RectF(0, 0, canvas.getWidth(), canvas.getHeight());
                canvas.drawRoundRect(rectF, radius, radius, mPreviewBorderPaint);
            }

            float translateXY = width * 0.05f;
            canvas.translate(translateXY, translateXY);
            canvas.scale(0.9f, 0.9f);

            if(isRound) {
                canvas.drawCircle(centerX, centerY, centerX, mBackgroundPaint);
            } else {
                canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mBackgroundPaint);
            }
        } else {
            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mBackgroundPaint);
        }

        float secRot = calendar.get(Calendar.SECOND) / 30f * (float) Math.PI;
        final int minutes = calendar.get(Calendar.MINUTE);
        float minRot = minutes / 30f * (float) Math.PI;
        float hrRot = ((calendar.get(Calendar.HOUR) + (minutes / 60f)) / 6f) *
                (float) Math.PI;

        float secLength = centerX - mSecondOuterOffset;
        float minLength = centerX - mMinuteOuterOffset;
        float hrLength = centerX - mHourOuterOffset;

        if (!isAmbient) {
            float secX = (float) Math.sin(secRot) * secLength;
            float secY = (float) -Math.cos(secRot) * secLength;
            canvas.drawLine(centerX, centerY, centerX + secX, centerY + secY, mSecondHandPaint);
        }

        float minX = (float) Math.sin(minRot) * minLength;
        float minY = (float) -Math.cos(minRot) * minLength;
        canvas.drawLine(centerX, centerY, centerX + minX, centerY + minY, mMinuteHandPaint);

        float hrX = (float) Math.sin(hrRot) * hrLength;
        float hrY = (float) -Math.cos(hrRot) * hrLength;
        canvas.drawLine(centerX, centerY, centerX + hrX, centerY + hrY, mHourHandPaint);
    }

}

