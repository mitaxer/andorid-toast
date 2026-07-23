package com.mitaxer.toast;

import android.app.Application;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Auto-initializes {@link AppHolder} on app startup.
 * Registered in AndroidManifest.xml, runs before any Activity/Service starts.
 * <p>
 * No user action required.
 */
public final class XToastInitProvider extends ContentProvider {

    @Override
    public boolean onCreate() {
        try {
            Application app = (Application) getContext().getApplicationContext();
            // 快速验证：用此 Application 创建 Toast 是否正常
            android.widget.Toast.makeText(app, "", android.widget.Toast.LENGTH_SHORT).show();
            AppHolder.init(app);
        } catch (Exception e) {
            throw new RuntimeException("XToastInitProvider: cannot create Toast with Application: "
                    + getContext() + " / " + getContext().getApplicationContext(), e);
        }
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
