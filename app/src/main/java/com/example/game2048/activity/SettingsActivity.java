package com.example.game2048.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.game2048.R;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences.Editor gsEditor;

    private SharedPreferences GameSettings;

    private TextView textThemeSwitch;

    private ImageView solidColorSwitchImage;

    private TextView textSolidColorSwitch;

    private ImageView soundSwitchImage;

    private TextView textSoundSwitch;

    private LinearLayout settingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        init();
    }
    public void init(){
        settingLayout = (LinearLayout) findViewById(R.id.settingLayout);
        textSoundSwitch=(TextView) findViewById(R.id.soundSwitch);
        soundSwitchImage = (ImageView) findViewById(R.id.soundSwitchImage);
        textSolidColorSwitch=(TextView) findViewById(R.id.solidColorSwitch);
        solidColorSwitchImage = (ImageView) findViewById(R.id.solidColorSwitchImage);
        textThemeSwitch = (TextView) findViewById(R.id.themeSwitch);
        GameSettings = getSharedPreferences("GameSettings", Context.MODE_PRIVATE);
        gsEditor = GameSettings.edit();
        boolean flag;
        flag = GameSettings.getBoolean("SoundSwitch", false);
        if(flag){
            soundSwitchImage.setImageResource(R.drawable.open);
            textSoundSwitch.setText("音效：开");
        }
        flag = GameSettings.getBoolean("SolidColorSwitch", false);
        if(flag){
            solidColorSwitchImage.setImageResource(R.drawable.open);
            textSolidColorSwitch.setText("纯色块：开");
        }
        int index = GameSettings.getInt("ThemeIndex", 1);
        switch (index){
            case 1:
                settingLayout.setBackgroundResource(R.drawable.back1);
                textThemeSwitch.setText("主题1");
                break;
            case 2:
                settingLayout.setBackgroundResource(R.drawable.back2);
                textThemeSwitch.setText("主题2");
                break;
            case 3:
                settingLayout.setBackgroundResource(R.drawable.back3);
                textThemeSwitch.setText("主题3");
                break;
            case 4:
                settingLayout.setBackgroundResource(R.drawable.back4);
                textThemeSwitch.setText("主题4");
                break;
            case 5:
                settingLayout.setBackgroundResource(R.drawable.back5);
                textThemeSwitch.setText("主题5");
                break;
            case  6:
                settingLayout.setBackgroundResource(R.drawable.back6);
                textThemeSwitch.setText("主题6");
                break;
        }
    }

    public void soundSwitch(View view){
        boolean flag = GameSettings.getBoolean("SoundSwitch", false);
        if(flag){
            soundSwitchImage.setImageResource(R.drawable.close);
            textSoundSwitch.setText("音效：关");
        }else{
            soundSwitchImage.setImageResource(R.drawable.open);
            textSoundSwitch.setText("音效：开");
        }
        gsEditor.putBoolean("SoundSwitch", !flag);
        gsEditor.apply();
    }

    public void solidColorSwitch(View view){
        boolean flag = GameSettings.getBoolean("SolidColorSwitch", false);
        if(flag){
            solidColorSwitchImage.setImageResource(R.drawable.close);
            textSolidColorSwitch.setText("纯色块：关");
        }else{
            solidColorSwitchImage.setImageResource(R.drawable.open);
            textSolidColorSwitch.setText("纯色块：开");
        }
        gsEditor.putBoolean("SolidColorSwitch", !flag);
        gsEditor.apply();
    }

    public void themeSwitch(View view){
        int index = GameSettings.getInt("ThemeIndex", 1);
        if (index == 1) {
            settingLayout.setBackgroundResource(R.drawable.back2);
            textThemeSwitch.setText("主题二");
        } else if (index == 2) {
            settingLayout.setBackgroundResource(R.drawable.back3);
            textThemeSwitch.setText("主题三");
        } else if (index == 3) {
            settingLayout.setBackgroundResource(R.drawable.back4);
            textThemeSwitch.setText("主题四");
        } else if (index == 4) {
            settingLayout.setBackgroundResource(R.drawable.back5);
            textThemeSwitch.setText("主题五");
        } else if (index == 5) {
            settingLayout.setBackgroundResource(R.drawable.back6);
            textThemeSwitch.setText("主题六");
        } else if (index == 6) {
            settingLayout.setBackgroundResource(R.drawable.back1);
            textThemeSwitch.setText("主题一");
        }
        if (index == 6) {
            gsEditor.putInt("ThemeIndex", 1);
        } else {
            gsEditor.putInt("ThemeIndex", index + 1);
        }
        gsEditor.apply();
    }
}