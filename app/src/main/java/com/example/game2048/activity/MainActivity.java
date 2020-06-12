package com.example.game2048.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.game2048.R;

public class MainActivity extends AppCompatActivity {
    //游戏记录
    private SharedPreferences gameRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gameRecord = getSharedPreferences("GameRecord", Context.MODE_PRIVATE);
    }

    //软件说明
    public void explain(View view){
        Intent intent = new Intent(MainActivity.this,ExplainActivity.class);
        startActivity(intent);
    }

    //4*4
    public void fourRow(View view){
        startActivity(4);
    }

    //5*5
    public void fiveRow(View view){
        startActivity(5);
    }

    //6*6
    public void sixRow(View view){
        startActivity(6);
    }

    public void startActivity(int row){
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        intent.putExtra("Row",row);
        startActivity(intent);
    }

    //恢复游戏
    public void recoverGame(View view){
        if(gameRecord.contains("Row")){
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            int row = gameRecord.getInt("Row", 4);
            if(row==4){
                intent.putExtra("Row",4);
            }else if(row==5){
                intent.putExtra("Row",5);
            }else if(row==6){
                intent.putExtra("Row",6);
            }
            intent.putExtra("RecoverGame",true);
            startActivity(intent);
        }else{
            Toast.makeText(MainActivity.this,"没有保存记录，来一局新游戏吧",Toast.LENGTH_SHORT).show();
        }
    }

    //设置
    public void settings(View view){
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }
}