package com.mitaxer.common.sample;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.mitaxer.common.XToast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(32, 32, 32, 32);

        // 基本功能
        root.addView(btn("默认 Short", v -> XToast.showShort("保存成功")));
        root.addView(btn("默认 Long", v -> XToast.showLong("加载中...")));
        root.addView(btn("自定义时长 5s", v -> XToast.show("自定义时长", 5000)));

        // 样式覆盖
        root.addView(btn("红色背景", v ->
                XToast.make().setText("操作失败").setBgColor(0xFFE53935).show()));
        root.addView(btn("绿色圆角", v ->
                XToast.make().setText("操作成功").setBgColor(0xFF43A047).setCornerRadius(8).show()));
        root.addView(btn("蓝色大字", v ->
                XToast.make().setText("提示信息").setBgColor(0xFF1E88E5)
                        .setTextSizeSp(20).setTextColor(Color.WHITE).show()));

        // 位置测试
        root.addView(btn("顶部居中", v ->
                XToast.make().setText("顶部居中")
                        .setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100)
                        .show()));
        root.addView(btn("底部靠右", v ->
                XToast.make().setText("底部靠右")
                        .setGravity(Gravity.BOTTOM | Gravity.RIGHT, 0, 100)
                        .show()));
        root.addView(btn("屏幕居中", v ->
                XToast.make().setText("屏幕居中")
                        .setGravity(Gravity.CENTER, 0, 0)
                        .show()));

        // 长文测试
        root.addView(btn("长文字测试", v ->
                XToast.showLong("这是一段很长的文字，用来测试最大宽度限制是否生效，看看会不会撑满屏幕")));

        // Dialog 遮挡测试
        root.addView(btn("Dialog + Toast", v -> {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("测试 Dialog")
                    .setMessage("Dialog 显示中，下面会弹 Toast，看是否能盖在 Dialog 之上")
                    .setPositiveButton("知道了", null)
                    .show();

            // Dialog 不关闭，直接弹 Toast
            XToast.make()
                    .setText("这个 Toast 应该盖在 Dialog 上面")
                    .setGravity(Gravity.CENTER, 0, 0)
                    .show();
        }));

        // 加滚动
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
