package com.mitaxer.common;

import android.graphics.Color;

import androidx.annotation.ColorInt;

/**
 * Configuration model for XToast style properties.
 * <p>
 * Used for both global default config and per-call override.
 * Each property tracks whether it has been explicitly set,
 * enabling property-level merge between override and global config.
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

    // ============ Setters (Builder-style) ============

    public XToastConfig setBgColor(@ColorInt int color) {
        this.bgColor = color;
        this.hasBgColor = true;
        return this;
    }

    public XToastConfig setCornerRadius(float radiusDp) {
        this.cornerRadius = radiusDp;
        this.hasCornerRadius = true;
        return this;
    }

    public XToastConfig setTextColor(@ColorInt int color) {
        this.textColor = color;
        this.hasTextColor = true;
        return this;
    }

    public XToastConfig setTextSizeSp(float sizeSp) {
        this.textSizeSp = sizeSp;
        this.hasTextSizeSp = true;
        return this;
    }

    public XToastConfig setMaxWidthDp(float maxWidthDp) {
        this.maxWidthDp = maxWidthDp;
        this.hasMaxWidthDp = true;
        return this;
    }

    public XToastConfig setPaddingDp(float left, float top, float right, float bottom) {
        this.paddingHorizontal = (left + right) / 2f;
        this.paddingVertical = (top + bottom) / 2f;
        this.hasPadding = true;
        return this;
    }

    public XToastConfig setPaddingDp(float horizontal, float vertical) {
        this.paddingHorizontal = horizontal;
        this.paddingVertical = vertical;
        this.hasPadding = true;
        return this;
    }

    XToastConfig setPadding(XToastConfig other) {
        this.paddingHorizontal = other.paddingHorizontal;
        this.paddingVertical = other.paddingVertical;
        this.hasPadding = other.hasPadding;
        return this;
    }

    // ============ Getters (with defaults) ============

    @ColorInt
    public int getBgColor() {
        return bgColor;
    }

    @ColorInt
    public int getBgColor(int defaultColor) {
        return hasBgColor ? bgColor : defaultColor;
    }

    public float getCornerRadius() {
        return cornerRadius;
    }

    public float getCornerRadius(float defaultRadius) {
        return hasCornerRadius ? cornerRadius : defaultRadius;
    }

    @ColorInt
    public int getTextColor() {
        return textColor;
    }

    @ColorInt
    public int getTextColor(int defaultColor) {
        return hasTextColor ? textColor : defaultColor;
    }

    public float getTextSizeSp() {
        return textSizeSp;
    }

    public float getMaxWidthDp() {
        return maxWidthDp;
    }

    public float getPaddingHorizontal() {
        return paddingHorizontal;
    }

    public float getPaddingVertical() {
        return paddingVertical;
    }

    // ============ State checks ============

    public boolean hasBgColor() { return hasBgColor; }
    public boolean hasCornerRadius() { return hasCornerRadius; }
    public boolean hasTextColor() { return hasTextColor; }
    public boolean hasTextSizeSp() { return hasTextSizeSp; }
    public boolean hasMaxWidthDp() { return hasMaxWidthDp; }
    public boolean hasPadding() { return hasPadding; }

    public boolean hasAny() {
        return hasBgColor || hasCornerRadius || hasTextColor
                || hasTextSizeSp || hasMaxWidthDp || hasPadding;
    }
}
