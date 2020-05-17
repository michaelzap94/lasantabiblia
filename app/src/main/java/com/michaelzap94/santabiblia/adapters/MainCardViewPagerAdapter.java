package com.michaelzap94.santabiblia.adapters;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
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
import com.michaelzap94.santabiblia.models.VersesMarked;
import com.michaelzap94.santabiblia.R;
import com.michaelzap94.santabiblia.utilities.BookHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MainCardViewPagerAdapter extends PagerAdapter  implements CardAdapter {
    private static final String TAG = "MainCardViewPager";
    private List<VersesMarked> mainCardContents;
    private List<CardView> mViews;
    private float mBaseElevation;
    private LayoutInflater layoutInflater;
    private Context context;

    public MainCardViewPagerAdapter(List<VersesMarked> mainCardContents, Context context) {
        if(mainCardContents != null){
            this.mainCardContents = mainCardContents;
        } else {
            this.mainCardContents = new ArrayList<>();
        }
        this.context = context;
        mViews = new ArrayList<>();
    }

    public void updateVersesMarkedViewPager(List<VersesMarked> _versesMarkedArrayList){
        mainCardContents.clear();
        mainCardContents.addAll(_versesMarkedArrayList);
        mViews.clear();
        mViews.addAll(Collections.nCopies( _versesMarkedArrayList.size(), null));
        notifyDataSetChanged();
    }

    public void addCardItem(VersesMarked item) {
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

    //so that notifyDataSetChanged refreshes
    @Override
    public int getItemPosition(Object object){
        return PagerAdapter.POSITION_NONE;
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

    private void bind(VersesMarked versesMarked, View view) {
        TextView txtView_title, txtView_content;
        txtView_title = view.findViewById(R.id.verses_marked_cardview_title);
        txtView_content = view.findViewById(R.id.verses_marked_cardview_content);

//            Label label = versesMarked.getLabel();
        String bookName = versesMarked.getBook().getName();
//            int book_number = versesMarked.getBook().getBookNumber();
//            int id = versesMarked.getIdVerseMarked();
        int chapter = versesMarked.getChapter();
        boolean hasNote = versesMarked.hasNote();
//            String note = versesMarked.getNote();
        TreeMap<Integer, String> verseTextDict = versesMarked.getVerseTextDict();
        String content = "";
//            int verseFrom = 1000;//no chapter has more than 1000 verses, therefore this is enough: Infinity
//            int verseTo = 0;
        List<Integer> selectedItems = new ArrayList<>();
        for (Map.Entry<Integer, String> mapElement : verseTextDict.entrySet()) {
            int verseNumber = (Integer) mapElement.getKey();
            selectedItems.add(verseNumber - 1);
            String text = (String) mapElement.getValue();
            content = content + " <b>" + verseNumber + "</b>"  + ". " + text;
        }

        Spanned contentSpanned;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            contentSpanned = Html.fromHtml(content, Html.FROM_HTML_MODE_COMPACT);
        } else {
            contentSpanned = Html.fromHtml(content);
        }

        //String title = bookName + " " + chapter + ":" + verseFrom  + (verseFrom < verseTo ? "-" + verseTo : BuildConfig.FLAVOR);
        String titleChapterVerses = BookHelper.getTitleBookAndCaps(chapter, selectedItems);
        String title = bookName + " " + titleChapterVerses;
        txtView_title.setText(title);
        txtView_content.setText(contentSpanned);
//        if(hasNote) {
//            txtView_note.setText(versesMarked.getNote());
//        }

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
