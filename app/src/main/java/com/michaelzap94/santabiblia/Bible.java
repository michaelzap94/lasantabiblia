package com.michaelzap94.santabiblia;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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

public class Bible extends BaseActivityTopDrawer{

    private static final String TAG = "Bible";
    private BottomNavigationView bottomNavigationView;
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
            return VersesFragment.newInstance(this.book_number, position + 1, this.verse_number);
        }

        @Override
        public int getItemPosition(Object object){
            return PagerAdapter.POSITION_NONE;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_bible);
        Log.d(TAG, "onCreate: CLICK ");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.book_number = extras.getInt("book");
            this.chapter_number = extras.getInt("chapter");
            this.verse_number = extras.getInt("verse");
            Book libro = BookHelper.getBook(this.book_number);
            if (libro != null) {
                this.bookName = libro.getName();
                this.totalChapters = libro.getNumCap();
                setTitle(this.bookName);
            }
        }
        this.viewPager = (ViewPager) findViewById(R.id.pager_view_chapters);
        this.viewPager.setOffscreenPageLimit(1);
        this.adapter = new VersesPagerAdapter(getSupportFragmentManager(), this.book_number, this.chapter_number, this.verse_number, this.totalChapters);
        viewPager.setAdapter(this.adapter);
//        this.tabLayout = (TabLayout) findViewById(R.id.tabs_chapters);
//        this.tabLayout.setupWithViewPager(this.viewPager);

        if (savedInstanceState != null) {
            //this.viewPager.onRestoreInstanceState(savedInstanceState.getParcelable("vp"));
        } else {
            this.viewPager.setCurrentItem(this.chapter_number - 1);
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog2();
            }
        });

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        CommonMethods.bottomBarActionHandler(bottomNavigationView, R.id.bnav_bible, Bible.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.bnav_bible);
    }

    public void openDialog2() {
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

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
    }
}
