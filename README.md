# XToast

Zero-dependency, stylable Android Toast utility.

## Features

- **No third-party dependencies** — pure Android SDK
- **System Toast** — reliable, zero extra permissions
- **Customizable** — background color, corner radius, text color/size, max width, padding
- **Global style + per-call override** — property-level merge
- **Auto-init** — no manual initialization required
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
    implementation("com.github.mitaxer:andorid-toast:1.0.0")
}
```

## Usage

### Quick show

```java
XToast.showShort("保存成功");
XToast.showLong("加载中...");
XToast.show("自定义时长", 5000);   // custom ms
```

### Global style (optional)

```java
// Application.onCreate()
XToast.init(config -> {
    config.setBgColor(0xFF333333)
          .setCornerRadius(24)
          .setTextColor(Color.WHITE)
          .setTextSizeSp(14)
          .setMaxWidthDp(300)
          .setPaddingDp(24, 12);
});
```

### Per-call override

```java
XToast.make()
    .setText("操作失败")
    .setBgColor(0xFFE53935)    // override only bg color
    .setDuration(3000)
    .show();
```

### Priority

```
per-call override > global config > system default
```

Each property merges independently — set only what you need to override.

## License

MIT
