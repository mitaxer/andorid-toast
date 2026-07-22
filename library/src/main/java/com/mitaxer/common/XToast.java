package com.mitaxer.common;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

/**
 * Lightweight Toast utility with optional style customization.
 * <p>
 * <b>Usage:</b>
 * <pre>
 * // Quick show
 * XToast.showShort("保存成功");
 * XToast.showLong("加载中...");
 * XToast.show("自定义时长", 5000);
 *
 * // Global style init (call once in Application.onCreate, optional)
 * XToast.init(config -> {
 *     config.setBgColor(0xFF333333)
 *           .setCornerRadius(24)
 *           .setTextColor(Color.WHITE)
 *           .setTextSizeSp(14)
 *           .setMaxWidthDp(300)
 *           .setPaddingDp(24, 12);
 * });
 *
 * // Per-call override (takes precedence over global config)
 * XToast.make()
 *     .setText("操作失败")
 *     .setBgColor(0xFFE53935)
 *     .setDuration(3000)
 *     .show();
 * </pre>
 *
 * <b>Priority:</b> per-call override &gt; global config &gt; system default
 *
 * @author mitaxer
 */
public final class XToast {

    private static final int DEFAULT_TEXT_COLOR = Color.parseColor("#FFFFFF");
    private static final int DEFAULT_BG_COLOR = Color.parseColor("#2B2B2B");
    private static final float DEFAULT_CORNER_RADIUS_DP = 24f;
    private static final int MS_SHORT = 2000;
    private static final int MS_LONG = 3500;

    // Duration type constants
    private static final int DUR_NOT_SET = 0;
    private static final int DUR_SHORT = 1;
    private static final int DUR_LONG = 2;
    private static final int DUR_CUSTOM = 3;

    private static volatile XToastConfig sGlobalConfig;
    private static final Handler sMainHandler = new Handler(Looper.getMainLooper());
    private static Toast sCurrentToast;

    private final XToastConfig mOverrideConfig = new XToastConfig();
    private CharSequence mText;
    private int mDurationType = DUR_NOT_SET;
    private int mCustomDurationMs;

    // gravity
    private boolean mHasGravity;
    private int mGravity;
    private int mGravityX;
    private int mGravityY;

    private XToast() {}

    // ====================== Global Config ======================

    public static void init(@NonNull XToastConfig config) {
        sGlobalConfig = config;
    }

    public static void init(@NonNull Configurator configurator) {
        XToastConfig config = new XToastConfig();
        configurator.configure(config);
        sGlobalConfig = config;
    }

    public interface Configurator {
        void configure(@NonNull XToastConfig config);
    }

    // ====================== Quick Show ======================

    public static void showShort(@NonNull CharSequence text) {
        XToast x = new XToast();
        x.mText = text;
        x.mDurationType = DUR_SHORT;
        x.show();
    }

    public static void showLong(@NonNull CharSequence text) {
        XToast x = new XToast();
        x.mText = text;
        x.mDurationType = DUR_LONG;
        x.show();
    }

    public static void show(@NonNull CharSequence text, long durationMs) {
        XToast x = new XToast();
        x.mText = text;
        x.mDurationType = DUR_CUSTOM;
        x.mCustomDurationMs = (int) durationMs;
        x.show();
    }

    // ====================== Builder ======================

    public static XToast make() {
        return new XToast();
    }

    public XToast setText(@NonNull CharSequence text) {
        mText = text;
        return this;
    }

    /**
     * Set duration in milliseconds. For LENGTH_SHORT / LENGTH_LONG,
     * use {@link #showShort(CharSequence)} / {@link #showLong(CharSequence)}.
     */
    public XToast setDuration(long durationMs) {
        mDurationType = DUR_CUSTOM;
        mCustomDurationMs = (int) durationMs;
        return this;
    }

    /**
     * Set display position.
     */
    public XToast setGravity(int gravity, int xOffset, int yOffset) {
        mHasGravity = true;
        mGravity = gravity;
        mGravityX = xOffset;
        mGravityY = yOffset;
        return this;
    }

    // ====================== Style Override Methods ======================

    public XToast setBgColor(@ColorInt int color) {
        mOverrideConfig.setBgColor(color);
        return this;
    }

    public XToast setCornerRadius(float radiusDp) {
        mOverrideConfig.setCornerRadius(radiusDp);
        return this;
    }

    public XToast setTextColor(@ColorInt int color) {
        mOverrideConfig.setTextColor(color);
        return this;
    }

    public XToast setTextSizeSp(float sizeSp) {
        mOverrideConfig.setTextSizeSp(sizeSp);
        return this;
    }

    public XToast setMaxWidthDp(float maxWidthDp) {
        mOverrideConfig.setMaxWidthDp(maxWidthDp);
        return this;
    }

    public XToast setPaddingDp(float left, float top, float right, float bottom) {
        mOverrideConfig.setPaddingDp(left, top, right, bottom);
        return this;
    }

    public XToast setPaddingDp(float horizontal, float vertical) {
        mOverrideConfig.setPaddingDp(horizontal, vertical);
        return this;
    }

    // ====================== Internal Duration Helpers ======================

    /** Returns system Toast constant: LENGTH_SHORT or LENGTH_LONG */
    private int resolveSysDuration() {
        if (mDurationType == DUR_LONG) {
            return Toast.LENGTH_LONG;
        }
        return Toast.LENGTH_SHORT;
    }

    /** Returns actual milliseconds for timing logic */
    private int resolveDurationMs() {
        switch (mDurationType) {
            case DUR_LONG:
                return MS_LONG;
            case DUR_CUSTOM:
                return mCustomDurationMs > 0 ? mCustomDurationMs : MS_SHORT;
            default: // DUR_NOT_SET, DUR_SHORT
                return MS_SHORT;
        }
    }

    /**
     * Returns the effective Toast duration to use when building a Toast object.<br>
     * For short/long: use the system constant. For custom ms: use LENGTH_SHORT
     * and rely on our own delayed cancel.
     */
    private int resolveToastDuration() {
        if (mDurationType == DUR_CUSTOM) {
            return Toast.LENGTH_SHORT;
        }
        return resolveSysDuration();
    }

    // ====================== Show ======================

    public void show() {
        if (mText == null || mText.length() == 0) return;

        sMainHandler.post(() -> {
            // Cancel previous Toast
            if (sCurrentToast != null) {
                sCurrentToast.cancel();
                sMainHandler.removeCallbacksAndMessages(null);
                sCurrentToast = null;
            }

            boolean hasCustomStyle = hasAnyCustomStyle();

            if (!hasCustomStyle) {
                // Pure system default
                sCurrentToast = Toast.makeText(
                        AppHolder.get(), mText, resolveSysDuration());
                applyGravity(sCurrentToast);
                sCurrentToast.show();
            } else {
                // Custom styled view
                Toast toast = new Toast(AppHolder.get());
                toast.setDuration(resolveToastDuration());
                applyGravity(toast);
                toast.setView(buildStyledView());
                toast.show();
                sCurrentToast = toast;

                // Delayed cancel only for custom durations
                if (mDurationType == DUR_CUSTOM) {
                    scheduleCancel(mCustomDurationMs > 0 ? mCustomDurationMs : MS_SHORT);
                } else if (mDurationType == DUR_LONG) {
                    scheduleCancel(MS_LONG);
                }
            }
        });
    }

    private void applyGravity(Toast toast) {
        if (mHasGravity) {
            toast.setGravity(mGravity, mGravityX, mGravityY);
        }
    }

    private void scheduleCancel(int delayMs) {
        sMainHandler.postDelayed(() -> {
            if (sCurrentToast != null) {
                sCurrentToast.cancel();
                sCurrentToast = null;
            }
        }, delayMs);
    }

    // ====================== Style Checks ======================

    private boolean hasAnyCustomStyle() {
        XToastConfig global = sGlobalConfig;
        return (global != null && global.hasAny())
                || mOverrideConfig.hasAny();
    }

    // ====================== View Building ======================

    private View buildStyledView() {
        XToastConfig merged = mergeConfig();

        float density = AppHolder.get().getResources().getDisplayMetrics().density;

        // Background drawable
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(merged.getBgColor(DEFAULT_BG_COLOR));
        bg.setCornerRadius(merged.getCornerRadius(DEFAULT_CORNER_RADIUS_DP) * density);

        // Text view
        TextView tv = new TextView(AppHolder.get());
        tv.setText(mText);
        tv.setTextColor(merged.getTextColor(DEFAULT_TEXT_COLOR));
        if (merged.getTextSizeSp() > 0) {
            tv.setTextSize(merged.getTextSizeSp());
        }
        tv.setGravity(Gravity.CENTER);

        // Padding
        if (merged.getPaddingHorizontal() > 0 || merged.getPaddingVertical() > 0) {
            float ph = merged.getPaddingHorizontal() > 0
                    ? merged.getPaddingHorizontal() * density : 24f * density;
            float pv = merged.getPaddingVertical() > 0
                    ? merged.getPaddingVertical() * density : 12f * density;
            tv.setPadding((int) ph, (int) pv, (int) ph, (int) pv);
        }

        // Max width
        if (merged.getMaxWidthDp() > 0) {
            tv.setMaxWidth((int) (merged.getMaxWidthDp() * density));
        }

        // Container
        FrameLayout container = new FrameLayout(AppHolder.get());
        container.setBackground(bg);
        container.addView(tv, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT));

        return container;
    }

    // ====================== Config Merge ======================

    private XToastConfig mergeConfig() {
        XToastConfig global = sGlobalConfig;
        if (global == null) {
            return mOverrideConfig;
        }
        XToastConfig result = new XToastConfig();
        result.setBgColor(mOverrideConfig.hasBgColor()
                ? mOverrideConfig.getBgColor() : global.getBgColor());
        result.setCornerRadius(mOverrideConfig.hasCornerRadius()
                ? mOverrideConfig.getCornerRadius() : global.getCornerRadius());
        result.setTextColor(mOverrideConfig.hasTextColor()
                ? mOverrideConfig.getTextColor() : global.getTextColor());
        result.setTextSizeSp(mOverrideConfig.hasTextSizeSp()
                ? mOverrideConfig.getTextSizeSp() : global.getTextSizeSp());
        result.setMaxWidthDp(mOverrideConfig.hasMaxWidthDp()
                ? mOverrideConfig.getMaxWidthDp() : global.getMaxWidthDp());
        result.setPadding(
                mOverrideConfig.hasPadding() ? mOverrideConfig : global);
        return result;
    }
}
