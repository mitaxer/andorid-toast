package com.mitaxer.toast.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.mitaxer.toast.XToast;

/**
 * XToast 功能演示
 *
 * 预设已在 SampleApp 中通过 init(light, dark) 配置。
 * 此处仅测试预设切换和自定义 init。
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(32, 32, 32, 32);

        addTitle(root, "— 预设切换（颜色由 SampleApp 配置）—");
        root.addView(btn("useLightMode()", v -> {
            XToast.useLightMode();
            XToast.showShort("LIGHT 预设");
        }));
        root.addView(btn("useDarkMode()", v -> {
            XToast.useDarkMode();
            XToast.showShort("DARK 预设");
        }));

        addTitle(root, "— 自定义 init（不影响预设）—");
        root.addView(btn("init() 暖白", v -> {
            XToast.init(cfg -> cfg.setBgColor(0xFFFFF0).setTextColor(0xFF333333));
            XToast.showShort("暖白自定义");
        }));
        root.addView(btn("init() 橙底白字", v -> {
            XToast.init(cfg -> cfg.setBgColor("#FF5722").setTextColor("#FFFFFF"));
            XToast.showShort("橙色底白字");
        }));
        root.addView(btn("init() 绿底白字", v -> {
            XToast.init(cfg -> cfg.setBgColor("0x4CAF50").setTextColor("#FFFFFF"));
            XToast.showShort("绿色底白字");
        }));

        addTitle(root, "— Builder 单次覆盖 —");
        root.addView(btn("红色圆角 + 白字 Builder", v ->
                XToast.make()
                        .setText("失败")
                        .setBgColor(0xFFE53935)
                        .setTextColor(0xFFFFFFFF)
                        .setCornerRadius(8)
                        .show()));
        root.addView(btn("顶部居中", v ->
                XToast.make()
                        .setText("顶部居中")
                        .setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100)
                        .show()));
        root.addView(btn("长文字测试", v ->
                XToast.showLong("长文字测试最大宽度限制，默认 320dp")));

        root.addView(btn("null 安全测试", v -> XToast.show(null)));

        ScrollView sv = new ScrollView(this);
        sv.addView(root);
        setContentView(sv);
    }

    private void addTitle(LinearLayout parent, String text) {
        android.widget.TextView tv = new android.widget.TextView(this);
        tv.setText(text);
        tv.setTextSize(12);
        tv.setTextColor(0xFF888888);
        tv.setPadding(0, 24, 0, 8);
        parent.addView(tv);
    }

    private Button btn(String text, android.view.View.OnClickListener listener) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setAllCaps(false);
        btn.setOnClickListener(listener);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = 8;
        btn.setLayoutParams(lp);
        return btn;
    }
}
