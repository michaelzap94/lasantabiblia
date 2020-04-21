package com.michaelzap94.santabiblia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.michaelzap94.santabiblia.fragments.ui.tabVerses.VersesFragment;
import com.michaelzap94.santabiblia.models.Book;
import com.michaelzap94.santabiblia.utilities.BookHelper;
import com.michaelzap94.santabiblia.utilities.CommonMethods;

public class Bible extends BaseActivityTopDrawer {

    private static final String TAG = "Bible";
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

        public VersesPagerAdapter(FragmentManager fa, int book_number, int totalChapters) {
            super(fa, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.book_number = book_number;
            this.totalChapters = totalChapters;
        }

        @Override
        public int getCount() {
            return this.totalChapters;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return (CharSequence) ("   Capitulo    " + (position+1));
        }

        @Override
        public Fragment getItem(int position) {
            return VersesFragment.newInstance(book_number, position + 1, 0);
        }

        @Override
        public int getItemPosition(Object object){
            return PagerAdapter.POSITION_NONE;
        }
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_bible);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.book_number = extras.getInt("book");
            this.chapter_number = extras.getInt("chapter");
            this.verse_number = extras.getInt("verse");
            Book libro = BookHelper.getBook(19);
            if (libro != null) {
                this.bookName = libro.getName();
                this.totalChapters = libro.getNumCap();
                setTitle(this.bookName);
            }
        }
        this.viewPager = (ViewPager) findViewById(R.id.pager_view_chapters);
        this.viewPager.setOffscreenPageLimit(1);
        this.adapter = new VersesPagerAdapter(getSupportFragmentManager(), 230, this.totalChapters);
        viewPager.setAdapter(this.adapter);
        this.tabLayout = (TabLayout) findViewById(R.id.tabs_chapters);
        this.tabLayout.setupWithViewPager(this.viewPager);

//        if (savedInstanceState != null) {
//            this.viewPager.onRestoreInstanceState(savedInstanceState.getParcelable("vp"));
//        } else {
//            this.viewPager.setCurrentItem(this.capitulo - 1);
//        }

        CommonMethods.bottomBarActionHandler((BottomNavigationView) findViewById(R.id.bottom_navigation), R.id.bnav_bible, Bible.this);
    }

//
//    private VersesPagerAdapter setupViewPager(VersesPagerAdapter vpa) {
//        Log.d(TAG, "setupViewPager: " + " " + this.book_number + " " + this.totalChapters);
//        for (int i = 1; i <= this.totalChapters; i++) {
//            int i2;
//            //ViewPagerAdapter viewPagerAdapter = this.adapter;
//            int i3 = this.book_number;
//            if (this.chapter_number == i) {
//                i2 = this.verse_number;
//            } else {
//                i2 = 0;
//            }
//            vpa.addFragment(VersesFragment.newInstance(i3, i, i2));
//        }
//        return vpa;
//    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
    }
}
