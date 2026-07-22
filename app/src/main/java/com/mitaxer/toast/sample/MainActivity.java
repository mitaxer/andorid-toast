package com.mitaxer.toast.sample;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.mitaxer.toast.XToast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(32, 32, 32, 32);

        root.addView(btn("default Short", v -> XToast.showShort("save success")));
        root.addView(btn("default Long", v -> XToast.showLong("loading...")));
        root.addView(btn("custom 5s", v -> XToast.show("custom duration", 5000)));

        root.addView(btn("red bg", v ->
                XToast.make().setText("failed").setBgColor(0xFFE53935).show()));
        root.addView(btn("green round", v ->
                XToast.make().setText("success").setBgColor(0xFF43A047).setCornerRadius(8).show()));
        root.addView(btn("blue big", v ->
                XToast.make().setText("info").setBgColor(0xFF1E88E5)
                        .setTextSizeSp(20).setTextColor(Color.WHITE).show()));

        root.addView(btn("top center", v ->
                XToast.make().setText("top center")
                        .setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100)
                        .show()));
        root.addView(btn("bottom right", v ->
                XToast.make().setText("bottom right")
                        .setGravity(Gravity.BOTTOM | Gravity.RIGHT, 0, 100)
                        .show()));
        root.addView(btn("center", v ->
                XToast.make().setText("center")
                        .setGravity(Gravity.CENTER, 0, 0)
                        .show()));

        root.addView(btn("long text", v ->
                XToast.showLong("long text for max width test long text for max width test")));

        root.addView(btn("Dialog + Toast", v -> {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Dialog Test")
                    .setMessage("Toast should appear on top of this Dialog")
                    .setPositiveButton("OK", null)
                    .show();

            XToast.make()
                    .setText("This Toast is on top of Dialog")
                    .setGravity(Gravity.CENTER, 0, 0)
                    .show();
        }));

        ScrollView sv = new ScrollView(this);
        sv.addView(root);
        setContentView(sv);
    }

    private Button btn(String text, android.view.View.OnClickListener listener) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setAllCaps(false);
        btn.setOnClickListener(listener);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = 12;
        btn.setLayoutParams(lp);
        return btn;
    }
}
