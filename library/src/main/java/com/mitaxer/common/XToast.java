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

    private static volatile XToastConfig sGlobalConfig;
    private static final Handler sMainHandler = new Handler(Looper.getMainLooper());
    private static Toast sCurrentToast;

    private final XToastConfig mOverrideConfig = new XToastConfig();
    private CharSequence mText;
    private int mDurationMs = -1;

    private XToast() {}

    // ====================== Global Config ======================

    /**
     * Set global default style. Call once in {@link Application#onCreate()}.
     */
    public static void init(@NonNull XToastConfig config) {
        sGlobalConfig = config;
    }

    /**
     * Convenience: init with a consumer.
     */
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
        make().setText(text).show();
    }

    public static void showLong(@NonNull CharSequence text) {
        make().setText(text).setDuration(Toast.LENGTH_LONG).show();
    }

    /**
     * Show with custom duration in milliseconds.
     */
    public static void show(@NonNull CharSequence text, long durationMs) {
        make().setText(text).setDuration(durationMs).show();
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
     * Set duration in milliseconds.
     * For custom ms, system Toast uses LENGTH_SHORT internally
     * and we cancel after the specified time.
     */
    public XToast setDuration(long durationMs) {
        mDurationMs = (int) durationMs;
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

            int duration = resolveDurationMs();
            boolean hasCustomStyle = hasAnyCustomStyle();

            if (!hasCustomStyle) {
                // Pure system default — no custom view overhead
                sCurrentToast = Toast.makeText(
                        AppHolder.get(), mText,
                        duration == 2000 ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG);
                sCurrentToast.show();
            } else {
                // Build custom styled view
                Toast toast = new Toast(AppHolder.get());
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(buildStyledView());
                toast.show();
                sCurrentToast = toast;

                // Auto-cancel for custom durations
                if (duration != 2000) {
                    sMainHandler.postDelayed(() -> {
                        if (sCurrentToast != null) {
                            sCurrentToast.cancel();
                            sCurrentToast = null;
                        }
                    }, duration);
                }
            }
        });
    }

    // ====================== Internal ======================

    private int resolveDurationMs() {
        if (mDurationMs != -1) {
            return mDurationMs;
        }
        return 2000; // default to LENGTH_SHORT
    }

    private boolean hasAnyCustomStyle() {
        XToastConfig global = sGlobalConfig;
        return (global != null && global.hasAny())
                || mOverrideConfig.hasAny();
    }

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
            float ph = merged.getPaddingHorizontal() > 0 ? merged.getPaddingHorizontal() * density : 24f * density;
            float pv = merged.getPaddingVertical() > 0 ? merged.getPaddingVertical() * density : 12f * density;
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
