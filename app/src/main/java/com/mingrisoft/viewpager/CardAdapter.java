package com.mingrisoft.viewpager;

import android.support.v7.widget.CardView;

/**
 * Created by MSI on 18/11/08.
 */

public interface CardAdapter {

    int MAX_ELEVATION_FACTOR = 8;

    float getBaseElevation();

    CardView getCardViewAt(int position);

    int getCount();


}
