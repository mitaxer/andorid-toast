package com.mitaxer.toast;

import android.graphics.Color;

import androidx.annotation.ColorInt;

/**
 * XToast 样式配置模型。
 * 同时用于全局默认配置和单次调用覆盖。
 * 每个属性跟踪是否被设置，实现属性级合并。
 *
 * @author mitaxer
 */
public final class XToastConfig {

    private int bgColor = Color.TRANSPARENT;
    private float cornerRadius = -1f;
    private int textColor = Color.TRANSPARENT;
    private float textSizeSp = -1f;
    private float maxWidthDp = -1f;
    private float paddingHorizontal = -1f;
    private float paddingVertical = -1f;

    private boolean hasBgColor;
    private boolean hasCornerRadius;
    private boolean hasTextColor;
    private boolean hasTextSizeSp;
    private boolean hasMaxWidthDp;
    private boolean hasPadding;

    // ========== Setter（Builder 链式调用） ==========

    /** 设置背景颜色。 */
    public XToastConfig setBgColor(@ColorInt int color) {
        this.bgColor = color;
        this.hasBgColor = true;
        return this;
    }

    /** 设置圆角半径（dp）。 */
    public XToastConfig setCornerRadius(float radiusDp) {
        this.cornerRadius = radiusDp;
        this.hasCornerRadius = true;
        return this;
    }

    /** 设置文字颜色。 */
    public XToastConfig setTextColor(@ColorInt int color) {
        this.textColor = color;
        this.hasTextColor = true;
        return this;
    }

    /** 设置文字字号（sp）。 */
    public XToastConfig setTextSizeSp(float sizeSp) {
        this.textSizeSp = sizeSp;
        this.hasTextSizeSp = true;
        return this;
    }

    /** 设置最大宽度（dp），限制长文字时的显示宽度。 */
    public XToastConfig setMaxWidthDp(float maxWidthDp) {
        this.maxWidthDp = maxWidthDp;
        this.hasMaxWidthDp = true;
        return this;
    }

    /** 设置内边距（分别指定四个方向 dp），内部使用水平/垂直均值存储。 */
    public XToastConfig setPaddingDp(float left, float top, float right, float bottom) {
        this.paddingHorizontal = (left + right) / 2f;
        this.paddingVertical = (top + bottom) / 2f;
        this.hasPadding = true;
        return this;
    }

    /** 设置内边距（水平/垂直统一 dp）。 */
    public XToastConfig setPaddingDp(float horizontal, float vertical) {
        this.paddingHorizontal = horizontal;
        this.paddingVertical = vertical;
        this.hasPadding = true;
        return this;
    }

    /** 从另一个配置对象复制内边距（包内使用）。 */
    XToastConfig setPadding(XToastConfig other) {
        this.paddingHorizontal = other.paddingHorizontal;
        this.paddingVertical = other.paddingVertical;
        this.hasPadding = other.hasPadding;
        return this;
    }

    // ========== Getter ==========

    public int getBgColor() { return bgColor; }
    public int getBgColor(int defaultColor) { return hasBgColor ? bgColor : defaultColor; }

    public float getCornerRadius() { return cornerRadius; }
    public float getCornerRadius(float defaultRadius) { return hasCornerRadius ? cornerRadius : defaultRadius; }

    public int getTextColor() { return textColor; }
    public int getTextColor(int defaultColor) { return hasTextColor ? textColor : defaultColor; }

    public float getTextSizeSp() { return textSizeSp; }
    public float getMaxWidthDp() { return maxWidthDp; }
    public float getPaddingHorizontal() { return paddingHorizontal; }
    public float getPaddingVertical() { return paddingVertical; }

    // ========== 状态检测 ==========

    public boolean hasBgColor() { return hasBgColor; }
    public boolean hasCornerRadius() { return hasCornerRadius; }
    public boolean hasTextColor() { return hasTextColor; }
    public boolean hasTextSizeSp() { return hasTextSizeSp; }
    public boolean hasMaxWidthDp() { return hasMaxWidthDp; }
    public boolean hasPadding() { return hasPadding; }

    /** 是否有任意属性被设置。 */
    public boolean hasAny() {
        return hasBgColor || hasCornerRadius || hasTextColor
                || hasTextSizeSp || hasMaxWidthDp || hasPadding;
    }
}
