package com.mitaxer.toast.sample;

import android.app.Application;

import com.mitaxer.toast.XToast;

public class SampleApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        XToast.init(config -> {
            config.setBgColor(0xFF333333)
                  .setCornerRadius(24)
                  .setTextColor(0xFFFFFFFF)
                  .setTextSizeSp(15)
                  .setMaxWidthDp(320)
                  .setPaddingDp(24, 12);
        });
    }
}
