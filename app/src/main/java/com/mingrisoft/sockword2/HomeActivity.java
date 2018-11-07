package com.mingrisoft.sockword2;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by MSI on 18/11/07.
 */

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    private ScreenListener screenListener;        //绑定此界面与手机屏幕状态的监听
    private SharedPreferences sharedPreferences;  //加载一个轻量级数据库
    private FragmentTransaction transaction;     //定义用于加载复习与设置的界面

    private StudyFragment studyFragment;
    private SetFragment setFragment;
    private Button wrongBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.home_layout);
        init();
    }
    /**
     * 初始化控件
     */
    private void init() {
        sharedPreferences = getSharedPreferences("share", Context.MODE_PRIVATE);
        wrongBtn = (Button)findViewById(R.id.wrong_btn);
        wrongBtn.setOnClickListener(this);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        screenListener = new ScreenListener(this);
        screenListener.begin(new ScreenListener.ScreenStateListener() {
            @Override
            public void onScreenOn() {
                if(sharedPreferences.getBoolean("btnTf",false)){
                    //判断屏幕是否解锁
                    if(sharedPreferences.getBoolean("tf",false)){
                        Intent intent = new Intent(HomeActivity.this,MainActivity.class);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onScreenOff() {
                /**
                 * 如果手机解锁了就把数据库里边的tf字段改成true
                 */
                editor.putBoolean("tf",true);
                editor.commit();
                //销毁锁屏界面
                BaseApplication.destroyAcitivity("mainActivity");

            }

            @Override
            public void onUserPresent() {
                /**
                 * 如果手机已经解锁
                 * 就把数据库里边的tf字段改成false
                 */
                editor.putBoolean("tf",false);
                editor.commit();

            }
        });
        studyFragment  = new StudyFragment();
        setFragment(studyFragment);
    }

    public void setFragment(Fragment fragment) {
        transaction = getFragmentManager().beginTransaction();
        //初始化transaction
        transaction.replace(R.id.frame_layout,fragment);
        transaction.commit();
    }
    public void study(View v){
        if(studyFragment == null){
            studyFragment = new StudyFragment();
        }
        setFragment(studyFragment);
    }
    public void set(View v){
        if(setFragment == null){
            setFragment = new SetFragment();
        }
        setFragment(setFragment);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.wrong_btn:
                Toast.makeText(this,"跳转到错题界面",Toast.LENGTH_SHORT).show();
                break;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        screenListener.unregisterListener();
    }
}
