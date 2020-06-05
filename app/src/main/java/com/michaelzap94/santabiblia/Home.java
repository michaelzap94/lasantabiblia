package com.michaelzap94.santabiblia;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.michaelzap94.santabiblia.DatabaseHelper.ContentDBHelper;
import com.michaelzap94.santabiblia.adapters.PagerAdapter.MainCardViewPagerAdapter;
import com.michaelzap94.santabiblia.fragments.dialogs.VersesLearned;
import com.michaelzap94.santabiblia.models.VersesMarked;
import com.michaelzap94.santabiblia.utilities.BookHelper;
import com.michaelzap94.santabiblia.utilities.CommonMethods;
import com.michaelzap94.santabiblia.utilities.ShadowTransformer;
import com.michaelzap94.santabiblia.utilities.Util;
import com.michaelzap94.santabiblia.viewmodel.VersesMarkedViewModel;

import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

public class Home extends BaseActivityTopDrawer  {
    private static final String TAG = "Home";
    private BottomNavigationView bottomNavigationView;
    private TextView main_card_mem_number;
    //    Integer[] colors = null;
    //    ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    private ViewPager viewPager;
    private MainCardViewPagerAdapter mainCardViewPagerAdapter;
    ///////////////////////////////////////////////////////////
    private ArrayList<VersesMarked> list = new ArrayList();
    private VersesMarkedViewModel viewModel;
    private ShadowTransformer mCardShadowTransformer;
    private MaterialButton bookmark_button;
    private MaterialButton last_seen_button;
    private MaterialButton verses_learned_button;
    int versesMarkedArrayListSize = 0;
    //INIT BUTTON VALUES==========================================================
    int book_bookmarked = 0;
    int chapter_bookmarked = 0;
    int book_lastseen = 0;
    int chapter_lastseen = 0;
    View.OnClickListener mClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.last_seen_button:
                    Log.d(TAG, "onClick: last_seen_button: " + book_lastseen + " " + chapter_lastseen);
                    goToBible(book_lastseen, chapter_lastseen);
                    break;
                case R.id.bookmark_button:
                    Log.d(TAG, "onClick: bookmark_button: " + book_bookmarked + " " + chapter_bookmarked);
                    goToBible(book_bookmarked, chapter_bookmarked);
                    break;
                case R.id.verses_learned_button:
                    goToVersesLearned();
                    break;
                default: break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_home_base);
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        setTitle(R.string.app_name);
        //=============================================================================================
        CommonMethods.checkBibleSelectedExist(Home.this);
        //INIT VIEWS===================================================================================
        main_card_mem_number = findViewById(R.id.main_card_mem_number);
        viewPager = findViewById(R.id.main_card_mem_viewpager);
        bookmark_button = findViewById(R.id.bookmark_button);
        last_seen_button = findViewById(R.id.last_seen_button);
        verses_learned_button = findViewById(R.id.verses_learned_button);
        //=============================================================================================
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();
        if(rotation != Surface.ROTATION_0){
            //not in portrait
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.height=(int) Resources.getSystem().getDisplayMetrics().heightPixels;
            viewPager.setLayoutParams(params);
        }

        //BUTTON LISTENER=============================================================================
        bookmark_button.setOnClickListener(mClickListener);
        last_seen_button.setOnClickListener(mClickListener);
        verses_learned_button.setOnClickListener(mClickListener);

        mainCardViewPagerAdapter = new MainCardViewPagerAdapter(list, this, this);
        viewModel = new ViewModelProvider(this).get(VersesMarkedViewModel.class);
        //Use when we need to reload data
        viewModel.getVersesLearned(0);//refresh -> load data

        mCardShadowTransformer = new ShadowTransformer(viewPager, mainCardViewPagerAdapter);

        int bottomAndTopPadding = Util.dpAsPixels(this,8);
        viewPager.setPadding(68, bottomAndTopPadding, 68, bottomAndTopPadding);
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
        CommonMethods.bottomBarActionHandler(bottomNavigationView, R.id.bnav_home, Home.this);
    }
    private void observerViewModel() {
        viewModel.getVersesMarkedListNotLearned().observe(this, (versesMarkedArrayList) -> {
            Log.d(TAG, "observerViewModel: LABEL GOT DATA" + versesMarkedArrayList.size() + "in label: Mem");
            versesMarkedArrayListSize = versesMarkedArrayList.size();
            //WHEN data is created  pass data and set it in the updateVersesMarkedViewPager VIEW
            mainCardViewPagerAdapter.updateVersesMarkedViewPager(versesMarkedArrayList);
            if(versesMarkedArrayListSize == 0 ) {
                main_card_mem_number.setText("0");
            } else {
                main_card_mem_number.setText((viewPager.getCurrentItem()+1)+"/"+versesMarkedArrayListSize);
            }
        });
    }
    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.bnav_home);
        viewModel.getVersesLearned(0);//refresh -> load data
        //INIT BUTTONS LAST SEEN AND BOOKMARK=================================================================================
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        book_bookmarked = prefs.getInt(CommonMethods.BOOK_BOOKMARKED, 0);
        chapter_bookmarked = prefs.getInt(CommonMethods.CHAPTER_BOOKMARKED, 0);
        if(chapter_bookmarked != 0 && book_bookmarked != 0) {
            Log.d(TAG, "onResume: book_bookmarked " + book_bookmarked);
            String book = BookHelper.getBook(book_bookmarked).getName();
            bookmark_button.setText(book + " " + chapter_bookmarked);
            bookmark_button.setEnabled(true);
        } else {
            bookmark_button.setText("None");
            bookmark_button.setEnabled(false);
        }

        book_lastseen = prefs.getInt(CommonMethods.BOOK_LASTSEEN, 0);
        chapter_lastseen = prefs.getInt(CommonMethods.CHAPTER_LASTSEEN, 0);
        if(chapter_lastseen != 0 && book_lastseen != 0) {
            Log.d(TAG, "onResume: book_lastseen " + book_lastseen);
            String book = BookHelper.getBook(book_lastseen).getName();
            last_seen_button.setText(book + " " + chapter_lastseen);
            last_seen_button.setEnabled(true);
        } else {
            last_seen_button.setText("None");
            last_seen_button.setEnabled(false);
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
    protected void goToBible(int book, int chapter){
        if(book != 0 && chapter != 0){
            Intent myIntent = new Intent(Home.this, Bible.class);
            myIntent.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
            myIntent.putExtra("book", book);
            myIntent.putExtra("chapter", chapter);
            myIntent.putExtra("verse", 0);
            myIntent.putExtra("resetstate", true);
            startActivity(myIntent);
            overridePendingTransition(0,0);
        }
    }
    public void goToVersesLearned(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        VersesLearned newFragment = VersesLearned.newInstance();
        // fragment fullscreen
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // For a little polish, specify a transition animation
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        // To make it fullscreen, use the 'content' root view as the container
        // for the fragment, which is always the root view for the activity
        transaction.add(android.R.id.content, newFragment)//R.id.dashboard_fragment-> remove margin in verses_marked_dialog_edit.xml
                .addToBackStack(null).commit();
    }
    //=================================================================================
    public void markAsLearned(String uuid, int position){new Home.AddVersesLearned(position).execute(uuid);}
    private class AddVersesLearned extends AsyncTask<String, Void, Boolean> {
        private int position;
        private AddVersesLearned(int position) {
            this.position = position;
        }
        protected Boolean doInBackground(String... args) {
            Log.d(TAG, "doInBackground: " + position);
            return ContentDBHelper.getInstance(Home.this).editVersesLearned(args[0], 1);
        }
        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if(success){
                mainCardViewPagerAdapter.removeCardItem(this.position);
                versesMarkedArrayListSize--;
                if(versesMarkedArrayListSize == 0 ) {
                    main_card_mem_number.setText("0");
                } else {
                    main_card_mem_number.setText((viewPager.getCurrentItem()+1)+"/"+versesMarkedArrayListSize);
                }
            } else {
                Toast.makeText(Home.this, "This item could not be deleted", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
