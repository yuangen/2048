package com.example.game2048.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.GridLayout;
import android.widget.Toast;

import com.example.game2048.listen.ScoreChangeListen;
import com.example.game2048.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends GridLayout {

    private String TAG = "-----------myView";

    //存储所有方块
    private Card[][] Cards;

    //当前游戏行数与列数
    private int Row;

    //游戏记录
    private SharedPreferences gameRecord;

    private SharedPreferences.Editor gsEditor;

    public ScoreChangeListen scoreChangeListen = null;

    private Context context;

    //当前得分

    private int Score;

    public SoundPool soundPool;

    private int soundID;

    private boolean soundSwitch;

    private class Point{
        int x;
        int y;
        public Point(int x, int y){
            this.x = x;
            this.y = y;
        }
    }

    //不会自动调用，如果有默认style时，在第二个构造函数中调用
    public GameView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        this.context = context;
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.ViewSet);
        Row = mTypedArray.getInt(R.styleable.ViewSet_Row, 4);
        mTypedArray.recycle();
        super.setColumnCount(Row);
        init();
    }
    //在xml布局文件中自动调用
    public GameView(Context context, AttributeSet attrs){
        super(context, attrs);
        this.context = context;
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.ViewSet);
        Row = mTypedArray.getInt(R.styleable.ViewSet_Row, 4);
        mTypedArray.recycle();
        super.setColumnCount(Row);
        init();
    }

    //在java代码中new的时候会用到
    public GameView(Context context){
        super(context);
        this.context = context;
        init();
    }

    private void init() {
        gameRecord = context.getSharedPreferences("GameRecord", Context.MODE_PRIVATE);
        SharedPreferences GameSettings = context.getSharedPreferences("GameSttings",Context.MODE_PRIVATE);
        boolean flag = GameSettings.getBoolean("SolidColorSwitch",false);
        soundSwitch = GameSettings.getBoolean("SoundSwitch", false);
        //SoundPool的构建方法在5.0后发生了变化
        if(Build.VERSION.SDK_INT < 21){
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }else{
            SoundPool.Builder builder = new SoundPool.Builder();
            builder.setMaxStreams(1);
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
            builder.setAudioAttributes(attrBuilder.build());
            soundPool = builder.build();
        }
        soundID = soundPool.load(context, R.raw.sound, 1);
        gsEditor = gameRecord.edit();
        Cards = new Card[Row][Row];
        addCard(GetCardWidth(Row));
        for (int y = 0; y < Row; y++){
            for (int x = 0; x <Row; x++){
//                Cards[x][y] = new Card(context);
                Cards[x][y].flag = flag;
            }
        }
        //添加两个初始方块
        randomCard();
        randomCard();
    }
    //获得系统的长宽
    private int GetCardWidth(int Row){
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int cardwidth = displayMetrics.widthPixels;
        return (cardwidth - 5) / Row;
    }

    //生成伪随机方块
    private void randomCard() {
        List<Point> points = new ArrayList<>();
        for (int x = 0; x < Row; x++) {
            for (int y = 0; y < Row; y++) {
                // 如果还有空白方块
                if (Cards[x][y].getNum() == 0) {
                    points.add(new Point(x, y));
                }
            }
        }
        if (points.size() == 0) {
            return;
        }
        Random random = new Random();
        int index = random.nextInt(20) % (points.size());
        Cards[points.get(index).x][points.get(index).y].setNum(2);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH){
        super.onSizeChanged(w, h, oldW, oldH);
//        int cardWidth = (Math.min(w, h) - 5) / Row;
    }


    //添加方块
    private void addCard(int cardWith){
        Card c;
        for (int y = 0; y < Row; y++){
            for(int x = 0; x < Row; x++){
                c = new Card(getContext());
                c.setNum(0);
                addView(c,cardWith,cardWith);
                Cards[x][y] = c;
            }
        }
    }
    //计算分数
    private void countScore(int num){
        Score = Score + num;
        if (scoreChangeListen != null){
            scoreChangeListen.OnNowScoreChange(Score);
            if(soundSwitch){
                soundPool.play(soundID, 1, 1, 0, 0, 1);
            }
        }
    }
    //左移
    private void moveLeftCrad(){
        allMoveLeft();
        for (int y = 0; y < Row; y++){
            for (int x = 0; x < Row - 1; x++){
                if (Cards[x][y].getNum() != 0){
                    if (Cards[x][y].equals(Cards[x+1][y])){
                        int num = Cards[x][y].getNum();
                        Cards[x][y].setNum(2 * num);
                        Cards[x + 1][y].setNum(0);
                        countScore(num);
                        allMoveLeft();
                    }
                }
            }
        }
        randomCard();
    }
    //右移
    private void moveRightCrad(){
        allMoveRight();
        for (int y = 0; y < Row; y++) {
            for (int x = Row - 1; x > 0; x--) {
                if (Cards[x][y].getNum() != 0) {
                    if (Cards[x][y].equals(Cards[x - 1][y])) {
                        int num = Cards[x][y].getNum();
                        Cards[x][y].setNum(2 * num);
                        Cards[x - 1][y].setNum(0);
                        countScore(num);
                        allMoveRight();
                    }
                }
            }
        }
        randomCard();
    }
    //上移
    private void moveUpCard(){
        allMoveUp();
        for (int x = 0; x < Row; x++) {
            for (int y = 0; y < Row - 1; y++) {
                if (Cards[x][y].getNum() != 0) {
                    if (Cards[x][y].equals(Cards[x][y + 1])) {
                        int num = Cards[x][y].getNum();
                        Cards[x][y].setNum(2 * num);
                        Cards[x][y + 1].setNum(0);
                        countScore(num);
                        allMoveUp();
                    }
                }
            }
        }
        randomCard();
    }
    //下移
    private void moveDownCrad(){
        allMoveDown();
        for (int x = 0; x < Row; x++) {
            for (int y = Row - 1; y > 0; y--) {
                if (Cards[x][y].getNum() != 0) {
                    if (Cards[x][y].equals(Cards[x][y - 1])) {
                        int num = Cards[x][y].getNum();
                        Cards[x][y].setNum(2 * num);
                        Cards[x][y - 1].setNum(0);
                        countScore(num);
                        allMoveDown();
                    }
                }
            }
        }
        randomCard();

    }
    //全部左移
    private void allMoveLeft(){
        for (int y = 0; y < Row; y++){
            int i = 0;
            for (int x = 0; x < Row; x++){
                if (Cards[x][y].getNum() != 0){
                    int num = Cards[x][y].getNum();
                    Cards[x][y].setNum(0);
                    Cards[i++][y].setNum(num);
                }
            }
        }
    }
    //全部右移
    private void allMoveRight(){
        for (int y = 0; y < Row; y++){
            int i = Row - 1;
            for (int x = Row - 1; x >= 0; x--){
                if (Cards[x][y].getNum() != 0){
                    int num = Cards[x][y].getNum();
                    Cards[x][y].setNum(0);
                    Cards[i--][y].setNum(num);
                }
            }
        }
    }
    //全部上移
    private void allMoveUp(){
        for (int x = 0; x < Row; x++){
            int i = 0;
            for (int y = 0; y < Row; y++){
                if (Cards[x][y].getNum() != 0){
                    int num = Cards[x][y].getNum();
                    Cards[x][y].setNum(0);
                    Cards[x][i++].setNum(num);
                }
            }
        }
    }
    //全部下移
    private void allMoveDown(){
        for (int x = Row - 1; x >= 0; x--){
            int i = Row - 1;
            for (int y = Row - 1; y >= 0; y--){
                if (Cards[x][y].getNum() != 0){
                    int num = Cards[x][y].getNum();
                    Cards[x][y].setNum(0);
                    Cards[x][i--].setNum(num);
                }
            }
        }
    }
    //触屏事件监听
    float X;
    float Y;
    float OffsetX;
    float OffsetY;
    int HintCount = 0;
    public boolean isHalfWay = true;
    public boolean onTouchEvent(MotionEvent event){
        if(HintCount == 1){
            return true;
        }
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                X = event.getX();
                Y = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                OffsetX = event.getX() - X;
                OffsetY = event.getY() - Y;
                if (Math.abs(OffsetX) > Math.abs(OffsetY)){
                    if (OffsetX < -5){
                        moveLeftCrad();
                    }else if (OffsetX > 5){
                        moveRightCrad();
                    }
                }else{
                    if (OffsetY < -5){
                        moveUpCard();
                    }else if (OffsetY > 5){
                        moveDownCrad();
                    }
                }
                HintMessage();
                break;
        }
        return true;
    }
    //判断游戏是否结束
    private boolean isOver(){
        for (int y = 0; y < Row;y++){
            for (int x = 0; x < Row; x++){
                if ((Cards[x][y].getNum() == 0) || (x - 1 >= 0 && Cards[x - 1][y].equals(Cards[x][y]))
                        || (x + 1 <= Row - 1 && Cards[x + 1][y].equals(Cards[x][y]))
                        || (y - 1 >= 0 && Cards[x][y - 1].equals(Cards[x][y]))
                        || (y + 1 <= Row - 1 && Cards[x][y + 1].equals(Cards[x][y]))) {
                    return false;
                }
            }
        }
        return true;
    }
    //当游戏结束时提示信息
    private void HintMessage(){
        if (isOver()){
            Toast.makeText(getContext(), "游戏结束啦",Toast.LENGTH_SHORT).show();
            HintCount = 1;
        }
    }
    //重新开始
    public void restart(){
        for (int y = 0; y < Row; y++){
            for(int x = 0; x < Row; x++){
                Cards[x][y].setNum(0);
            }
        }
        Score = 0;
        HintCount = 0;
        randomCard();
        randomCard();
    }
    //保存游戏
    public void saveGame(){
        gsEditor.clear();
        gsEditor.putInt("Row", Row);
        gsEditor.putInt("Score", Score);
        int k = 0;
        for (int i = 0; i < Row; i++){
            for (int j = 0; j < Row; j++){
                k++;
                String str = k + "";
                gsEditor.putInt(str, Cards[i][j].getNum());
            }
        }
        if (gsEditor.commit()){
            Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "保存失败,请重试", Toast.LENGTH_SHORT).show();
        }
    }
    //恢复游戏
    public void recoverGame(){
        int k = 0;
        for (int i = 0; i < Row; i++){
            for (int j = 0; j < Row; j++){
                int num = gameRecord.getInt(String.valueOf(k++), 0);
                Cards[i][j].setNum(num);
            }
        }
        Score = gameRecord.getInt("Score", 0);
        scoreChangeListen.OnNowScoreChange(Score);
    }

}
