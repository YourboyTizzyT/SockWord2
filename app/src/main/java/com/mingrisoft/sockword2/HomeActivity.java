package com.mingrisoft.sockword2;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assetsbasedata.AssetsDatabaseManager;
import com.mingrisoft.greendao.entity.greendao.DaoMaster;
import com.mingrisoft.greendao.entity.greendao.DaoSession;
import com.mingrisoft.greendao.entity.greendao.WisdomEntity;
import com.mingrisoft.greendao.entity.greendao.WisdomEntityDao;
import com.mingrisoft.viewpager.CardItem;
import com.mingrisoft.viewpager.CardPagerAdapter;
import com.mingrisoft.viewpager.ShadowTransformer;

import java.util.List;
import java.util.Random;

/**
 * Created by MSI on 18/11/07.
 */

public class HomeActivity extends AppCompatActivity {
    private ScreenListener screenListener;        //绑定此界面与手机屏幕状态的监听
    private SharedPreferences sharedPreferences;  //加载一个轻量级数据库
    private FragmentTransaction transaction;     //定义用于加载复习与设置的界面

    private StudyFragment studyFragment;
    private Button wrongBtn;

    private TextView difficulty_text,
            wisdomEnglish,
            wisdomChina,
            alreadyStudyText,
            alreadyMasteredText,
            wrongText;

    /**
     * 菜单控件
     */
    private Button mButton;                              //菜单按钮
    private ViewPager mViewPager;                        //页面
    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;
    private ShadowTransformer mFragmentCardShadowTransformer;

    private boolean mShowingFragments = true;



    //==============================================================================================
    private DaoMaster mDaoMaster;                            //数据库管理者
    private DaoSession mDaoSession;                          //与数据库进行会话
    //生成对应的表，对数据库的操作使用此对象
    private WisdomEntityDao questionDao;
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
        //wrongBtn = (Button)findViewById(R.id.wrong_btn);
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
        /**
         * 加载用户数据
         */
        difficulty_text = (TextView)findViewById(R.id.difficulty_text);   //学习难度绑定
        wisdomChina = (TextView)findViewById(R.id.wisdom_china);
        wisdomEnglish = (TextView)findViewById(R.id.wisdom_english);

        alreadyMasteredText = (TextView)findViewById(R.id.already_mastered);
        alreadyStudyText = (TextView)findViewById(R.id.already_study);

        wrongText = (TextView)findViewById(R.id.wrong_text);

        AssetsDatabaseManager.initManager(this);   //初始化，只需要调用一次
        //获取管理对象，因为数据库需要管理对象才能获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        SQLiteDatabase db1 = mg.getDatabase("wisdom.db");     //通过管理对象获取数据库
        mDaoMaster = new DaoMaster(db1);                            //初始化管理者
        mDaoSession = mDaoMaster.newSession();                      //初始化会话对象
        questionDao = mDaoSession.getWisdomEntityDao();             //获取数据

        /**
         * 设置名句
         */
        List<WisdomEntity> datas = questionDao.queryBuilder().list();
        Random random = new Random();
        int i = random.nextInt(10);
        //从数据库获取到这条英文数据
        wisdomEnglish.setText(datas.get(i).getEnglish());
        wisdomChina.setText(datas.get(i).getChina());
        setText();

        initViewPagerCardList();
    }


    /**
     * 启动滑动菜单控件
     */
    private void initViewPagerCardList(){

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mCardAdapter = new CardPagerAdapter();
        mCardAdapter.addCardItem(new CardItem(R.string.title_1, R.string.text_1,R.string.button_1));
        mCardAdapter.addCardItem(new CardItem(R.string.title_2, R.string.text_1,R.string.button_2));
        mCardAdapter.addCardItem(new CardItem(R.string.title_3, R.string.text_1,R.string.button_3));
        mCardAdapter.addCardItem(new CardItem(R.string.title_4, R.string.text_1,R.string.button_4));

        mCardAdapter.setOnClickCallback(new CardPagerAdapter.OnClickCallback() {
            @Override
            public void onClick(int position) {
                switch (position){
                    case 0:
                        Toast.makeText(HomeActivity.this,"点击进入第1个页面",Toast.LENGTH_SHORT);
                        Log.v("111","点击进入第1个页面");
                        break;
                    case 1:
                        Toast.makeText(HomeActivity.this,"点击进入第2个页面",Toast.LENGTH_SHORT);
                        startActivity(new Intent(HomeActivity.this,SetManager.class));
                        break;
                    case 2:
                        Toast.makeText(HomeActivity.this,"点击进入第3个页面",Toast.LENGTH_SHORT);
                        break;
                    case 3:
                        Toast.makeText(HomeActivity.this,"点击进入第4个页面",Toast.LENGTH_SHORT);
                        startActivity(new Intent(HomeActivity.this,StudyActivity.class));
                        break;
                }
            }
        });
        mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);

        mViewPager.setAdapter(mCardAdapter);

        mViewPager.setPageTransformer(false, mCardShadowTransformer);

        mViewPager.setOffscreenPageLimit(3);

        mCardShadowTransformer.enableScaling(true);
    }
    private void setText(){
        alreadyMasteredText.setText(sharedPreferences.getInt("alreadyMastered",0)+"");
        alreadyStudyText.setText(sharedPreferences.getInt("alreadyStudy",0)+"");
        wrongText.setText(sharedPreferences.getInt("wrong",0)+"");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        screenListener.unregisterListener();
    }
    @Override
    protected void onStart(){
        super.onStart();
        difficulty_text.setText(sharedPreferences.getString("difficulty","四级")+"英语");  //默认设置难度为四级
        List<WisdomEntity>datas = questionDao.queryBuilder().list();
        Random random = new Random();
        int i = random.nextInt(10);
        //从数据库获取到这条英文数据
        wisdomEnglish.setText(datas.get(i).getEnglish());
        wisdomChina.setText(datas.get(i).getChina());
        alreadyMasteredText.setText(sharedPreferences.getInt("alreadyMastered",0)+"");
        alreadyStudyText.setText(sharedPreferences.getInt("alreadyStudy",0)+"");
        wrongText.setText(sharedPreferences.getInt("wrong",0)+"");
        }

}
