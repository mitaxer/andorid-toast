package com.mitaxer.toast.sample;

import android.app.Application;

import com.mitaxer.toast.XToast;
import com.mitaxer.toast.XToastConfig;

public class SampleApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 全局风格配置（测试用�?        XToast.init(config -> {
            config.setBgColor(0xFF333333)
                  .setCornerRadius(24)
                  .setTextColor(0xFFFFFFFF)
                  .setTextSizeSp(15)
                  .setMaxWidthDp(320)
                  .setPaddingDp(24, 12);
        });
    }
}
