package com.mingrisoft.viewpager;

/**
 * Created by MSI on 18/11/08.
 */

/**
 * 该类用来设置图片的标题
 */
public class CardItem {

    private int mTextResource;      //内容
    private int mTitleResource;     //标题
    private int mButtonResource;     //标题

    /**
     * 初始化
     * @param mTextResource
     * @param mTitleResource
     */
    public CardItem(int mTextResource,int mTitleResource,int mButtonResource){
        this.mTitleResource = mTitleResource;
        this.mTextResource = mTextResource;
        this.mButtonResource = mButtonResource;
    }
    public int getText(){
        return mTextResource;
    }
    public int getTitle(){
        return mTitleResource;
    }
    public int getButton(){
        return mButtonResource;
    }
}
