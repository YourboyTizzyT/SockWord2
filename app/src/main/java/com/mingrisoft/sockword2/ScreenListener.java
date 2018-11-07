package com.mingrisoft.sockword2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;

/**
 * Created by MSI on 18/11/07.
 */

public class ScreenListener {

    //1.初始化广播和接口
    private Context context;
    private ScreenBroadReceiver mScreenReceiver;
    private ScreenStateListener mScreenStateListener;

    /**
     * 初始化
     */
    public ScreenListener(Context context){
        this.context = context;
        mScreenReceiver = new ScreenBroadReceiver();
    }
    //2.自定义接口
    public interface ScreenStateListener{
        void onScreenOn();       //手机屏幕点亮
        void onScreenOff();      //手机屏幕关闭
        void onUserPresent();    //手机屏幕解锁
    }
    //3.获取屏幕状态
    private void getScreenState(){
        //初始化powerManager
        PowerManager manager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if(manager.isScreenOn()){
            if(mScreenStateListener!=null){
                mScreenStateListener.onScreenOn();
            }
        }else {
            if(mScreenStateListener!=null){
                mScreenStateListener.onScreenOff();
            }
        }
    }
    //4.屏幕监听广播
    /**
     * 写一个内部广播
     */
    private class ScreenBroadReceiver extends BroadcastReceiver{

        private String action = null;
        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();
            if(Intent.ACTION_SCREEN_ON.equals(action)){
                mScreenStateListener.onScreenOn();
            }else if(Intent.ACTION_SCREEN_OFF.equals(action)){
                mScreenStateListener.onScreenOff();
            }else if(Intent.ACTION_USER_PRESENT.equals(action)){
                mScreenStateListener.onUserPresent();
            }
        }
    }
    /**
     * 开始监听广播状态
     */
    public void begin(ScreenStateListener listener){
        mScreenStateListener = listener;
        registerListener();
        getScreenState();
    }

    private void registerListener() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        context.registerReceiver(mScreenReceiver,filter);
    }

    public void unregisterListener (){
        context.unregisterReceiver(mScreenReceiver);
    }
}
