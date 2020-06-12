package com.example.game2048.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.game2048.R;
import com.example.game2048.listen.ScoreChangeListen;
import com.example.game2048.view.GameView;

public class GameActivity extends AppCompatActivity {

    private GameView gameView;

    private TextView textNowScore;

    private TextView textHighestScore;

    private ScoreChangeListen scoreChangeListen;

    private SharedPreferences.Editor gsEditor;

    //历史最高分
    private int highestScore;

    //实现“再点一次返回键推出程序”效果
    private boolean isExit = false;

    private boolean flag;

    private int temp;

    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            isExit = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        int Row = intent.getIntExtra("Row", 4);
        if (Row == 4){
            setContentView(R.layout.activity_four);
        }else if (Row == 5){
            setContentView(R.layout.activity_five);
        }else{
            setContentView(R.layout.activity_six);
        }
        init();
        //判断是否需要恢复历史记录
        if (intent.getBooleanExtra("RecoverGame", false)){
            gameView.recoverGame();
        }
    }

    private void init() {
        gameView = (GameView) findViewById(R.id.gameView_four);
        textNowScore = (TextView) findViewById(R.id.nowScore);
        textHighestScore = (TextView) findViewById(R.id.highestScore);
        TextView textRestart = (TextView) findViewById(R.id.restart);
        TextView textSaveGame = (TextView) findViewById(R.id.save_game);
        SharedPreferences gameSettings = getSharedPreferences("GameSettings", Context.MODE_PRIVATE);
        gsEditor = gameSettings.edit();
        highestScore = gameSettings.getInt("HighestScore", 0);
        textNowScore.setText("当前得分：\n" + 0);
        textHighestScore.setText("最高得分：\n" + highestScore);
        flag = true;
        int themeIndex = gameSettings.getInt("ThemeIndex", 1);
        LinearLayout rootLayout = (LinearLayout) findViewById(R.id.rootLayout);
        int resource = 0;
        switch (themeIndex) {
            case 1:
                resource = R.drawable.back1;
                break;
            case 2:
                resource = R.drawable.back2;
                break;
            case 3:
                resource = R.drawable.back3;
                break;
            case 4:
                resource = R.drawable.back4;
                break;
            case 5:
                resource = R.drawable.back5;
                break;
            case 6:
                resource = R.drawable.back6;
                break;
        }
        if (rootLayout != null && resource != 0){
            rootLayout.setBackgroundResource(resource);
        }
        //重新开始游戏
        textRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
                builder.setMessage("确认重新开始游戏吗？");
                builder.setTitle("提示");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gameView.restart();
                        textNowScore.setText("当前得分\n" + 0);
                        if (temp != 0){
                            scoreChangeListen.OnHighestScoreChange(temp);
                            highestScore = temp;
                            flag = true;
                        }
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.create().show();
            }
        });
        //保存游戏
        textSaveGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
                builder.setMessage("确认保存游戏吗？");
                builder.setTitle("提示");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gameView.saveGame();
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.create().show();
            }
        });
        scoreChangeListen = new ScoreChangeListen() {
            @Override
            public void OnNowScoreChange(int Score) {
                textNowScore.setText("当前得分\n" + Score);
                if (Score > highestScore){
                    if (flag && highestScore != 0){
                        Toast.makeText(GameActivity.this,"打破最高记录啦，请继续保持",Toast.LENGTH_LONG).show();
                        flag = false;
                    }
                    temp = Score;
                    textHighestScore.setText("最高得分\n" + temp);
                }
            }

            @Override
            public void OnHighestScoreChange(int Score) {
                gsEditor.putInt("HighestScore", Score);
                gsEditor.apply();
            }
        };
        gameView.scoreChangeListen = scoreChangeListen;
    }
    //重写返回键监听事件
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK){
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (!isExit){
            isExit = true;
            if (gameView.isHalfWay){
                Toast.makeText(this, "再按一次结束游戏,建议保存游戏", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "再按一次结束游戏", Toast.LENGTH_SHORT).show();
            }
            //利用handler延迟发送改变状态信息
            mHandler.sendEmptyMessageDelayed(0, 2000);
        }else {
            finish();
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if (temp != 0){
            scoreChangeListen.OnHighestScoreChange(temp);
        }
        gameView.soundPool.release();
    }
}