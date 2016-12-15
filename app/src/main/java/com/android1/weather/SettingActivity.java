package com.android1.weather;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ToggleButton;

public class SettingActivity extends AppCompatActivity {
    private ToggleButton btnDonvi;
    private NumberPicker npUpdate;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public static final String TYPE_TEMP="Đơn vị";
    public static final String TIME_UPDATE="updateTime";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
      //  setTitle("Thiết lập");
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        ColorDrawable colorDrawable=new ColorDrawable();
        colorDrawable.setColor(Color.parseColor("#696969"));
        actionBar.setBackgroundDrawable(colorDrawable);

        sharedPreferences = getSharedPreferences(MainActivity.KEY_WEATHER, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        LinearLayout activity_setting= (LinearLayout) findViewById(R.id.activity_setting);
        addControls();

        savePreference();

    }

    @Override
    protected void onPause() {
        super.onPause();
        editor.putInt(TIME_UPDATE,npUpdate.getValue()).commit();
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

        npUpdate.setValue(sharedPreferences.getInt(TIME_UPDATE,1));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home)
            finish();
        return true;
    }

    private void addControls() {
        btnDonvi = (ToggleButton) findViewById(R.id.tg_unit);
        npUpdate= (NumberPicker) findViewById(R.id.np_update);
        npUpdate.setMinValue(1);
        npUpdate.setMaxValue(12);
    }
}
