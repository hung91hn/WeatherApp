package com.android1.weather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.android.volley.misc.DiskLruCache;

public class SettingActivity extends AppCompatActivity {

    ToggleButton btnThongbao,btnAnhdong,btnDonvi;
    SharedPreferences s1,s2,s3;
    boolean on ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setTitle("Thiết lập");

        addControls();

        savePreference();

    }

    private void savePreference() {


        s1=getSharedPreferences("abc",MODE_PRIVATE);
        btnDonvi.setChecked(s1.getBoolean("Donvi",true));
        btnDonvi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnDonvi.isChecked()) {
                    SharedPreferences.Editor editor = (SharedPreferences.Editor) getSharedPreferences("abc", MODE_PRIVATE).edit();
                    editor.putBoolean("Donvi", true);
                    editor.commit();
                } else {
                    SharedPreferences.Editor editor = (SharedPreferences.Editor) getSharedPreferences("abc", MODE_PRIVATE).edit();
                    editor.putBoolean("Donvi", false);
                    editor.commit();
                }

            }
        });
        s2=getSharedPreferences("edf",MODE_PRIVATE);
        btnThongbao.setChecked(s2.getBoolean("Thongbao",true));
        btnThongbao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnThongbao.isChecked()) {
                    SharedPreferences.Editor editor = (SharedPreferences.Editor) getSharedPreferences("edf", MODE_PRIVATE).edit();
                    editor.putBoolean("Thongbao", true);
                    editor.commit();
                } else {
                    SharedPreferences.Editor editor = (SharedPreferences.Editor) getSharedPreferences("edf", MODE_PRIVATE).edit();
                    editor.putBoolean("Thongbao", false);
                    editor.commit();
                }

            }
        });
        s3=getSharedPreferences("xyz",MODE_PRIVATE);
        btnAnhdong.setChecked(s3.getBoolean("Anhdong",true));
        btnAnhdong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnAnhdong.isChecked()) {
                    SharedPreferences.Editor editor = (SharedPreferences.Editor) getSharedPreferences("xyz", MODE_PRIVATE).edit();
                    editor.putBoolean("Anhdong", true);
                    editor.commit();
                } else {
                    SharedPreferences.Editor editor = (SharedPreferences.Editor) getSharedPreferences("xyz", MODE_PRIVATE).edit();
                    editor.putBoolean("Anhdong", false);
                    editor.commit();
                }

            }
        });


    }


    private void addControls() {
        btnDonvi= (ToggleButton) findViewById(R.id.btnDonvi);
        btnThongbao= (ToggleButton) findViewById(R.id.btnThongbao);
        btnAnhdong= (ToggleButton) findViewById(R.id.btnAnhdong);
    }
}
