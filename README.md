# XToast

Zero-dependency, stylable Android Toast utility.

## Features

- **No third-party dependencies** — pure Android SDK
- **System Toast** — reliable, zero extra permissions
- **Day/Night theme presets** — built-in LIGHT & DARK, switch anytime
- **Customizable** — background color, corner radius, text color/size, max width, padding
- **Auto-init** — no manual initialization required
- **String color support** — `"#RRGGBB"`, `"#AARRGGBB"`, `"0xRRGGBB"` all accepted
- **Auto-fix alpha** — `0xFFFFF0` writes like `0xFFFFFFF0`, library fills alpha=FF for you
- **Null-safe** — pass `null` or empty string, Toast silently ignored
- **Custom style fallback** — if styled view fails, degrades to system Toast automatically
- **Three show methods** — short, long, custom duration

## Setup

### Add repository

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        maven("https://jitpack.io")
    }
}
```

### Add dependency

```kotlin
// app/build.gradle.kts
dependencies {
    implementation("com.github.mitaxer:android-toast:main-SNAPSHOT")
}
```

## Usage

### Quick show

```java
XToast.showShort("保存成功");
XToast.showLong("加载中...");
XToast.show("自定义时长", 5000);   // custom ms
XToast.show(null);                 // safe, silently ignored
```

### Theme presets (LIGHT / DARK)

```java
// Default is LIGHT. Switch anytime:
XToast.useLightMode();   // 半透灰底 0x55C0C0C0 + 深灰字 0xFF333333
XToast.useDarkMode();    // 深灰底 0xFF2B2B2B + 白字 0xFFFFFFFF
```

### Configure both presets at app startup

```java
// Application.onCreate()
XToast.init(
    light -> light
        .setBgColor(0x55C0C0C0)
        .setTextColor(0xFF333333)
        .setCornerRadius(8)
        .setTextSizeSp(15)
        .setMaxWidthDp(320)
        .setPaddingDp(16, 8),
    dark -> dark
        .setBgColor(0xFF2B2B2B)
        .setTextColor(0xFFFFFFFF)
        .setCornerRadius(8)
        .setTextSizeSp(15)
        .setMaxWidthDp(320)
        .setPaddingDp(16, 8)
);
// After init, useLightMode() / useDarkMode() use your colors
```

### Custom one-off style (doesn't affect presets)

```java
XToast.init(config -> {
    config.setBgColor(0xFFE53935)
          .setTextColor(0xFFFFFFFF)
          .setCornerRadius(8)
          .setTextSizeSp(15)
          .setMaxWidthDp(320)
          .setPaddingDp(16, 8);
});
```

### String color

```java
XToast.init(cfg -> cfg
    .setBgColor("#FF5722")   // orange
    .setTextColor("#FFFFFF")
);
XToast.init(cfg -> cfg
    .setBgColor("0x4CAF50")  // green, alpha auto-filled
    .setTextColor("#FFFFFF")
);
```

### Per-call override (Builder)

```java
XToast.make()
    .setText("操作失败")
    .setBgColor(0xFFE53935)
    .setDuration(3000)
    .show();

XToast.make()
    .setText("顶部居中")
    .setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100)
    .show();
```

### Custom gravity

```java
XToast.make()
    .setText("顶部居中")
    .setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100)
    .show();

XToast.make()
    .setText("底部靠右")
    .setGravity(Gravity.BOTTOM | Gravity.RIGHT, 0, 100)
    .show();
```

### Priority

```
per-call override > init/theme config > factory defaults
```

Each property merges independently — set only what you need to override.

## License

MIT
