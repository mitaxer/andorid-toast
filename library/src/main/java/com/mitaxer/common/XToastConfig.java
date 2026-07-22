package com.mitaxer.common;

import android.graphics.Color;

import androidx.annotation.ColorInt;

/**
 * XToast 样式配置模型。
 * <p>
 * 同时用于全局默认配置和单次调用覆盖。
 * 每个属性都跟踪是否被显式设置，实现属性级合并——
 * 单次调用设置了某属性则覆盖全局，没设置的属性回到全局配置。
 *
 * @author mitaxer
 */
public final class XToastConfig {

    /** 背景颜色（默认透明，表示未设置） */
    private int bgColor = Color.TRANSPARENT;
    /** 圆角半径（dp），-1 表示未设置 */
    private float cornerRadius = -1f;
    /** 文字颜色（默认透明，表示未设置） */
    private int textColor = Color.TRANSPARENT;
    /** 文字字号（sp），-1 表示未设置 */
    private float textSizeSp = -1f;
    /** 最大宽度（dp），-1 表示未设置 */
    private float maxWidthDp = -1f;
    /** 水平内边距（dp），-1 表示未设置 */
    private float paddingHorizontal = -1f;
    /** 垂直内边距（dp），-1 表示未设置 */
    private float paddingVertical = -1f;

    /** 是否设置了背景颜色 */
    private boolean hasBgColor;
    /** 是否设置了圆角半径 */
    private boolean hasCornerRadius;
    /** 是否设置了文字颜色 */
    private boolean hasTextColor;
    /** 是否设置了文字字号 */
    private boolean hasTextSizeSp;
    /** 是否设置了最大宽度 */
    private boolean hasMaxWidthDp;
    /** 是否设置了内边距 */
    private boolean hasPadding;

    // ============ Setter（Builder 链式调用） ============

    /**
     * 设置背景颜色。
     *
     * @param color 背景颜色值
     * @return 当前实例
     */
    public XToastConfig setBgColor(@ColorInt int color) {
        this.bgColor = color;
        this.hasBgColor = true;
        return this;
    }

    /**
     * 设置圆角半径。
     *
     * @param radiusDp 圆角半径（dp）
     * @return 当前实例
     */
    public XToastConfig setCornerRadius(float radiusDp) {
        this.cornerRadius = radiusDp;
        this.hasCornerRadius = true;
        return this;
    }

    /**
     * 设置文字颜色。
     *
     * @param color 文字颜色值
     * @return 当前实例
     */
    public XToastConfig setTextColor(@ColorInt int color) {
        this.textColor = color;
        this.hasTextColor = true;
        return this;
    }

    /**
     * 设置文字字号。
     *
     * @param sizeSp 字号（sp）
     * @return 当前实例
     */
    public XToastConfig setTextSizeSp(float sizeSp) {
        this.textSizeSp = sizeSp;
        this.hasTextSizeSp = true;
        return this;
    }

    /**
     * 设置最大宽度（用于限制长文字时的显示宽度）。
     *
     * @param maxWidthDp 最大宽度（dp）
     * @return 当前实例
     */
    public XToastConfig setMaxWidthDp(float maxWidthDp) {
        this.maxWidthDp = maxWidthDp;
        this.hasMaxWidthDp = true;
        return this;
    }

    /**
     * 设置内边距（分别指定四个方向）。
     * 内部使用水平/垂直均值存储。
     *
     * @param left   左边距（dp）
     * @param top    上边距（dp）
     * @param right  右边距（dp）
     * @param bottom 下边距（dp）
     * @return 当前实例
     */
    public XToastConfig setPaddingDp(float left, float top, float right, float bottom) {
        this.paddingHorizontal = (left + right) / 2f;
        this.paddingVertical = (top + bottom) / 2f;
        this.hasPadding = true;
        return this;
    }

    /**
     * 设置内边距（水平/垂直统一）。
     *
     * @param horizontal 水平边距（dp）
     * @param vertical   垂直边距（dp）
     * @return 当前实例
     */
    public XToastConfig setPaddingDp(float horizontal, float vertical) {
        this.paddingHorizontal = horizontal;
        this.paddingVertical = vertical;
        this.hasPadding = true;
        return this;
    }

    /**
     * 从另一个配置对象复制内边距值（用于配置合并）。
     * 包内访问，不对外暴露。
     */
    XToastConfig setPadding(XToastConfig other) {
        this.paddingHorizontal = other.paddingHorizontal;
        this.paddingVertical = other.paddingVertical;
        this.hasPadding = other.hasPadding;
        return this;
    }

    // ============ Getter（带默认值版本） ============

    /**
     * 获取背景颜色。
     */
    @ColorInt
    public int getBgColor() {
        return bgColor;
    }

    /**
     * 获取背景颜色，未设置时返回指定默认值。
     *
     * @param defaultColor 默认颜色
     * @return 已设置返回本身，否则返回 defaultColor
     */
    @ColorInt
    public int getBgColor(int defaultColor) {
        return hasBgColor ? bgColor : defaultColor;
    }

    /**
     * 获取圆角半径（dp）。
     */
    public float getCornerRadius() {
        return cornerRadius;
    }

    /**
     * 获取圆角半径（dp），未设置时返回指定默认值。
     *
     * @param defaultRadius 默认半径
     * @return 已设置返回本身，否则返回 defaultRadius
     */
    public float getCornerRadius(float defaultRadius) {
        return hasCornerRadius ? cornerRadius : defaultRadius;
    }

    /**
     * 获取文字颜色。
     */
    @ColorInt
    public int getTextColor() {
        return textColor;
    }

    /**
     * 获取文字颜色，未设置时返回指定默认值。
     *
     * @param defaultColor 默认颜色
     * @return 已设置返回本身，否则返回 defaultColor
     */
    @ColorInt
    public int getTextColor(int defaultColor) {
        return hasTextColor ? textColor : defaultColor;
    }

    /**
     * 获取文字字号（sp）。
     */
    public float getTextSizeSp() {
        return textSizeSp;
    }

    /**
     * 获取最大宽度（dp）。
     */
    public float getMaxWidthDp() {
        return maxWidthDp;
    }

    /**
     * 获取水平内边距（dp）。
     */
    public float getPaddingHorizontal() {
        return paddingHorizontal;
    }

    /**
     * 获取垂直内边距（dp）。
     */
    public float getPaddingVertical() {
        return paddingVertical;
    }

    // ============ 状态检测 ============ 

    public boolean hasBgColor() { return hasBgColor; }
    public boolean hasCornerRadius() { return hasCornerRadius; }
    public boolean hasTextColor() { return hasTextColor; }
    public boolean hasTextSizeSp() { return hasTextSizeSp; }
    public boolean hasMaxWidthDp() { return hasMaxWidthDp; }
    public boolean hasPadding() { return hasPadding; }

    /**
     * 判断是否设置了至少一个样式属性。
     *
     * @return true 表示有任意属性被设置
     */
    public boolean hasAny() {
        return hasBgColor || hasCornerRadius || hasTextColor
                || hasTextSizeSp || hasMaxWidthDp || hasPadding;
    }
}
