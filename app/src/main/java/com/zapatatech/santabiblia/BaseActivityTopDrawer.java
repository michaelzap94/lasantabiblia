package com.zapatatech.santabiblia;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.zapatatech.santabiblia.fragments.ui.tabBooks.BooksPagerAdapter;
import com.zapatatech.santabiblia.models.Book;
import com.zapatatech.santabiblia.utilities.BookHelper;
import com.zapatatech.santabiblia.utilities.DrawerLayoutHorizontalSupport;

import java.util.Locale;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public abstract class BaseActivityTopDrawer extends AppCompatActivity {

    private static final String TAG = "BaseActivityTopDrawer";
    private DrawerLayoutHorizontalSupport mDrawer;
    private View mDrawerLeft;
    private ActionBarDrawerToggle mDrawerToggle;
    private ViewPager viewPagerBooks;
    private TabLayout tabsBooks;
    private Toolbar toolbar;

    //FLAGS==========================
    static final String FLAG_LANG = "Lang";
    private String flagInSharedPref;
    private SharedPreferences sp;

    protected void onCreate(Bundle savedInstanceState, int contentView) {
        super.onCreate(savedInstanceState);
        //SET INITIAL LANGUAGE==============================================================
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        flagInSharedPref = sp.getString(FLAG_LANG, "");
//        String defaultLang = Locale.getDefault().getLanguage();
//        String currentLang = getResources().getConfiguration().locale.getLanguage();
        //only change language if necessary
        if (!flagInSharedPref.equals("") && !flagInSharedPref.equals(getResources().getConfiguration().locale.getLanguage())) {
            Locale locale = new Locale(flagInSharedPref);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
        }
        //======================================================================================
        setContentView(contentView);
        toolbar = findViewById(R.id.toolbar_books);
        setSupportActionBar(toolbar);
        mDrawer = findViewById(R.id.drawer_layout_books);
        mDrawerLeft = findViewById(R.id.nav_view_books);
        viewPagerBooks = findViewById(R.id.pager_view_books);
        tabsBooks = findViewById(R.id.tabs_books);
        //======================================================================================
        BooksPagerAdapter sectionsPagerAdapter = new BooksPagerAdapter(this, getSupportFragmentManager());
        // add the SectionsPagerAdapter to the viewPager
        viewPagerBooks.setAdapter(sectionsPagerAdapter);
        viewPagerBooks.setCurrentItem(1);//select NT
        //add the viewPager to the tab layout
        tabsBooks.setupWithViewPager(viewPagerBooks);
        viewPagerBooks.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                if (BaseActivityTopDrawer.this.mDrawer == null) {
                    return;
                }
                if (position == 2) {
                    BaseActivityTopDrawer.this.mDrawer.setverifyScrollChild(false);
                } else {
                    BaseActivityTopDrawer.this.mDrawer.setverifyScrollChild(true);
                }
            }

            public void onPageScrollStateChanged(int state) {
            }
        });
        //======================================================================================
        this.mDrawer.setInterceptTouchEventChildId(R.id.pager_view_books);
        this.mDrawerToggle = new ActionBarDrawerToggle(this, this.mDrawer, this.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerClosed(View view) {
                if (BaseActivityTopDrawer.this.mDrawer != null) {
                    BaseActivityTopDrawer.this.mDrawer.setverifyScrollChild(false);
                }
            }
            public void onDrawerOpened(View drawerView) {
                if (BaseActivityTopDrawer.this.mDrawer != null && BaseActivityTopDrawer.this.viewPagerBooks.getCurrentItem() != 2) {
                    BaseActivityTopDrawer.this.mDrawer.setverifyScrollChild(true);
                }
            }
        };
        this.mDrawer.addDrawerListener(this.mDrawerToggle);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(true);
        this.mDrawerToggle.syncState();
    }

    public static void onChapterClickedFromDrawer(Activity ct, int position) {
        Log.d(TAG, "onBindViewHolder: CLICK RECEIVED "+position);
        Book book = (Book) BookHelper.getBook(position);
        Intent myIntent = new Intent(ct, Bible.class);
        myIntent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
        myIntent.putExtra("book", book.getBookNumber());
        myIntent.putExtra("chapter", 1);
        myIntent.putExtra("verse", 0);
        ct.startActivity(myIntent);
    }

    @Override
    public void onBackPressed() {
        if (isNavigationDrawerOpen()) {
            closeNavigationDrawer();
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        if (isNavigationDrawerOpen()) {
            closeNavigationDrawer();
        }
    }
    public void closeNavigationDrawer() {
        if (this.mDrawer != null && this.mDrawerLeft != null) {
            this.mDrawer.closeDrawer(this.mDrawerLeft);
        }
    }
    public boolean isNavigationDrawerOpen() {
        return (this.mDrawer == null || this.mDrawerLeft == null || !this.mDrawer.isDrawerOpen(this.mDrawerLeft)) ? false : true;
    }
}
