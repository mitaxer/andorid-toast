package com.mitaxer.toast.sample;

import android.app.Application;

public class SampleApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 出厂默认即满足当前需求，无需额外初始化。
        // 如需自定义配色，可调用 XToast.init(light, dark) 覆盖。
    }
}
