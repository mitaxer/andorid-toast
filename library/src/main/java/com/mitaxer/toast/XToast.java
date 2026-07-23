package com.mitaxer.toast;

import android.app.Application;
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
 * 轻量级 Toast 工具类，支持样式定制。
 * 基于系统 Toast 实现，零额外权限，不依赖第三方库。
 * 支持全局默认样式配置 + 单次调用临时覆盖（属性级合并）。
 *
 * 优先级：单次覆盖 > 全局配置 > 系统默认样式
 *
 * @author mitaxer
 */
public final class XToast {

    /** 预设主题模式 */
    public enum Mode {
        /** 浅色：近白底 + 深灰字 */
        LIGHT,
        /** 深色：深灰底 + 白字 */
        DARK
    }

    private static final int DEFAULT_TEXT_COLOR = Color.parseColor("#FFFFFF");
    private static final int DEFAULT_BG_COLOR = Color.parseColor("#2B2B2B");
    private static final float DEFAULT_CORNER_RADIUS_DP = 24f;
    private static final int MS_SHORT = 2000;
    private static final int MS_LONG = 3500;

    // 时长类型（避免与系统 LENGTH_SHORT=0 / LENGTH_LONG=1 混淆）
    private static final int DUR_NOT_SET = 0;
    private static final int DUR_SHORT = 1;
    private static final int DUR_LONG = 2;
    private static final int DUR_CUSTOM = 3;

    private static XToastConfig sLightPreset;
    private static XToastConfig sDarkPreset;
    private static volatile XToastConfig sActiveConfig;
    private static final Handler sMainHandler = new Handler(Looper.getMainLooper());
    private static Toast sCurrentToast;

    private final XToastConfig mOverrideConfig = new XToastConfig();
    private CharSequence mText;
    private int mDurationType = DUR_NOT_SET;
    private int mCustomDurationMs;

    private boolean mHasGravity;
    private int mGravity;
    private int mGravityX;
    private int mGravityY;

    private XToast() {}

    // ==================== 全局配置 ====================

    static {
        sLightPreset = new XToastConfig();
        sLightPreset.setMode(Mode.LIGHT);
        sDarkPreset = new XToastConfig();
        sDarkPreset.setMode(Mode.DARK);
        sActiveConfig = sLightPreset;
    }

    /** 设置自定义配色。不影响 LIGHT/DARK 预设。 */
    public static void init(@NonNull XToastConfig config) {
        sActiveConfig = config;
    }

    /** 设置自定义配色（Lambda 方式）。不影响 LIGHT/DARK 预设。 */
    public static void init(@NonNull Configurator configurator) {
        XToastConfig config = new XToastConfig();
        configurator.configure(config);
        sActiveConfig = config;
    }

    /** 同时配置白天/夜间预设颜色（在 Application 初始化时调用）。不调则用默认值。 */
    public static void init(@NonNull Configurator light, @NonNull Configurator dark) {
        sLightPreset = new XToastConfig();
        light.configure(sLightPreset);
        sDarkPreset = new XToastConfig();
        dark.configure(sDarkPreset);
        sActiveConfig = sLightPreset;
    }

    /** 切换到 LIGHT 预设（出厂默认 或 init(light,dark) 配置的值）。 */
    public static void useLightMode() {
        sActiveConfig = sLightPreset;
    }

    /** 切换到 DARK 预设（出厂默认 或 init(light,dark) 配置的值）。 */
    public static void useDarkMode() {
        sActiveConfig = sDarkPreset;
    }

    public interface Configurator {
        void configure(@NonNull XToastConfig config);
    }

    // ==================== 快速调用 ====================

    /** 显示短 Toast（约 2s）。text 为 null 或空字符串时静默忽略。 */
    public static void showShort(CharSequence text) {
        show(text, DUR_SHORT, 0);
    }

    /** 显示默认时长的 Toast，等同于 showShort。 */
    public static void show(CharSequence text) {
        showShort(text);
    }

    /** 显示长 Toast（约 3.5s）。text 为 null 或空字符串时静默忽略。 */
    public static void showLong(CharSequence text) {
        show(text, DUR_LONG, 0);
    }

    /** 显示自定义时长的 Toast（毫秒）。text 为 null 或空字符串时静默忽略。 */
    public static void show(CharSequence text, long durationMs) {
        show(text, DUR_CUSTOM, (int) durationMs);
    }

    private static void show(CharSequence text, int durationType, int customDurationMs) {
        if (text == null || text.length() == 0) return;
        XToast x = new XToast();
        x.mText = text;
        x.mDurationType = durationType;
        x.mCustomDurationMs = customDurationMs;
        x.show();
    }

    // ==================== Builder ====================

    /** 创建 Builder 实例，用于单次定制样式。 */
    public static XToast make() {
        return new XToast();
    }

    /** 设置待显示的文本。传 null 或空字符串时 show() 不会显示。 */
    public XToast setText(CharSequence text) {
        mText = text;
        return this;
    }

    /**
     * 设置自定义显示时长（毫秒）。
     * 系统 Toast 仅支持 LENGTH_SHORT/LENGTH_LONG 常量，
     * 自定义毫秒使用 LENGTH_SHORT 显示，到期后主动取消。
     */
    public XToast setDuration(long durationMs) {
        mDurationType = DUR_CUSTOM;
        mCustomDurationMs = (int) durationMs;
        return this;
    }

    /** 设置显示位置。 */
    public XToast setGravity(int gravity, int xOffset, int yOffset) {
        mHasGravity = true;
        mGravity = gravity;
        mGravityX = xOffset;
        mGravityY = yOffset;
        return this;
    }

    // ==================== 样式覆盖 ====================

    public XToast setBgColor(@ColorInt int color) {
        mOverrideConfig.setBgColor(color);
        return this;
    }

    public XToast setBgColor(@NonNull String hexColor) {
        mOverrideConfig.setBgColor(hexColor);
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

    public XToast setTextColor(@NonNull String hexColor) {
        mOverrideConfig.setTextColor(hexColor);
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

    // ==================== 内部：时长解析 ====================

    /** 返回系统时长常量（LENGTH_SHORT / LENGTH_LONG）。 */
    private int resolveSysDuration() {
        return mDurationType == DUR_LONG ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
    }

    /** 返回实际毫秒数，用于延时取消。 */
    private int resolveDurationMs() {
        if (mDurationType == DUR_LONG) return MS_LONG;
        if (mDurationType == DUR_CUSTOM) return mCustomDurationMs > 0 ? mCustomDurationMs : MS_SHORT;
        return MS_SHORT;
    }

    /**
     * 构建 Toast 对象时的时长：自定义毫秒用 LENGTH_SHORT，到期后主动取消。
     * SHORT/LONG 直接用系统常量。
     */
    private int resolveToastDuration() {
        return mDurationType == DUR_CUSTOM ? Toast.LENGTH_SHORT : resolveSysDuration();
    }

    // ==================== 显示 ====================

    /** 显示 Toast。同一时刻只有一个，新的会取消旧的。 */
    public void show() {
        if (mText == null || mText.length() == 0) return;

        sMainHandler.post(() -> {
            Application app = AppHolder.get();

            // 取消上一个
            cancelCurrent();

            if (!hasAnyCustomStyle()) {
                showSystemToast(app);
            } else {
                try {
                    showCustomToast(app);
                } catch (Exception e) {
                    // 自定义样式失败，降级为系统默认样式
                    showSystemToast(app);
                }
            }
        });
    }

    private void cancelCurrent() {
        if (sCurrentToast != null) {
            sCurrentToast.cancel();
            sMainHandler.removeCallbacksAndMessages(null);
            sCurrentToast = null;
        }
    }

    private void showSystemToast(Application app) {
        sCurrentToast = Toast.makeText(app, mText, resolveSysDuration());
        applyGravity(sCurrentToast);
        sCurrentToast.show();
        scheduleCleanup(resolveDurationMs());
    }

    private void showCustomToast(Application app) {
        Toast toast = new Toast(app);
        toast.setDuration(resolveToastDuration());
        applyGravity(toast);
        toast.setView(buildStyledView());
        toast.show();
        sCurrentToast = toast;
        scheduleCleanup(resolveDurationMs());
    }

    /** Toast 显示结束后清除静态引用，避免内存泄漏。 */
    private void scheduleCleanup(int delayMs) {
        sMainHandler.postDelayed(() -> {
            if (sCurrentToast != null) {
                sCurrentToast.cancel();
                sCurrentToast = null;
            }
        }, delayMs);
    }

    private void applyGravity(Toast toast) {
        if (mHasGravity) {
            toast.setGravity(mGravity, mGravityX, mGravityY);
        }
    }

    // ==================== 样式检测 ====================

    /** 判断是否有自定义样式（全局 + 单次覆盖），都没有则走系统原生路径。 */
    private boolean hasAnyCustomStyle() {
        return (sActiveConfig != null && sActiveConfig.hasAny()) || mOverrideConfig.hasAny();
    }

    // ==================== 自定义 View 构建 ====================

    /** 根据合并后的配置构建自定义 Toast View（圆角背景、文字样式、边距、最大宽度）。 */
    private View buildStyledView() {
        XToastConfig merged = mergeConfig();
        float density = AppHolder.get().getResources().getDisplayMetrics().density;

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(merged.getBgColor(DEFAULT_BG_COLOR));
        bg.setCornerRadius(merged.getCornerRadius(DEFAULT_CORNER_RADIUS_DP) * density);

        TextView tv = new TextView(AppHolder.get());
        tv.setText(mText);
        tv.setTextColor(merged.getTextColor(DEFAULT_TEXT_COLOR));
        if (merged.getTextSizeSp() > 0) {
            tv.setTextSize(merged.getTextSizeSp());
        }
        tv.setGravity(Gravity.CENTER);

        float ph = merged.getPaddingHorizontal() > 0 ? merged.getPaddingHorizontal() * density : 24f * density;
        float pv = merged.getPaddingVertical() > 0 ? merged.getPaddingVertical() * density : 12f * density;
        tv.setPadding((int) ph, (int) pv, (int) ph, (int) pv);

        if (merged.getMaxWidthDp() > 0) {
            tv.setMaxWidth((int) (merged.getMaxWidthDp() * density));
        }

        FrameLayout container = new FrameLayout(AppHolder.get());
        container.setBackground(bg);
        container.addView(tv, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT));
        return container;
    }

    // ==================== 配置合并 ====================

    /** 合并全局和单次配置：覆盖属性优先，未设置的回退到全局。 */
    private XToastConfig mergeConfig() {
        XToastConfig active = sActiveConfig;
        if (active == null) return mOverrideConfig;

        XToastConfig result = new XToastConfig();
        if (mOverrideConfig.hasBgColor()) {
            result.setBgColor(mOverrideConfig.getBgColor());
        } else if (active.hasBgColor()) {
            result.setBgColor(active.getBgColor());
        }
        if (mOverrideConfig.hasCornerRadius()) {
            result.setCornerRadius(mOverrideConfig.getCornerRadius());
        } else if (active.hasCornerRadius()) {
            result.setCornerRadius(active.getCornerRadius());
        }
        if (mOverrideConfig.hasTextColor()) {
            result.setTextColor(mOverrideConfig.getTextColor());
        } else if (active.hasTextColor()) {
            result.setTextColor(active.getTextColor());
        }
        if (mOverrideConfig.hasTextSizeSp()) {
            result.setTextSizeSp(mOverrideConfig.getTextSizeSp());
        } else if (active.hasTextSizeSp()) {
            result.setTextSizeSp(active.getTextSizeSp());
        }
        if (mOverrideConfig.hasMaxWidthDp()) {
            result.setMaxWidthDp(mOverrideConfig.getMaxWidthDp());
        } else if (active.hasMaxWidthDp()) {
            result.setMaxWidthDp(active.getMaxWidthDp());
        }
        if (mOverrideConfig.hasPadding()) {
            result.setPadding(mOverrideConfig);
        } else if (active.hasPadding()) {
            result.setPadding(active);
        }
        return result;
    }
}
