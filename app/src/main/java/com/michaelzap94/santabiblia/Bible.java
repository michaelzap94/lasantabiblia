package com.michaelzap94.santabiblia;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.michaelzap94.santabiblia.adapters.dialogs.GridAdapter;
import com.michaelzap94.santabiblia.fragments.ui.tabVerses.VersesFragment;
import com.michaelzap94.santabiblia.models.Book;
import com.michaelzap94.santabiblia.utilities.BookHelper;
import com.michaelzap94.santabiblia.utilities.CommonMethods;

import java.util.HashMap;

public class Bible extends BaseActivityTopDrawer{

    private static final String TAG = "Bible";
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fab;
    private int book_number;
    private int totalChapters;
    public String bookName;
    private int chapter_number;
    private int verse_number;

    private VersesPagerAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public class VersesPagerAdapter extends FragmentStatePagerAdapter {
        private int book_number;
        private int totalChapters;
        private int chapter_number;
        private int verse_number;
        private HashMap<Integer, VersesFragment> mPageReferenceMap = new HashMap<>();

        public VersesPagerAdapter(FragmentManager fa, int book_number, int chapter_number, int verse_number, int totalChapters) {
            super(fa, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.book_number = book_number;
            this.totalChapters = totalChapters;
            this.chapter_number = chapter_number;
            this.verse_number = verse_number;
        }

        @Override
        public int getCount() {
            return this.totalChapters;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return (CharSequence) ("Capitulo" + (position+1));
        }

        @Override
        public Fragment getItem(int position) {
            VersesFragment fragment = VersesFragment.newInstance(this.book_number, position + 1, this.verse_number);
            mPageReferenceMap.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem (ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            mPageReferenceMap.remove(position);
        }

        public VersesFragment getFragment(int key) {
            return mPageReferenceMap.get(key);
        }

        @Override
        public int getItemPosition(Object object){
            return PagerAdapter.POSITION_NONE;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_bible);
        Log.d(TAG, "onCreate: CLICK ");
        //init views====================================================
        this.viewPager = (ViewPager) findViewById(R.id.pager_view_chapters);
        this.viewPager.setOffscreenPageLimit(1);

        Bundle extras = getIntent().getExtras();
        Log.d(TAG, "onCreate: BEFORE");
        if (extras != null) {

            this.book_number = extras.getInt("book");
            this.chapter_number = extras.getInt("chapter");
            this.verse_number = extras.getInt("verse");
            Log.d(TAG, "onCreate: AFTER " + this.book_number + " " + this.chapter_number);
            Book libro = BookHelper.getBook(this.book_number);
            if (libro != null) {
                this.bookName = libro.getName();
                this.totalChapters = libro.getNumCap();
                setTitle(this.bookName);
            }
            setBibleState();
        }


        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openChapterSelector();
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                getSupportActionBar().setTitle(bookName + ": "+ (viewPager.getCurrentItem()+1));
            }
            @Override
            public void onPageSelected(int position) {
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        CommonMethods.bottomBarActionHandler(bottomNavigationView, R.id.bnav_bible, Bible.this);
    }

    private void setBibleState(){
        this.adapter = new VersesPagerAdapter(getSupportFragmentManager(), this.book_number, this.chapter_number, this.verse_number, this.totalChapters);
        viewPager.setAdapter(this.adapter);
//        this.tabLayout = (TabLayout) findViewById(R.id.tabs_chapters);
//        this.tabLayout.setupWithViewPager(this.viewPager);

//        if (savedInstanceState != null) {
//            //this.viewPager.onRestoreInstanceState(savedInstanceState.getParcelable("vp"));
//        } else {
            this.viewPager.setCurrentItem(this.chapter_number - 1);
//        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.bnav_bible);

//        Bundle extras = getIntent().getExtras();
//        Log.d(TAG, "onResume: BEFORE");

//        if (extras != null) {
//            if(extras.getBoolean("resetstate")) {
//                this.book_number = extras.getInt("book");
//                this.chapter_number = extras.getInt("chapter");
//                this.verse_number = extras.getInt("verse");
//                Log.d(TAG, "onResume: AFTER " + this.book_number + " " + this.chapter_number);
//                Book libro = BookHelper.getBook(this.book_number);
//                if (libro != null) {
//                    this.bookName = libro.getName();
//                    this.totalChapters = libro.getNumCap();
//                    setTitle(this.bookName);
//                }
//                setBibleState();
//            }
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        CommonMethods.setLastSeen(this, this.book_number, this.viewPager.getCurrentItem() + 1);
    }

    public void openChapterSelector() {
        LayoutInflater inflater = this.getLayoutInflater();
        // Dialog layout
        View v = inflater.inflate(R.layout.dialog_chapters_grid, null);
        // Get gridView from dialog_choice
        GridView gV = (GridView) v.findViewById(R.id.gridView);
        // GridAdapter (Pass context and files list)
        final AlertDialog.Builder builder2 = new AlertDialog.Builder(Bible.this);
        builder2.setTitle(this.bookName+ "\n" + this.totalChapters + " Capitulos");
        //builder2.setCustomTitle(getLayoutInflater().inflate(R.layout.btn_share,null));
        builder2.setView(v);
        builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder2.setCancelable(true);
        final Dialog dialog = builder2.create();
        GridAdapter adapter = new GridAdapter(this, this.viewPager, dialog, this.totalChapters);
        gV.setAdapter(adapter);
        dialog.show();
    }

    //============================================================================================
    public void onVersesMarkedEditedFromDialog(int _book_number, int _chapter_number){
        VersesFragment currentFragmentInstance = this.adapter.getFragment(this.viewPager.getCurrentItem());
//        currentFragmentInstance.getViewModel().fetchData(this.book_number, this.chapter_number);
        currentFragmentInstance.getViewModel().fetchData(_book_number,  _chapter_number);
    }
    //============================================================================================
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        overridePendingTransition(0, 0);
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Log.d(TAG, "onNewIntent:    before: " + extras.getBoolean("resetstate"));
            if(extras.getBoolean("resetstate")){
                this.book_number = extras.getInt("book");
                this.chapter_number = extras.getInt("chapter");
                this.verse_number = extras.getInt("verse");
                Log.d(TAG, "onNewIntent: AFTER " + this.book_number + " " + this.chapter_number);
                Book libro = BookHelper.getBook(this.book_number);
                if (libro != null) {
                    this.bookName = libro.getName();
                    this.totalChapters = libro.getNumCap();
                    setTitle(this.bookName);
                }
                setBibleState();
            }
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    public void showFloatingActionButton(){
        fab.show();
    }
    public void hideFloatingActionButton(){
        fab.hide();
    }
    public BottomNavigationView getBottomNavigationView(){
        return bottomNavigationView;
    }
    public void showBottomNavigationView(){
        bottomNavigationView.setVisibility(View.VISIBLE);
    }
    public void hideBottomNavigationView(){
        bottomNavigationView.setVisibility(View.GONE);
    }
}
