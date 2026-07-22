package com.mitaxer.common.sample;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;

import com.mitaxer.common.XToast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(32, 32, 32, 32);

        // 默认样式
        root.addView(createButton("默认 Short", v -> XToast.showShort("保存成功")));
        root.addView(createButton("默认 Long", v -> XToast.showLong("加载中...")));
        root.addView(createButton("自定义时长 5s", v -> XToast.show("自定义时长", 5000)));

        // 临时覆盖样式
        root.addView(createButton("红色背景", v ->
                XToast.make().setText("操作失败")
                        .setBgColor(0xFFE53935)
                        .show()
        ));
        root.addView(createButton("绿色圆角", v ->
                XToast.make().setText("操作成功")
                        .setBgColor(0xFF43A047)
                        .setCornerRadius(8)
                        .show()
        ));
        root.addView(createButton("蓝色大号文字", v ->
                XToast.make().setText("提示信息")
                        .setBgColor(0xFF1E88E5)
                        .setTextSizeSp(20)
                        .setTextColor(Color.WHITE)
                        .show()
        ));
        root.addView(createButton("居中顶部显示", v ->
                XToast.make().setText("顶部居中")
                        .show()
        ));
        root.addView(createButton("长文字测试", v ->
                XToast.showLong("这是一段很长的文字，用来测试最大宽度限制是否生效，看看会不会撑满屏幕")
        ));

        setContentView(root);
    }

    private Button createButton(String text, android.view.View.OnClickListener listener) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setAllCaps(false);
        btn.setOnClickListener(listener);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = 16;
        btn.setLayoutParams(lp);
        return btn;
    }
}
