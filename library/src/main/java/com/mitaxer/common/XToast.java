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
 * 轻量级 Toast 工具类，支持样式定制。
 * <p>
 * 基于系统 Toast 实现，零额外权限，不依赖第三方库。
 * 支持全局默认样式配置 + 单次调用临时覆盖（属性级合并）。
 * <p>
 * <b>快速调用：</b>
 * <pre>
 * XToast.showShort("保存成功");         // 约 2s
 * XToast.showLong("加载中...");         // 约 3.5s
 * XToast.show("自定义时长", 5000);      // 自定义毫秒
 * </pre>
 * <p>
 * <b>全局样式（可选，在 Application.onCreate 中调用一次）：</b>
 * <pre>
 * XToast.init(config -> {
 *     config.setBgColor(0xFF333333)
 *           .setCornerRadius(24)
 *           .setTextColor(Color.WHITE)
 *           .setTextSizeSp(14)
 *           .setMaxWidthDp(300)
 *           .setPaddingDp(24, 12);
 * });
 * </pre>
 * <p>
 * <b>单次覆盖（优先级高于全局配置）：</b>
 * <pre>
 * XToast.make()
 *     .setText("操作失败")
 *     .setBgColor(0xFFE53935)
 *     .setDuration(3000)
 *     .show();
 * </pre>
 * <p>
 * <b>优先级：</b>单次覆盖 &gt; 全局配置 &gt; 系统默认样式
 * <p>
 * 每个属性独立合并——未设置的属性会回退到全局配置，全局未设置的则使用系统默认。
 *
 * @author mitaxer
 */
public final class XToast {

    /** 默认文字颜色：白色 */
    private static final int DEFAULT_TEXT_COLOR = Color.parseColor("#FFFFFF");
    /** 默认背景颜色：深灰 */
    private static final int DEFAULT_BG_COLOR = Color.parseColor("#2B2B2B");
    /** 默认圆角半径（dp） */
    private static final float DEFAULT_CORNER_RADIUS_DP = 24f;
    /** SHORT 对应显示时长（毫秒） */
    private static final int MS_SHORT = 2000;
    /** LONG 对应显示时长（毫秒） */
    private static final int MS_LONG = 3500;

    // 时长类型常量（避免与系统 Toast.LENGTH_SHORT=0 / LENGTH_LONG=1 混淆）
    private static final int DUR_NOT_SET = 0;
    private static final int DUR_SHORT = 1;
    private static final int DUR_LONG = 2;
    private static final int DUR_CUSTOM = 3;

    /** 全局默认样式配置 */
    private static volatile XToastConfig sGlobalConfig;
    /** 主线程 Handler，用于切线程和延迟取消 */
    private static final Handler sMainHandler = new Handler(Looper.getMainLooper());
    /** 当前正在显示的 Toast，用于互斥取消 */
    private static Toast sCurrentToast;

    /** 单次调用的样式覆盖配置 */
    private final XToastConfig mOverrideConfig = new XToastConfig();
    /** 待显示的文本 */
    private CharSequence mText;
    /** 时长类型 */
    private int mDurationType = DUR_NOT_SET;
    /** 自定义时长（毫秒），仅在 mDurationType == DUR_CUSTOM 时有效 */
    private int mCustomDurationMs;

    /** 是否设置了显示位置 */
    private boolean mHasGravity;
    /** Toast 显示位置 */
    private int mGravity;
    /** X 轴偏移（像素） */
    private int mGravityX;
    /** Y 轴偏移（像素） */
    private int mGravityY;

    private XToast() {}

    // ====================== 全局配置 ======================

    /**
     * 设置全局默认样式。在 {@link android.app.Application#onCreate()} 中调用一次即可。
     *
     * @param config 全局样式配置
     */
    public static void init(@NonNull XToastConfig config) {
        sGlobalConfig = config;
    }

    /**
     * 设置全局默认样式（Lambda 方式）。
     *
     * @param configurator 配置回调，接收 {@link XToastConfig} 进行设置
     */
    public static void init(@NonNull Configurator configurator) {
        XToastConfig config = new XToastConfig();
        configurator.configure(config);
        sGlobalConfig = config;
    }

    /**
     * 全局配置回调接口。
     */
    public interface Configurator {
        /**
         * 配置全局 Toast 样式。
         *
         * @param config 配置对象
         */
        void configure(@NonNull XToastConfig config);
    }

    // ====================== 快速调用 ======================

    /**
     * 显示短 Toast（约 2s）。
     *
     * @param text 显示的文本
     */
    public static void showShort(@NonNull CharSequence text) {
        XToast x = new XToast();
        x.mText = text;
        x.mDurationType = DUR_SHORT;
        x.show();
    }

    /**
     * 显示长 Toast（约 3.5s）。
     *
     * @param text 显示的文本
     */
    public static void showLong(@NonNull CharSequence text) {
        XToast x = new XToast();
        x.mText = text;
        x.mDurationType = DUR_LONG;
        x.show();
    }

    /**
     * 显示自定义时长的 Toast。
     *
     * @param text      显示的文本
     * @param durationMs 显示时长（毫秒）
     */
    public static void show(@NonNull CharSequence text, long durationMs) {
        XToast x = new XToast();
        x.mText = text;
        x.mDurationType = DUR_CUSTOM;
        x.mCustomDurationMs = (int) durationMs;
        x.show();
    }

    // ====================== Builder ======================

    /**
     * 创建 Builder 实例，用于单次调用定制样式。
     * <pre>
     * XToast.make()
     *     .setText("操作失败")
     *     .setBgColor(0xFFE53935)
     *     .setDuration(3000)
     *     .show();
     * </pre>
     *
     * @return XToast 实例
     */
    public static XToast make() {
        return new XToast();
    }

    /**
     * 设置待显示的文本。
     *
     * @param text 文本
     * @return 当前实例
     */
    public XToast setText(@NonNull CharSequence text) {
        mText = text;
        return this;
    }

    /**
     * 设置自定义显示时长。
     * <p>
     * 注意：系统 Toast 仅支持 LENGTH_SHORT 和 LENGTH_LONG 两个常量。
     * 自定义毫秒时长使用 LENGTH_SHORT 内部显示，到期后主动取消。
     * 如需使用 SHORT/LONG 系统常量，请使用 {@link #showShort(CharSequence)} / {@link #showLong(CharSequence)}。
     *
     * @param durationMs 显示时长（毫秒）
     * @return 当前实例
     */
    public XToast setDuration(long durationMs) {
        mDurationType = DUR_CUSTOM;
        mCustomDurationMs = (int) durationMs;
        return this;
    }

    /**
     * 设置 Toast 显示位置。
     *
     * @param gravity  Gravity 常量，如 {@link Gravity#TOP} | {@link Gravity#CENTER_HORIZONTAL}
     * @param xOffset  X 轴偏移（像素），相对于 gravity 指定的位置
     * @param yOffset  Y 轴偏移（像素），相对于 gravity 指定的位置
     * @return 当前实例
     */
    public XToast setGravity(int gravity, int xOffset, int yOffset) {
        mHasGravity = true;
        mGravity = gravity;
        mGravityX = xOffset;
        mGravityY = yOffset;
        return this;
    }

    // ====================== 样式覆盖 ======================

    /**
     * 覆盖背景颜色（单次有效）。
     *
     * @param color 颜色值，如 {@code 0xFFE53935}
     * @return 当前实例
     */
    public XToast setBgColor(@ColorInt int color) {
        mOverrideConfig.setBgColor(color);
        return this;
    }

    /**
     * 覆盖圆角半径（单次有效）。
     *
     * @param radiusDp 圆角半径（dp）
     * @return 当前实例
     */
    public XToast setCornerRadius(float radiusDp) {
        mOverrideConfig.setCornerRadius(radiusDp);
        return this;
    }

    /**
     * 覆盖文字颜色（单次有效）。
     *
     * @param color 文字颜色
     * @return 当前实例
     */
    public XToast setTextColor(@ColorInt int color) {
        mOverrideConfig.setTextColor(color);
        return this;
    }

    /**
     * 覆盖文字大小（单次有效）。
     *
     * @param sizeSp 字号（sp）
     * @return 当前实例
     */
    public XToast setTextSizeSp(float sizeSp) {
        mOverrideConfig.setTextSizeSp(sizeSp);
        return this;
    }

    /**
     * 覆盖最大宽度（单次有效）。
     *
     * @param maxWidthDp 最大宽度（dp）
     * @return 当前实例
     */
    public XToast setMaxWidthDp(float maxWidthDp) {
        mOverrideConfig.setMaxWidthDp(maxWidthDp);
        return this;
    }

    /**
     * 覆盖内边距（单次有效）。
     *
     * @param left  左边距（dp）
     * @param top   上边距（dp）
     * @param right 右边距（dp）
     * @param bottom 下边距（dp）
     * @return 当前实例
     */
    public XToast setPaddingDp(float left, float top, float right, float bottom) {
        mOverrideConfig.setPaddingDp(left, top, right, bottom);
        return this;
    }

    /**
     * 覆盖内边距（单次有效，水平/垂直统一）。
     *
     * @param horizontal 水平边距（dp）
     * @param vertical   垂直边距（dp）
     * @return 当前实例
     */
    public XToast setPaddingDp(float horizontal, float vertical) {
        mOverrideConfig.setPaddingDp(horizontal, vertical);
        return this;
    }

    // ====================== 时长解析 ======================

    /**
     * 返回系统 Toast 时长常量（LENGTH_SHORT 或 LENGTH_LONG）。
     * 用于 {@link Toast#makeText} 等需要系统常量的场景。
     */
    private int resolveSysDuration() {
        if (mDurationType == DUR_LONG) {
            return Toast.LENGTH_LONG;
        }
        return Toast.LENGTH_SHORT;
    }

    /**
     * 返回实际毫秒数，用于延迟取消的计时。
     */
    private int resolveDurationMs() {
        switch (mDurationType) {
            case DUR_LONG:
                return MS_LONG;
            case DUR_CUSTOM:
                return mCustomDurationMs > 0 ? mCustomDurationMs : MS_SHORT;
            default:
                return MS_SHORT;
        }
    }

    /**
     * 返回构建 Toast 对象时应使用的时长常量。
     * 自定义毫秒时长使用 LENGTH_SHORT 显示，到期后主动取消。
     */
    private int resolveToastDuration() {
        if (mDurationType == DUR_CUSTOM) {
            return Toast.LENGTH_SHORT;
        }
        return resolveSysDuration();
    }

    // ====================== 显示 ======================

    /**
     * 显示 Toast。
     * <p>
     * 同一时刻只有一个 Toast 能显示，新的会取消旧的。
     * 未设置样式时走系统原生路径，设置样式时走自定义 View 路径。
     */
    public void show() {
        if (mText == null || mText.length() == 0) return;

        sMainHandler.post(() -> {
            // 取消上一个 Toast，保证屏幕不会同时出现多个 Toast
            if (sCurrentToast != null) {
                sCurrentToast.cancel();
                sMainHandler.removeCallbacksAndMessages(null);
                sCurrentToast = null;
            }

            boolean hasCustomStyle = hasAnyCustomStyle();

            if (!hasCustomStyle) {
                // 无样式定制，直接使用系统原生 Toast，最轻量
                sCurrentToast = Toast.makeText(
                        AppHolder.get(), mText, resolveSysDuration());
                applyGravity(sCurrentToast);
                sCurrentToast.show();
            } else {
                // 样式定制路径，构建自定义 View 后塞入 Toast
                Toast toast = new Toast(AppHolder.get());
                toast.setDuration(resolveToastDuration());
                applyGravity(toast);
                toast.setView(buildStyledView());
                toast.show();
                sCurrentToast = toast;

                // 自定义时长或 Long 时长需主动取消
                if (mDurationType == DUR_CUSTOM) {
                    scheduleCancel(mCustomDurationMs > 0 ? mCustomDurationMs : MS_SHORT);
                } else if (mDurationType == DUR_LONG) {
                    scheduleCancel(MS_LONG);
                }
            }
        });
    }

    /**
     * 将用户设置的 Gravity 应用到 Toast 对象上。
     */
    private void applyGravity(Toast toast) {
        if (mHasGravity) {
            toast.setGravity(mGravity, mGravityX, mGravityY);
        }
    }

    /**
     * 延迟指定时间后取消当前 Toast 并清理静态引用。
     *
     * @param delayMs 延迟毫秒数
     */
    private void scheduleCancel(int delayMs) {
        sMainHandler.postDelayed(() -> {
            if (sCurrentToast != null) {
                sCurrentToast.cancel();
                sCurrentToast = null;
            }
        }, delayMs);
    }

    // ====================== 样式检测 ======================

    /**
     * 判断是否有任何自定义样式（全局 + 单次覆盖）。
     * 都没有时走系统原生路径，避免创建自定义 View 的开销。
     */
    private boolean hasAnyCustomStyle() {
        XToastConfig global = sGlobalConfig;
        return (global != null && global.hasAny())
                || mOverrideConfig.hasAny();
    }

    // ====================== 自定义 View 构建 ======================

    /**
     * 根据合并后的配置（全局 + 覆盖）构建自定义 Toast View。
     * <p>
     * 包含：背景圆角、文字颜色/大小、内边距、最大宽度限制。
     */
    private View buildStyledView() {
        XToastConfig merged = mergeConfig();

        float density = AppHolder.get().getResources().getDisplayMetrics().density;

        // 背景：使用 GradientDrawable 实现圆角
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(merged.getBgColor(DEFAULT_BG_COLOR));
        bg.setCornerRadius(merged.getCornerRadius(DEFAULT_CORNER_RADIUS_DP) * density);

        // 文字
        TextView tv = new TextView(AppHolder.get());
        tv.setText(mText);
        tv.setTextColor(merged.getTextColor(DEFAULT_TEXT_COLOR));
        if (merged.getTextSizeSp() > 0) {
            tv.setTextSize(merged.getTextSizeSp());
        }
        tv.setGravity(Gravity.CENTER);

        // 内边距
        if (merged.getPaddingHorizontal() > 0 || merged.getPaddingVertical() > 0) {
            float ph = merged.getPaddingHorizontal() > 0
                    ? merged.getPaddingHorizontal() * density : 24f * density;
            float pv = merged.getPaddingVertical() > 0
                    ? merged.getPaddingVertical() * density : 12f * density;
            tv.setPadding((int) ph, (int) pv, (int) ph, (int) pv);
        }

        // 最大宽度
        if (merged.getMaxWidthDp() > 0) {
            tv.setMaxWidth((int) (merged.getMaxWidthDp() * density));
        }

        // 外层容器
        FrameLayout container = new FrameLayout(AppHolder.get());
        container.setBackground(bg);
        container.addView(tv, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT));

        return container;
    }

    // ====================== 配置合并 ======================

    /**
     * 合并全局配置和单次覆盖配置。
     * <p>
     * 属性级合并：单次覆盖设置了就覆盖，没设置的属性走全局配置。
     * 两个都没设置的属性调用方会用默认值（如 DEFAULT_BG_COLOR）。
     */
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
