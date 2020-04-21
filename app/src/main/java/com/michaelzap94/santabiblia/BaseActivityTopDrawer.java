package com.michaelzap94.santabiblia;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.michaelzap94.santabiblia.fragments.ui.tabBooks.BooksPagerAdapter;
import com.michaelzap94.santabiblia.models.Book;
import com.michaelzap94.santabiblia.utilities.BookHelper;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public abstract class BaseActivityTopDrawer extends AppCompatActivity {

    private static final String TAG = "BaseActivityTopDrawer";
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private ViewPager viewPagerBooks;
    private TabLayout tabsBooks;
    private Toolbar toolbar;

    protected void onCreate(Bundle savedInstanceState, int contentView) {
        super.onCreate(savedInstanceState);
        setContentView(contentView);
        toolbar = findViewById(R.id.toolbar_books);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout_books);
        navigationView = findViewById(R.id.nav_view_books);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Single widgets in xml
        viewPagerBooks = findViewById(R.id.pager_view_books);
        tabsBooks = findViewById(R.id.tabs_books);
        //Create the sectionsPagerAdapter
        BooksPagerAdapter sectionsPagerAdapter = new BooksPagerAdapter(this, getSupportFragmentManager());
        // add the SectionsPagerAdapter to the viewPager
        viewPagerBooks.setAdapter(sectionsPagerAdapter);
        viewPagerBooks.setCurrentItem(1);//select NT
        //add the viewPager to the tab layout
        tabsBooks.setupWithViewPager(viewPagerBooks);

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                Log.d(TAG, "onDrawerSlide: " + String.valueOf(slideOffset));
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);//DrawerLayout instance
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                Log.d(TAG, "onDrawerClosed: ");

            }

            @Override
            public void onDrawerStateChanged(int newState) {
                Log.d(TAG, "onDrawerStateChanged: " + String.valueOf(newState));

            }
        });

    }

    public static void onChapterClickedFromDrawer(Context ct, int position) {
        Log.d(TAG, "onBindViewHolder: CLICK RECEIVED "+position);

        Book book = (Book) BookHelper.getLibrosAT().get(position);
        Intent myIntent = new Intent(ct, Bible.class);
        myIntent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
        myIntent.putExtra("book", book.getId());
        myIntent.putExtra("chapter", 1);
        myIntent.putExtra("verse", 0);
        ct.startActivity(myIntent);
    }

    /**
     * Close the Navigation View/Drawer when clicked outside the box.
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Rect viewRect = new Rect();
        navigationView.getGlobalVisibleRect(viewRect);
        if (!viewRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
            //hide your navigation view here.
            drawer.closeDrawer(GravityCompat.START);
        }
        return super.dispatchTouchEvent(ev);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.nav_drawer, menu);
//        return true;
//    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
