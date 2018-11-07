package com.mingrisoft.sockword2;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Switch;

/**
 * Created by MSI on 18/11/06.
 */

public class SwitchButton extends FrameLayout{
    private ImageView openImage;
    private ImageView closeImage;
    public SwitchButton(Context context){
        this(context,null);
    }
    /**
     * 构造方法
     */
    public SwitchButton(Context context, AttributeSet attrs,int defStyleAttr) {
        this(context,attrs);
    }
    public SwitchButton(Context context, AttributeSet attrs) {
        super(context,attrs);
        /**
         * context通过调用obtainStyledAttributes方法获取一个Type Array，然后由TypeArray
         * 对属性进行设置
         */
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.SwitchButton);
        //画出开关为打开时的状态
        Drawable openDrawable = typedArray.getDrawable(R.styleable.SwitchButton_switchOpenImage);
        Drawable closeDrawable = typedArray.getDrawable(R.styleable.SwitchButton_swithCloseImage);

        int switchStatus = typedArray.getInt(R.styleable.SwitchButton_switchStatus,0);

        //调用结束后务必调用recycle()方法，否则这次设定会对下次设定有影响
        typedArray.recycle();

        LayoutInflater.from(context).inflate(R.layout.switch_button,this);

        openImage  = (ImageView)findViewById(R.id.iv_switch_open);
        closeImage = (ImageView)findViewById(R.id.iv_switch_close);

        if(openDrawable!=null){
            openImage.setImageDrawable(openDrawable);
        }
        if(closeDrawable!=null){
            closeImage.setImageDrawable(closeDrawable);
        }
        if(switchStatus == 1){
            closeSwitch();
        }


    }
    public boolean isSwitchOpen(){
        return openImage.getVisibility() == View.VISIBLE;
    }
    public boolean openSwitch(){
        openImage.setVisibility(View.VISIBLE);     //显示打开开关
        closeImage.setVisibility(View.INVISIBLE);  //隐藏关闭开关
        return true;
    }
    public boolean closeSwitch() {     //关闭按钮
        //隐藏打开开关
        openImage.setVisibility(View.INVISIBLE);
        closeImage.setVisibility(View.VISIBLE);
        //显示关闭开关
        return true;
    }

}
