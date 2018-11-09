package com.mingrisoft.sockword2;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public class SetManager extends AppCompatActivity implements View.OnClickListener{

    private SharedPreferences sharedPreferences;
    private Spinner spinnerDifficulty;
    private Spinner spinnerALLNum;
    private Spinner spinnerNewNum;
    private Spinner spinnerReviewNum;
    private SwitchButton switchButton;
    private ArrayAdapter<String> adapterDifficulty,adapterAllNum,adapterNewNum,adapterReviewNum;

    //选择难度下拉框的选择内容
    String[] difficulty = new String[]{"小学","初中","高中","四级","六级"};
    //解锁题目下拉框的选择内容
    String[] allNum = new String[]{"2道","4道","6道","8道"};
    //新题目下拉框的选择内容
    String[] newNum = new String[]{"10","30","50","100"};
    //复习题目下拉菜单
    String[] reviewNum = new String[]{"10","30","50","100"};

    SharedPreferences.Editor editor = null;
    /**
     * 绑定设置界面的布局
     * 由于设置界面为Fragment，与Activities的加载方式有所不同
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_manager);
        init();
    }

    private void init() {
        sharedPreferences = getSharedPreferences("share", Context.MODE_PRIVATE);//初始化数据库
        editor = sharedPreferences.edit();
        //开关按钮绑定id
        switchButton = (SwitchButton)findViewById(R.id.switch_btn);
        switchButton.setOnClickListener(this);
        spinnerDifficulty = (Spinner)findViewById(R.id.spinner_difficulty);
        spinnerALLNum = (Spinner)findViewById(R.id.spinner_all_number);
        spinnerNewNum = (Spinner)findViewById(R.id.spinner_new_number);
        spinnerReviewNum = (Spinner)findViewById(R.id.spinner_revise_number);


        /**
         * 获取难度选项***********************************************************************************************
         */
        adapterDifficulty = new ArrayAdapter<String>(this,android.R.layout.simple_selectable_list_item,difficulty);
        spinnerDifficulty.setAdapter(adapterDifficulty);
        setSpinnerItemSelectdByValue(spinnerDifficulty,sharedPreferences.getString("difficulty","四级"));
        this.spinnerDifficulty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String msg = parent.getItemAtPosition(position).toString();
                editor.putString("difficulty",msg);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        /**
         * 获取题目数选项***********************************************************************************************
         */
        adapterAllNum = new ArrayAdapter<String>(this,android.R.layout.simple_selectable_list_item,allNum);
        spinnerALLNum.setAdapter(adapterAllNum);
        setSpinnerItemSelectdByValue(spinnerALLNum,sharedPreferences.getInt("allNum",2)+"道");
        this.spinnerALLNum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String msg = parent.getItemAtPosition(position).toString();
                int i  = Integer.parseInt(msg.substring(0,1));
                editor.putInt("allNum",i);
                Log.v("allNum",Integer.toString(i));
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        /**
         * 获取新题目数选项***********************************************************************************************
         */
        adapterNewNum = new ArrayAdapter<String>(this,android.R.layout.simple_selectable_list_item,newNum);
        spinnerNewNum.setAdapter(adapterNewNum);
        setSpinnerItemSelectdByValue(spinnerNewNum,sharedPreferences.getString("newNum","10"));
        this.spinnerNewNum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String msg = parent.getItemAtPosition(position).toString();
                editor.putString("newNum",msg);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        /**
         * 获取新题目数选项***********************************************************************************************
         */
        adapterReviewNum = new ArrayAdapter<String>(this,android.R.layout.simple_selectable_list_item,reviewNum);
        spinnerReviewNum.setAdapter(adapterReviewNum);
        setSpinnerItemSelectdByValue(spinnerReviewNum,sharedPreferences.getString("reviewNum","10"));
        this.spinnerReviewNum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String msg = parent.getItemAtPosition(position).toString();
                editor.putString("reviewNum",msg);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    private void setSpinnerItemSelectdByValue(Spinner spinner, String value) {
        SpinnerAdapter apsAdapter = spinner.getAdapter();//获取得到SpinnerAdapter对象
        int k =apsAdapter.getCount();
        for(int i=0;i<k;i++){
            if(value.equals(apsAdapter.getItem(i).toString())){
                spinner.setSelection(i,true);
            }
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.switch_btn:
                if(switchButton.isSwitchOpen()){
                    switchButton.closeSwitch();
                }else{
                    switchButton.openSwitch();
                    editor.putBoolean("btnTf",true);
                }
                editor.commit();
                break;
        }
    }
}
