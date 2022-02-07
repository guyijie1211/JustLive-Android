package com.sunnyweather.android.ui.customerUIs.bottomPop;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.contrarywind.view.WheelView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.lxj.xpopup.core.BottomPopupView;
import com.sunnyweather.android.R;
import com.sunnyweather.android.ui.liveRoom.LiveRoomActivity;

import java.util.ArrayList;
import java.util.List;

public class TimeBottomPop extends BottomPopupView {

    private SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

    public TimeBottomPop(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.time_bottom_custom;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        SwitchMaterial switchBtnOn = findViewById(R.id.time_bottom_switch);
        SwitchMaterial switchBtn = findViewById(R.id.time_bottom_switch_auto);

        switchBtnOn.setChecked(sharedPreferences.getBoolean("closeAppOn", false));
        switchBtnOn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            switchBtn.setEnabled(isChecked);
            if (isChecked) {
                startCountDown();
            } else {
                sharedPreferences.edit().putBoolean("closeAppOn", false).commit();
                switchBtn.setChecked(false);
                ((LiveRoomActivity)getContext()).stopCountdown();
            }
        });


        switchBtn.setChecked(sharedPreferences.getBoolean("closeAppOn", false));
        switchBtn.setEnabled(switchBtnOn.isChecked());
        switchBtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("closeAppOn", isChecked).commit();
        });

        WheelView wheelView = findViewById(R.id.time_wheelview);

        wheelView.setCyclic(false);
        int theme = sharedPreferences.getInt("theme", R.style.SunnyWeather);
        if (theme != R.style.nightTheme) {
            wheelView.setTextColorCenter(R.color.colorOnPrimary);
        } else {
            wheelView.setTextColorCenter(R.color.colorOnPrimary_night);
        }
        final List<String> mOptionsItems = new ArrayList<>();
        for (int i = 1; i <= 120; i++) {
            mOptionsItems.add(String.valueOf(i));
        }

        ArrayWheelAdapter adapter = new ArrayWheelAdapter(mOptionsItems);
        wheelView.setAdapter(adapter);
        wheelView.setCurrentItem(sharedPreferences.getInt("closeAppTime", 0) - 1);
        wheelView.setOnItemSelectedListener(index -> {
            sharedPreferences.edit().putInt("closeAppTime", Integer.parseInt(mOptionsItems.get(index))).commit();
            startCountDown();
        });
    }

    private void startCountDown() {
        int time = sharedPreferences.getInt("closeAppTime", 0);
        SwitchMaterial switchBtnOn = findViewById(R.id.time_bottom_switch);
        if (switchBtnOn.isChecked() && time > 0) {
            CountDownTimer countDownTimer = new CountDownTimer(time * 60000, 10000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    // 5分钟倒计时
                    if (millisUntilFinished < 5.5 * 60000 && millisUntilFinished > 4.5 * 60000) {
                        ToastUtils.showLong("5分钟后关闭app");
                    }
                }
                @Override
                public void onFinish() {
                    AppUtils.exitApp();
                }
            };
            ((LiveRoomActivity)getContext()).setCountDown(countDownTimer);
            ToastUtils.showShort("开始计时," + time + "分钟后退出应用");
        }
    }

}
