package com.android1.weather;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ToggleButton;

public class SettingActivity extends AppCompatActivity {
    private ToggleButton btnThongbao, btnAnhdong, btnDonvi;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public static final String TYPE_TEMP="Đơn vị";
    public static final String NOTIFY="Thông báo";
    public static final String GIF_IAMGE="Ảnh động";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
      //  setTitle("Thiết lập");
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getSharedPreferences(MainActivity.KEY_WEATHER, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        addControls();

        savePreference();

    }

    private void savePreference() {
        btnDonvi.setChecked(sharedPreferences.getBoolean(TYPE_TEMP, true));
        btnDonvi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnDonvi.isChecked()) {
                    editor.putBoolean(TYPE_TEMP, true);
                } else {
                    editor.putBoolean(TYPE_TEMP, false);
                }
                    editor.commit();
                MainActivity.changeSetting=true;

            }
        });
        btnThongbao.setChecked(sharedPreferences.getBoolean(NOTIFY, true));
        btnThongbao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnThongbao.isChecked()) {
                    editor.putBoolean(NOTIFY, true);
                    editor.commit();
                } else {
                    editor.putBoolean(NOTIFY, false);
                    editor.commit();
                }

            }
        });
        btnAnhdong.setChecked(sharedPreferences.getBoolean(GIF_IAMGE, true));
        btnAnhdong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnAnhdong.isChecked()) {
                    editor.putBoolean(GIF_IAMGE, true);
                    editor.commit();
                } else {
                    editor.putBoolean(GIF_IAMGE, false);
                    editor.commit();
                }

            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home)
            finish();
        return true;
    }

    private void addControls() {
        btnDonvi = (ToggleButton) findViewById(R.id.tg_unit);
        btnThongbao = (ToggleButton) findViewById(R.id.tg_notifi);
        btnAnhdong = (ToggleButton) findViewById(R.id.tg_animation);
    }
}
