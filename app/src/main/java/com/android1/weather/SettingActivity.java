package com.android1.weather;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ToggleButton;

public class SettingActivity extends AppCompatActivity {
    private ToggleButton btnThongbao, btnAnhdong, btnDonvi;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setTitle("Thiết lập");
        sharedPreferences = getSharedPreferences("WEATHER", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        addControls();

        savePreference();

    }

    private void savePreference() {
        btnDonvi.setChecked(sharedPreferences.getBoolean("Donvi", true));
        btnDonvi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnDonvi.isChecked()) {
                    editor.putBoolean("Donvi", true);
                    editor.commit();
                } else {
                    editor.putBoolean("Donvi", false);
                    editor.commit();
                }

            }
        });
        btnThongbao.setChecked(sharedPreferences.getBoolean("Thongbao", true));
        btnThongbao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnThongbao.isChecked()) {
                    editor.putBoolean("Thongbao", true);
                    editor.commit();
                } else {
                    editor.putBoolean("Thongbao", false);
                    editor.commit();
                }

            }
        });
        btnAnhdong.setChecked(sharedPreferences.getBoolean("Anhdong", true));
        btnAnhdong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnAnhdong.isChecked()) {
                    editor.putBoolean("Anhdong", true);
                    editor.commit();
                } else {
                    editor.putBoolean("Anhdong", false);
                    editor.commit();
                }

            }
        });


    }


    private void addControls() {
        btnDonvi = (ToggleButton) findViewById(R.id.tg_unit);
        btnThongbao = (ToggleButton) findViewById(R.id.tg_notifi);
        btnAnhdong = (ToggleButton) findViewById(R.id.tg_animation);
    }
}
