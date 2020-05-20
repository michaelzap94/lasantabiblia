package com.michaelzap94.santabiblia;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.michaelzap94.santabiblia.adapters.MainCardViewPagerAdapter;
import com.michaelzap94.santabiblia.adapters.VersesMarkedRecyclerViewAdapter;
import com.michaelzap94.santabiblia.models.Book;
import com.michaelzap94.santabiblia.models.MainCardContent;
import com.michaelzap94.santabiblia.models.VersesMarked;
import com.michaelzap94.santabiblia.utilities.BookHelper;
import com.michaelzap94.santabiblia.utilities.CommonMethods;
import com.michaelzap94.santabiblia.utilities.ShadowTransformer;
import com.michaelzap94.santabiblia.viewmodel.VersesMarkedViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivityTopDrawer  {
    private static final String TAG = "MainActivity";
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    private BottomNavigationView bottomNavigationView;
    private TextView main_card_mem_number;
//    Integer[] colors = null;
    //    ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    private ViewPager viewPager;
    private MainCardViewPagerAdapter mainCardViewPagerAdapter;
    ///////////////////////////////////////////////////////////
    private final int label_id_memorize = 1;
    private ArrayList<VersesMarked> list = new ArrayList();
    private VersesMarkedViewModel viewModel;
    private ShadowTransformer mCardShadowTransformer;
    private MaterialButton bookmark_button;
    private MaterialButton last_seen_button;
    int versesMarkedArrayListSize = 0;
    //==========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_main_base);
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        CommonMethods.checkDatabaseExistLoad(MainActivity.this);
        //INIT VIEWS===================================================================================
        main_card_mem_number = findViewById(R.id.main_card_mem_number);
        viewPager = findViewById(R.id.main_card_mem_viewpager);
        bookmark_button = findViewById(R.id.bookmark_button);
        last_seen_button = findViewById(R.id.last_seen_button);


        mainCardViewPagerAdapter = new MainCardViewPagerAdapter(list, this);
        viewModel = new ViewModelProvider(this).get(VersesMarkedViewModel.class);
        //Use when we need to reload data
        viewModel.fetchData(label_id_memorize);//refresh -> load data
        //viewModel.getUserMutableLiveData().observe(context, verseListUpdateObserver);

        mCardShadowTransformer = new ShadowTransformer(viewPager, mainCardViewPagerAdapter);

        viewPager.setPadding(68, 8, 68, 10);
//        viewPager.setPageMargin(5);
        viewPager.setAdapter(mainCardViewPagerAdapter);
        viewPager.setPageTransformer(false, mCardShadowTransformer);
        viewPager.setOffscreenPageLimit(1);


//        Integer[] colors_temp = {
//                getResources().getColor(R.color.color1),
//                getResources().getColor(R.color.color2),
//                getResources().getColor(R.color.color3),
//                getResources().getColor(R.color.color4)
//        };
//
//        colors = colors_temp;

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                main_card_mem_number.setText((viewPager.getCurrentItem()+1)+"/"+versesMarkedArrayListSize);
//                if (position < (adapter.getCount() -1) && position < (colors.length - 1)) {
//                    viewPager.setBackgroundColor(
//
//                            (Integer) argbEvaluator.evaluate(
//                                    positionOffset,
//                                    colors[position],
//                                    colors[position + 1]
//                            )
//                    );
//                }
//
//                else {
//                    viewPager.setBackgroundColor(colors[colors.length - 1]);
//                }
            }
            @Override
            public void onPageSelected(int position) {
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        //===================================================================================
        observerViewModel();
        //===================================================================================
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        CommonMethods.bottomBarActionHandler(bottomNavigationView, R.id.bnav_home, MainActivity.this);
    }

    private void observerViewModel() {
        viewModel.getUserMutableLiveData().observe(this, (versesMarkedArrayList) -> {
            Log.d(TAG, "observerViewModel: LABEL GOT DATA" + versesMarkedArrayList.size() + "in label: Mem");
            versesMarkedArrayListSize = versesMarkedArrayList.size();
            //WHEN data is created  pass data and set it in the updateVersesMarkedViewPager VIEW
            mainCardViewPagerAdapter.updateVersesMarkedViewPager(versesMarkedArrayList);
            main_card_mem_number.setText((viewPager.getCurrentItem()+1)+"/"+versesMarkedArrayListSize);
        });
    }


    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: ");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.bnav_home);
        viewModel.fetchData(label_id_memorize);//refresh -> load data
        //INIT BUTTONS LAST SEEN AND BOOKMARK=================================================================================
        SharedPreferences prefs = getSharedPreferences(CommonMethods.MY_PREFS_NAME, MODE_PRIVATE);
        int book_bookmarked = prefs.getInt(CommonMethods.BOOK_BOOKMARKED, -1);
        int chapter_bookmarked = prefs.getInt(CommonMethods.CHAPTER_BOOKMARKED, -1);
        if(chapter_bookmarked != -1 && book_bookmarked != -1) {
            String book = BookHelper.getBook(book_bookmarked).getName();
            bookmark_button.setText(book + " " + chapter_bookmarked);
        } else {
            bookmark_button.setText("None");
        }

        int book_lastseen = prefs.getInt(CommonMethods.BOOK_LASTSEEN, -1);
        int chapter_lastseen = prefs.getInt(CommonMethods.CHAPTER_LASTSEEN, -1);
        if(chapter_lastseen != -1 && book_lastseen != -1) {
            String book = BookHelper.getBook(book_lastseen).getName();
            last_seen_button.setText(book + " " + chapter_lastseen);
        } else {
            last_seen_button.setText("None");
        }
        //=================================================================================
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        overridePendingTransition(0, 0);
    }
}
