package com.michaelzap94.santabiblia;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import com.michaelzap94.santabiblia.models.Book;
import com.michaelzap94.santabiblia.utilities.BookHelper;

import java.util.ArrayList;

public class BibleCompare extends AppCompatActivity {
    private static final String TAG = "BibleCompare";
    private int book_number;
    private String bookName;
    private int totalChapters;
    private int chapter_number;
    private int verse_number;
    private ArrayList<Integer> selectedVerses;

    private Toolbar toolbar;
    private ActionBar mActionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bible_compare);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);


        Bundle extras = getIntent().getExtras();
        Log.d(TAG, "onCreate: BEFORE");
        if (extras != null) {
            this.book_number = extras.getInt("book");
            this.chapter_number = extras.getInt("chapter");
            this.selectedVerses = extras.getIntegerArrayList("selectedVerses");
            Book book = BookHelper.getBook(this.book_number);
            if (book != null) {
                this.bookName = book.getName();
                this.totalChapters = book.getNumCap();
                mActionBar.setTitle(this.bookName);
            }
            mActionBar.setSubtitle(BookHelper.getTitleBookAndCaps(chapter_number, selectedVerses));
//            setBibleState();
        }

        if(selectedVerses.size() == 1) {

        } else {

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
