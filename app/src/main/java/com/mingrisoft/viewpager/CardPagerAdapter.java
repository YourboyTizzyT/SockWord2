package com.mingrisoft.viewpager;

import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mingrisoft.sockword2.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MSI on 18/11/08.
 */

public class CardPagerAdapter extends PagerAdapter implements CardAdapter{

    private List<CardView> mViews;     //内容序列
    private List<CardItem> mData;      //标题序列
    private float mBaseElevation;
    private OnClickCallback onClickCallback;

    public CardPagerAdapter(){
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
    }
    public void addCardItem(CardItem cardItem){
        mViews.add(null);
        mData.add(cardItem);
    }

    @Override
    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.adapter, container, false);
        view.findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickCallback.onClick(position);
            }
        });
        container.addView(view);
        bind(mData.get(position), view);
        CardView cardView = (CardView) view.findViewById(R.id.cardView);

        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }

        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        mViews.set(position, cardView);
        return view;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }
    private void bind(CardItem item, View view) {
        TextView titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        TextView contentTextView = (TextView) view.findViewById(R.id.contentTextView);
        Button buttonTextView = (Button)view.findViewById(R.id.start);
        buttonTextView.setText(item.getButton());
        titleTextView.setText(item.getTitle());
        contentTextView.setText(item.getText());
    }
    public interface OnClickCallback{
        void onClick(int position);
    }

    public void setOnClickCallback(OnClickCallback onClickCallback) {
        this.onClickCallback = onClickCallback;
    }
}
