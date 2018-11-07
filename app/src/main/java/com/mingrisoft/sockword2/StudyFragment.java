package com.mingrisoft.sockword2;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.assetsbasedata.AssetsDatabaseManager;
import com.mingrisoft.greendao.entity.greendao.DaoMaster;
import com.mingrisoft.greendao.entity.greendao.DaoSession;
import com.mingrisoft.greendao.entity.greendao.WisdomEntity;
import com.mingrisoft.greendao.entity.greendao.WisdomEntityDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by MSI on 18/11/05.
 */

public class StudyFragment extends Fragment{
    private TextView difficulty_text,
                     wisdomEnglish,
                     wisdomChina,
                     alreadyStudyText,
                     alreadyMasteredText,
                     wrongText;
    private SharedPreferences sharedPreferences;             //定义轻量级数据库
    private DaoMaster mDaoMaster;                            //数据库管理者
    private DaoSession mDaoSession;                          //与数据库进行会话

    //生成对应的表，对数据库的操作使用此对象
    private WisdomEntityDao questionDao;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.study_fragment_layout,null);     //绑定局部文件
        sharedPreferences = getActivity().getSharedPreferences("share", Context.MODE_PRIVATE);   //初始化数据库
        difficulty_text = (TextView)view.findViewById(R.id.difficulty_text);   //学习难度绑定
        wisdomChina = (TextView)view.findViewById(R.id.wisdom_china);
        wisdomEnglish = (TextView)view.findViewById(R.id.wisdom_english);

        alreadyMasteredText = (TextView)view.findViewById(R.id.already_mastered);
        alreadyStudyText = (TextView)view.findViewById(R.id.already_study);

        wrongText = (TextView)view.findViewById(R.id.wrong_text);

        AssetsDatabaseManager.initManager(getActivity());   //初始化，只需要调用一次
        //获取管理对象，因为数据库需要管理对象才能获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        SQLiteDatabase db1 = mg.getDatabase("wisdom.db");     //通过管理对象获取数据库
        mDaoMaster = new DaoMaster(db1);                            //初始化管理者
        mDaoSession = mDaoMaster.newSession();                      //初始化会话对象
        questionDao = mDaoSession.getWisdomEntityDao();             //获取数据
        return view;
    }
    public void onStart(){
        super.onStart();
        difficulty_text.setText(sharedPreferences.getString("difficulty","四级")+"英语");  //默认设置难度为四级
        List<WisdomEntity>datas = questionDao.queryBuilder().list();
        Random random = new Random();
        int i = random.nextInt(10);
        //从数据库获取到这条英文数据
        wisdomEnglish.setText(datas.get(i).getEnglish());
        wisdomChina.setText(datas.get(i).getChina());
        setText();
    }
    private void setText(){
        alreadyMasteredText.setText(sharedPreferences.getInt("alreadyMastered",0)+"");
        alreadyStudyText.setText(sharedPreferences.getInt("alreadyStudy",0)+"");
        wrongText.setText(sharedPreferences.getInt("wrong",0)+"");
    }
}
