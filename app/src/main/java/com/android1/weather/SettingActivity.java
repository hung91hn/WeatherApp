package com.android1.weather;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

public class SettingActivity extends AppCompatActivity {

    ToggleButton btnThongbao,btnAnhdong,btnDonvi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setTitle("Thiết lập");
        addControls();
        addEvents();
    }

    private void addEvents() {
        btnDonvi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
             if (compoundButton.isChecked());
                else {
                 Intent intent=getIntent();
                 Bundle bundle=intent.getBundleExtra("TEMP");
                 String s=bundle.getString("temp");
                int i= (int) ((Integer.valueOf(s)-32)/1.8);
                 Bundle bundle1=new Bundle();
                 bundle1.putInt("tempC",i);
                 Intent intent1=new Intent(SettingActivity.this,MainActivity.class);
                 intent1.putExtra("TEMPC",bundle);
                 startActivity(intent1);
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
