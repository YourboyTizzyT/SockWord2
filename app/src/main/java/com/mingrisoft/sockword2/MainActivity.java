package com.mingrisoft.sockword2;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assetsbasedata.AssetsDatabaseManager;
import com.iflytek.cloud.speech.SpeechConstant;
import com.iflytek.cloud.speech.SpeechError;
import com.iflytek.cloud.speech.SpeechListener;
import com.iflytek.cloud.speech.SpeechSynthesizer;
import com.iflytek.cloud.speech.SpeechUser;
import com.iflytek.cloud.speech.SynthesizerListener;
import com.mingrisoft.greendao.entity.greendao.CET4Entity;
import com.mingrisoft.greendao.entity.greendao.CET4EntityDao;
import com.mingrisoft.greendao.entity.greendao.DaoMaster;
import com.mingrisoft.greendao.entity.greendao.DaoSession;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, SynthesizerListener {

    //用来显示单词和音标的
    private TextView  timeText,dateText,wordText,englishText;
    private ImageView playVioce;
    private String mMonth,mDay,mWay,mHours,mMinute; //用来显示时间
    private SpeechSynthesizer speechSynthesizer;    //合成对象

    //锁屏
    private KeyguardManager km;
    private KeyguardManager.KeyguardLock kl;
    private RadioGroup radioGroup;
    private RadioButton radioOne,radioTwo,radioThree,radioFour;  //单词意思的三个选项
    private SharedPreferences sharedPreferences;       //定义轻量级数据库
    SharedPreferences.Editor editor = null;            //编辑数据库

    int j = 0;                                         //用于记录答了几道题
    List<Integer> list;                                //用于从数据库读取相应的词库
    List<CET4Entity> datas;
    int k;

    /**
     * 手指按下时的位置坐标（x1，y1）
     * 手指离开时的位置坐标（x2，y2）
     */
    float x1=0,y1=0,x2=0,y2=0;
    private SQLiteDatabase  db;                        //创建数据库
    private DaoMaster mDaoMaster,dbMaster;             //管理者
    private DaoSession mDaoSession,dbSession;          //和数据库进行会话

    //对应的表，由Java代码生成的，对数据库内相应的表操作使用此对象
    private CET4EntityDao questionDao,dbDao;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //将锁屏页面显示到手机屏幕的最上层
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        //初始化数据库、控件等模块
        init();


    }

    /**
     * 初始化控件：
     * 1.初始化轻量级数据库
     * 2.添加10个10以内的随机数
     * 3.得到键盘锁管理对象
     * 4.初始化GreenDao数据库
     * 5.初始化控件
     * 6.定语音合成器id与调用语音初始化的方法
     */
    private void init(){
        /**
         * 初始化轻量级数据库
         */
        sharedPreferences = getSharedPreferences("share", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();  //初始化轻量级数据库编辑器
        //给播放单词语音的设置个appid（这个要到讯飞平台去进行申请，详情请参考讯飞官网）
        list = new ArrayList<Integer>();   //初始化list
        /**
         * 添加10个20以内的随机数
         */
        Random r = new Random();
        int i;
        while(list.size()<10){
            i = r.nextInt(20);
            if(!list.contains(i)){
                list.add(i);
            }
        }
        /**
         * 打印list
         */
        for(int tt = 0;tt<list.size();tt++){
            Log.v("list",list.toString());
        }
        /**
         * 得到键盘锁管理对象
         */
        km = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
        kl = km.newKeyguardLock("unlock");
        //初始化，只需要调用一次
        AssetsDatabaseManager.initManager(this);
        //获取管理对象，因为数据库需要通过管理对象才能获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        //通过管理对象获取数据库
        SQLiteDatabase db1 = mg.getDatabase("word.db");
        //对数据库进行操作
        mDaoMaster = new DaoMaster(db1);
        mDaoSession =  mDaoMaster.newSession();
        questionDao = mDaoSession.getCET4EntityDao();
        /**
         * 此DevOpenHelper类继承自SQLiteOpenHelper，
         * 第一个参数Context，第二个参数数据库名字，第三个参数CursorFactory
         */
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this,"wrong.db",null);
        /**
         * 初始化数据库
         */
        db = helper.getWritableDatabase();
        dbMaster = new DaoMaster(db);
        dbSession = dbMaster.newSession();
        dbDao = dbSession.getCET4EntityDao();
        /**
         * 控件初始化
         */
        //用于显示分钟绑定id
        timeText = (TextView)findViewById(R.id.time_text);
        dateText = (TextView)findViewById(R.id.date_text);
        wordText = (TextView)findViewById(R.id.word_text);
        englishText = (TextView)findViewById(R.id.english_text);
        playVioce = (ImageView)findViewById(R.id.play_vioce);
        playVioce.setOnClickListener(this);
        radioGroup = (RadioGroup)findViewById(R.id.choose_group);
        radioOne  = (RadioButton)findViewById(R.id.choose_btn_one);
        radioTwo  = (RadioButton)findViewById(R.id.choose_btn_two);
        radioThree  = (RadioButton)findViewById(R.id.choose_btn_three);
        radioFour  = (RadioButton)findViewById(R.id.choose_btn_four);

        radioGroup.setOnCheckedChangeListener(this);

        //绑定语音合成器的id与调用语音初始化的方法
        setParam();
        //appid换成自己申请的，播放语音
        SpeechUser.getUser().login(MainActivity.this,null,null,"appid=573a7bf0",listener);

    }

    /**
     * 初始化语音播报
     */
    public void setParam(){
        speechSynthesizer = SpeechSynthesizer.createSynthesizer(this);
        speechSynthesizer.setParameter(SpeechConstant.VOICE_NAME,"xiaoyan");
        speechSynthesizer.setParameter(SpeechConstant.SPEED,"50");
        speechSynthesizer.setParameter(SpeechConstant.VOLUME,"50");
        speechSynthesizer.setParameter(SpeechConstant.PITCH,"50");
    }

    /**
     * 喇叭按钮事件响应，
     * 点击喇叭图标发音
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.play_vioce:
                String text = wordText.getText().toString();   //把单词提取出来
                speechSynthesizer.startSpeaking(text,this);
                Log.v("voice","点击了喇叭");
        }
    }

    /**
     * 词义选项控件触发事件，词义选项点击事件
     * @param group
     * @param checkedId
     */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        radioGroup.setClickable(false);                      //默认选项未被选中
        switch (checkedId) {
            case R.id.choose_btn_one:
                String msg =radioOne.getText().toString().substring(3);
                Log.v("voice","点击了按钮1");
                btnGetText(msg,radioOne);
                break;
            case R.id.choose_btn_two:
                String msg1 = radioTwo.getText().toString().substring(3);
                Log.v("voice","点击了按钮2");
                btnGetText(msg1,radioTwo);
                break;
            case R.id.choose_btn_three:
                String msg3 = radioThree.getText().toString().substring(3);
                Log.v("voice","点击了按钮3");
                btnGetText(msg3,radioThree);
                break;
            case R.id.choose_btn_four:
                String msg4 = radioFour.getText().toString().substring(3);
                Log.v("voice","点击了按钮4");
                btnGetText(msg4,radioFour);
                break;
        }
    }

    /**
     * 获取数据库文件
     * 还原单词与选项颜色
     */

    private void setTextColor(){
        //还原单词选项的颜色
        radioOne.setChecked(false);
        radioTwo.setChecked(false);
        radioThree.setChecked(false);
        /**将选项的按钮设置为白色*/
        radioOne.setTextColor(Color.parseColor("#FFFFFF"));
        radioTwo.setTextColor(Color.parseColor("#FFFFFF"));
        radioThree.setTextColor(Color.parseColor("#FFFFFF"));
        radioFour.setTextColor(Color.parseColor("#FFFFFF"));
        wordText.setTextColor(Color.parseColor("#FFFFFF"));              //将单词设置为白色
        englishText.setTextColor(Color.parseColor("#FFFFFF"));           //将音标设置为白色

    }

    /**
     * 解锁方法
     */
    private void  unlock(){
        Intent intent1 = new Intent(Intent.ACTION_MAIN);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent1.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent1);
        kl.disableKeyguard();
        finish();                    //销毁当前activity

    }

    /**
     * 获取数据库数据，设置中英文，选项等内容
     */
    private void getDBData(){
        datas  = questionDao.queryBuilder().list();   //把词库里边的单词读取出来
        /**
         * 设置英文单词
         */
        k = list.get(j);                              //list是一个随机数列，取出第j个随机数
        wordText.setText(datas.get(k).getWord());     //设置单词
        englishText.setText(datas.get(k).getEnglish()); //设置音标
        setStudyContents(datas,k,list);
    }

    private void setStudyContents(List<CET4Entity> datas,int k,List<Integer> list) {
        //1.设置单词内容
        wordText.setText(datas.get(k).getWord());
        //2.设置音标
        englishText.setText(datas.get(k).getEnglish());
        //3.设置选项
        Random r = new Random();   //产生新的随机数
        List<Integer>RandomInt = new ArrayList<>();
        int i;

        //设置四个选项
        //随机产生4个不重复的随机数,且这几个数都不等于k
       while(4>RandomInt.size()){
            i = r.nextInt(20);
            if(!RandomInt.contains(i))
                if(i!=k)
             RandomInt.add(i);
        }

        Log.v("Random","K ="+Integer.toString(k)+RandomInt.toString()+datas.get(k).getWord());
        //根据第一个随机数判断选项的位置
        int wordLocation = RandomInt.get(0);
        if(0<=wordLocation&&5>wordLocation){
            radioOne.setText("A: "+datas.get(k).getChina());   //设置A为正确答案
            radioTwo.setText("B: "+datas.get(RandomInt.get(1)).getChina());
            radioThree.setText("C: "+datas.get(RandomInt.get(2)).getChina());
            radioFour.setText("D: "+datas.get(RandomInt.get(3)).getChina());
        }else if(10>wordLocation){
            radioOne.setText("A: "+datas.get(RandomInt.get(1)).getChina());
            radioTwo.setText("B: "+datas.get(k).getChina());   //设置B为正确答案
            radioThree.setText("C: "+datas.get(RandomInt.get(2)).getChina());
            radioFour.setText("D: "+datas.get(RandomInt.get(3)).getChina());
        }else if(15>wordLocation){
            radioOne.setText("A: "+datas.get(RandomInt.get(1)).getChina());
            radioTwo.setText("B: "+datas.get(RandomInt.get(2)).getChina());
            radioThree.setText("C: "+datas.get(k).getChina());   //设置C为正确答案
            radioFour.setText("D: "+datas.get(RandomInt.get(3)).getChina());
        }else if(20>wordLocation){
            radioOne.setText("A: "+datas.get(RandomInt.get(1)).getChina());
            radioTwo.setText("B: "+datas.get(RandomInt.get(2)).getChina());
            radioThree.setText("C: "+datas.get(RandomInt.get(3)).getChina());
            radioFour.setText("D: "+datas.get(k).getChina());   //设置D为正确答案
        }


    }

    /**
     * 手势滑动事件
     * 获取手势坐标点（x1，y1）、（x2，y2）
     */
    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            //当手指按下时坐标（x,y）
            x1 = event.getX();
            y1 = event.getY();
        }
        if(event.getAction() == MotionEvent.ACTION_UP){
            x2 = event.getX();
            y2 = event.getY();
            if((y1-y2)>200){     //向上划
                //已掌握的单词数量加1
                int num = sharedPreferences.getInt("alreadyMaster",0)+1;
                editor.putInt("alreadyMaster",num);                                  //输入数据库
                editor.commit();                                                     //保存

            }else if((y2-y1)>200){  //向下划
                Toast.makeText(this,"待加功能",Toast.LENGTH_SHORT).show();
            }else if((x1 -x2 )> 200){       //向左滑
                getNextData();
            }else if((x2 - x1 )> 200){       //右滑解锁
                unlock();
            }
        }

        return super.onTouchEvent(event);
    }
    /**
     * 获取下一题
     */
    private void getNextData(){
        j++;
        int i = sharedPreferences.getInt("allNum",2);
        if(i>j){
            getDBData();                        //获取数据
            setTextColor();                     //设置颜色
            //已经学习的单词数加1
            int num = sharedPreferences.getInt("alreadyStudy",0)+1;
            editor.putInt("alreadyStudy",num);
            editor.commit();                     //存到数据库里边
        }else {
            unlock();                            //解锁
        }
    }

    @Override
    public void onSpeakBegin() {

    }

    @Override
    public void onBufferProgress(int i, int i1, int i2, String s) {

    }

    @Override
    public void onSpeakPaused() {

    }

    @Override
    public void onSpeakResumed() {

    }

    @Override
    public void onSpeakProgress(int i, int i1, int i2) {

    }

    @Override
    public void onCompleted(SpeechError speechError) {

    }
    /**
     * 通用回调接口
     */
    private SpeechListener listener = new SpeechListener() {
        @Override
        public void onEvent(int i, Bundle bundle) {

        }
        //数据回调
        @Override
        public void onData(byte[] bytes) {

        }

        @Override
        public void onCompleted(SpeechError speechError) {

        }
    };
    /**
     * 获取系统时间，并设置将其显示出来
     */
    protected void onStart(){
        super.onStart();

        Calendar calendar = Calendar.getInstance();
        mMonth = String.valueOf(calendar.get(Calendar.MONTH)+1);    //获取日期的月
        mDay = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)); //获取日期的日
        mWay = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));  //获取日期的星期
        /**
         * 如果小时是个位数
         * 在前面加一个0
         */
        if(calendar.get(Calendar.HOUR)<10){
            mHours = "0"+calendar.get(Calendar.HOUR);
        }else{
            mHours = String.valueOf(calendar.get(Calendar.HOUR));
        }
        /**
         * 如果分钟是个位数
         * 在前面加一个0
         */
        if(calendar.get(Calendar.MINUTE)<10){
            mMinute = "0"+calendar.get(Calendar.MINUTE);
        }else{
            mMinute = String.valueOf(calendar.get(Calendar.MINUTE));
        }
        /**
         * 获取星期
         * 并设置出来
         */
        if("1".equals(mDay)){
            mWay = "天";
        }else if("2".equals(mDay)){
            mWay = "一";
        }else if("3".equals(mDay)){
            mWay = "二";
        }else if("4".equals(mDay)){
            mWay = "三";
        }else if("5".equals(mDay)){
            mWay = "四";
        }else if("6".equals(mDay)){
            mWay = "五";
        }else if("7".equals(mDay)){
            mWay = "六";
        }

        //timeText.setText(mHours+":"+mMinute);
        //dateText.setText(mMonth+"月"+mDay+"日"+"  "+"星期"+mWay);

        timeText.setText(mHours + ":" + mMinute);
        dateText.setText(mMonth + "月" + mDay + "日" + "    " + "星期" + mWay);
        BaseApplication.addDestroyActivity(this,"mainActivity");
        getDBData();
    }

    /**
     * 把记错的单词存放到数据库中
     */
    private void saveWrongData(){
        String word = datas.get(k).getWord();                //获取答错这道题的单词
        String english = datas.get(k).getEnglish();          //获取答错这道题的音标
        String china = datas.get(k).getChina();              //获取答错这道题的汉语意思
        String sign = datas.get(k).getSign();                //获取答错这道题的标记

        CET4Entity data = new CET4Entity(Long.valueOf(dbDao.count()),
                word,english,china,sign);  //把这些数据存入数据库
        dbDao.insertOrReplace(data);                          //把这些字段存入数据库
    }
    /**
     * 答对设置绿色，答错设置红色
     */
    private void btnGetText(String msg,RadioButton btn){

        if(msg.equals(datas.get(k).getChina())){
            wordText.setTextColor(Color.GREEN);
            englishText.setTextColor(Color.GREEN);
            btn.setTextColor(Color.GREEN);
        }else{
            wordText.setTextColor(Color.RED);
            englishText.setTextColor(Color.RED);
            btn.setTextColor(Color.RED);
            saveWrongData();  //保存到数据库

            int wrong = sharedPreferences.getInt("wrong",0);
            editor.putInt("wrong",wrong+1);
            editor.putString("wrongId",","+datas.get(j).getId());
            editor.commit();                          //保存
        }
    }

}
