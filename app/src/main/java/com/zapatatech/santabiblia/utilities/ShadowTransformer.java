package com.zapatatech.santabiblia.utilities;
import android.view.View;

import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;

import com.zapatatech.santabiblia.interfaces.CardAdapter;


public class ShadowTransformer implements ViewPager.OnPageChangeListener, ViewPager.PageTransformer {
    private static final String TAG = "ShadowTransformer";
    private ViewPager mViewPager;
    private CardAdapter mAdapter;
    private float mLastOffset;
    private boolean mScalingEnabled = false;

    public ShadowTransformer(ViewPager viewPager, CardAdapter adapter) {
        mViewPager = viewPager;
        viewPager.addOnPageChangeListener(this);
        mAdapter = adapter;
    }

    public void enableScaling(boolean enable) {
        if (mScalingEnabled && !enable) {
            // shrink main card
            CardView currentCard = mAdapter.getCardViewAt(mViewPager.getCurrentItem());
            if (currentCard != null) {
                currentCard.animate().scaleY(1);
                currentCard.animate().scaleX(1);
            }
        }else if(!mScalingEnabled && enable){
            // grow main card
            CardView currentCard = mAdapter.getCardViewAt(mViewPager.getCurrentItem());
            if (currentCard != null) {
                currentCard.animate().scaleY(1.1f);
                currentCard.animate().scaleX(1.1f);
            }
        }

        mScalingEnabled = enable;
    }

    @Override
    public void transformPage(View page, float position) {
        if (mViewPager.getCurrentItem() == 0) {
            page.setTranslationX(-32);
        } else if (mViewPager.getCurrentItem() == mAdapter.getCount() - 1) {
            page.setTranslationX(32);
        } else {
            page.setTranslationX(0);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        int realCurrentPosition;
        int nextPosition;
        float baseElevation = mAdapter.getBaseElevation();
        float realOffset;
        boolean goingLeft = mLastOffset > positionOffset;

        // If we're going backwards, onPageScrolled receives the last position
        // instead of the current one
        if (goingLeft) {
            realCurrentPosition = position + 1;
            nextPosition = position;
            realOffset = 1 - positionOffset;
        } else {
            nextPosition = position + 1;
            realCurrentPosition = position;
            realOffset = positionOffset;
        }
//        Log.d(TAG, "onPageScrolled: realOffset " + realOffset);
//        Log.d(TAG, "onPageScrolled: positionOffset " + positionOffset);
//
//        Log.d(TAG, "onPageScrolled: mAdapter.getCount() " + mAdapter.getCount());
//        Log.d(TAG, "onPageScrolled: position " + position);
//        Log.d(TAG, "onPageScrolled: nextPosition " + nextPosition);
//        Log.d(TAG, "onPageScrolled: realCurrentPosition " + realCurrentPosition);
//        Log.d(TAG, "onPageScrolled: =========================================");
        if(position == mAdapter.getCount() - 1) {
            //if this is the last card
            CardView currentCard = mAdapter.getCardViewAt(position);
            currentCard.setCardElevation((baseElevation + baseElevation
                    * (CardAdapter.MAX_ELEVATION_FACTOR - 1) * (1 - positionOffset)));
        }
        // Avoid crash on overscroll
        if (nextPosition > mAdapter.getCount() - 1 || realCurrentPosition > mAdapter.getCount() - 1) {
            return;
        }

        CardView currentCard = mAdapter.getCardViewAt(realCurrentPosition);

        // This might be null if a fragment is being used
        // and the views weren't created yet
        if (currentCard != null) {
            if (mScalingEnabled) {
                currentCard.setScaleX((float) (1 + 0.1 * (1 - realOffset)));
                currentCard.setScaleY((float) (1 + 0.1 * (1 - realOffset)));
            }
            currentCard.setCardElevation((baseElevation + baseElevation
                    * (CardAdapter.MAX_ELEVATION_FACTOR - 1) * (1 - realOffset)));
        }

        CardView nextCard = mAdapter.getCardViewAt(nextPosition);

        // We might be scrolling fast enough so that the next (or previous) card
        // was already destroyed or a fragment might not have been created yet
        if (nextCard != null) {
            if (mScalingEnabled) {
                nextCard.setScaleX((float) (1 + 0.1 * (realOffset)));
                nextCard.setScaleY((float) (1 + 0.1 * (realOffset)));
            }
            nextCard.setCardElevation((baseElevation + baseElevation
                    * (CardAdapter.MAX_ELEVATION_FACTOR - 1) * (realOffset)));
        }

        mLastOffset = positionOffset;
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
