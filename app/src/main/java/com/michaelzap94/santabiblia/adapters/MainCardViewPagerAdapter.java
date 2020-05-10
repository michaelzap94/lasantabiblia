package com.michaelzap94.santabiblia.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;

import com.google.android.material.card.MaterialCardView;
import com.michaelzap94.santabiblia.interfaces.CardAdapter;
import com.michaelzap94.santabiblia.models.MainCardContent;
import com.michaelzap94.santabiblia.R;

import java.util.ArrayList;
import java.util.List;

public class MainCardViewPagerAdapter extends PagerAdapter  implements CardAdapter {
    private static final String TAG = "Adapter";
    private List<MainCardContent> mainCardContents;
    private List<CardView> mViews;
    private float mBaseElevation;
    private LayoutInflater layoutInflater;
    private Context context;

    public MainCardViewPagerAdapter(List<MainCardContent> mainCardContents, Context context) {
        if(mainCardContents != null){
            this.mainCardContents = mainCardContents;
        } else {
            this.mainCardContents = new ArrayList<>();
        }
        this.context = context;
        mViews = new ArrayList<>();
    }

    public void addCardItem(MainCardContent item) {
        mViews.add(null);
        mainCardContents.add(item);
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
        return mainCardContents.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.main_mem_cardview, container, false);

        bind(mainCardContents.get(position), view);
        container.addView(view, 0);
        Log.d(TAG, "newadapter instantiateItem: " + position);

        MaterialCardView cardView = (MaterialCardView) view.findViewById(R.id.main_card_mem_cardview);

        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }

        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        mViews.set(position, cardView);

        return view;
    }

    private void bind(MainCardContent item, View view) {
        TextView title, desc;
        title = view.findViewById(R.id.verses_marked_cardview_title);
        desc = view.findViewById(R.id.verses_marked_cardview_content);
        title.setText(item.getTitle());
        desc.setText(item.getDesc());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        Log.d(TAG, "newadapter destroyItem: " + position);
        container.removeView((View)object);
        mViews.set(position, null);
    }
}
