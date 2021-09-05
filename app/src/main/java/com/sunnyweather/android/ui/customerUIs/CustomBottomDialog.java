package com.sunnyweather.android.ui.customerUIs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;

import com.sunnyweather.android.R;

public class CustomBottomDialog extends Dialog {

    public CustomBottomDialog(@NonNull Context context) {
        super(context, R.style.bottom_dialog_bg_style);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.top_dialog_layout);
        setWindowTheme();
        setCancelable(true);
        setCanceledOnTouchOutside(true);
    }

    private void setWindowTheme() {
        Window window = this.getWindow();
        // 设置弹出位置
        window.setGravity(Gravity.BOTTOM);
        // 设置弹出动画
        window.setWindowAnimations(R.style.show_dialog_animStyle);
        // 设置对话框大小
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
