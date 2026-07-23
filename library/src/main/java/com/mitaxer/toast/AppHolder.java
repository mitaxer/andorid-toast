package com.mitaxer.toast;

import android.app.Application;

import androidx.annotation.NonNull;

/**
 * Internal holder for Application context.
 * Auto-initialized by {@link XToastInitProvider}.
 */
final class AppHolder {

    private static Application sApp;

    private AppHolder() {}

    static void init(@NonNull Application app) {
        if (app == null) {
            throw new IllegalStateException("XToast: AppHolder.init() got null Application");
        }
        sApp = app;
    }

    @NonNull
    static Application get() {
        if (sApp == null) {
            throw new IllegalStateException(
                    "AppHolder not initialized. " +
                    "Make sure XToastInitProvider is registered in AndroidManifest.xml");
        }
        return sApp;
    }
}
