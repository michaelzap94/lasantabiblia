package com.zapatatech.santabiblia;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.zapatatech.santabiblia.DatabaseHelper.BibleCreator;
import com.zapatatech.santabiblia.DatabaseHelper.BibleDBHelper;
import com.zapatatech.santabiblia.adapters.RecyclerView.BibleCompareRVA;
import com.zapatatech.santabiblia.models.Book;
import com.zapatatech.santabiblia.utilities.BookHelper;
import com.zapatatech.santabiblia.utilities.CommonMethods;
import com.zapatatech.santabiblia.utilities.SwipeToDelete;
import com.zapatatech.santabiblia.viewmodel.VersesViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class BibleCompare extends AppCompatActivity {
    private static final String TAG = "BibleCompare";
    public static final String SELECTED_BIBLES_TO_COMPARE = "selectedBiblesToCompare";
    private static final String FLAG_PREV_BUTTON = "PREV";
    private static final String FLAG_NEXT_BUTTON = "NEXT";
    private int book_number;
    private String bookName;
    private int totalChapters;
    private int chapter_number;
    private int totalVerses;
    private ArrayList<Integer> selectedVerses;
    private ArrayList<String> selectedBibles;
    private ArrayList<String[]> data = new ArrayList<>();
    private FloatingActionButton addBibleButton;
    private Toolbar toolbar;
    private ActionBar mActionBar;
    private SharedPreferences prefs;
    private CoordinatorLayout coordinatorLayout;
    private MaterialButton prevButton;
    private MaterialButton nextButton;
    public Toast toast;
    int minSelectedVerse, maxSelectedVerse;

    private RecyclerView rvView;
    private BibleCompareRVA rvAdapter;
    private VersesViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bible_compare);
        rvView = findViewById(R.id.bible_compare_rv);
        addBibleButton = findViewById(R.id.bible_compare_add_bible);
        toolbar = findViewById(R.id.toolbar);
        coordinatorLayout = findViewById(R.id.bible_compare_coordinatorlayout);
        prevButton = findViewById(R.id.bible_compare_prev);
        nextButton = findViewById(R.id.bible_compare_next);
        //===========================================================
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        //===========================================================
        Bundle extras = getIntent().getExtras();
        Log.d(TAG, "onCreate: BEFORE");
        if (extras != null) {
            this.book_number = extras.getInt("book");
            this.chapter_number = extras.getInt("chapter");
            this.totalVerses = extras.getInt("totalVerses");
            this.selectedVerses = extras.getIntegerArrayList("selectedVerses");
            minSelectedVerse = Collections.min(selectedVerses);
            maxSelectedVerse = Collections.max(selectedVerses);
            Book book = BookHelper.getBook(this.book_number);
            if (book != null) {
                this.bookName = book.getName();
                this.totalChapters = book.getNumCap();
                mActionBar.setTitle(this.bookName);
            }
            mActionBar.setSubtitle(BookHelper.getTitleBookAndCaps(chapter_number, selectedVerses));
            //=====================================================================================
            if(minSelectedVerse <= 0) {
                prevButton.setEnabled(false);
            }
            if(maxSelectedVerse >= totalVerses - 1){
                nextButton.setEnabled(false);
            }
            prefs = PreferenceManager.getDefaultSharedPreferences(this);
            //===========================================================
            viewModel = new ViewModelProvider(BibleCompare.this).get(VersesViewModel.class);
            //===========================================================
            rvAdapter = new BibleCompareRVA(data);
            rvView.setLayoutManager(new LinearLayoutManager(this));
            rvView.setAdapter(rvAdapter);
            enableSwipeToDeleteAndUndo();
            observerViewModel();
            loadSelectedBibles();
            if(selectedBibles != null && selectedBibles.size() != 0) {
                viewModel.fetchDataForBibleCompare(book_number, chapter_number, selectedVerses, selectedBibles);//refresh -> load data
            }
            //====================================================
            addBibleButton.setOnClickListener(mClickListener);
            prevButton.setOnClickListener(mClickListener);
            nextButton.setOnClickListener(mClickListener);
        }
    }
    private View.OnClickListener mClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.bible_compare_add_bible: openBibleSelectorToCompare();
                    break;
                case R.id.bible_compare_prev: goToPrevNextVerse(FLAG_PREV_BUTTON);
                    break;
                case R.id.bible_compare_next: goToPrevNextVerse(FLAG_NEXT_BUTTON);
                    break;
                default: break;
            }
        }
    };

    private void goToPrevNextVerse(String flagPage){
        if(book_number != 0 && chapter_number != 0 && totalVerses != 0 && selectedVerses.size() > 0){

            ArrayList<Integer> newSelectedVerses = new ArrayList<>();
            if(flagPage == FLAG_PREV_BUTTON) {
                if(minSelectedVerse > 0) {
                    newSelectedVerses.add(minSelectedVerse - 1);
                }
            } else {
                if(maxSelectedVerse < totalVerses - 1){
                    newSelectedVerses.add(maxSelectedVerse + 1);
                }
            }
            if(newSelectedVerses.size() > 0) {
                Intent myIntent = new Intent(BibleCompare.this, BibleCompare.class);
                myIntent.putExtra("book", book_number);
                myIntent.putExtra("chapter", chapter_number);
                myIntent.putExtra("totalVerses", totalVerses);
                myIntent.putIntegerArrayListExtra("selectedVerses", (ArrayList<Integer>) newSelectedVerses);
                finish();
                startActivity(myIntent);
                overridePendingTransition(0,0);
            }
        }
    }

    private void observerViewModel() {
        viewModel.getBibleCompareData().observe(BibleCompare.this, (verseArrayList) -> {
            Log.d(TAG, "observerViewModel: VersesFragment GOT DATA" + verseArrayList.size() + "in fragment: " + this.chapter_number);
            data = verseArrayList;
            rvAdapter.refreshData(verseArrayList);
        });
    }
    private ArrayList<String> getDownloadedBiblesNotSelected() {
        Set<String> set = prefs.getStringSet(SELECTED_BIBLES_TO_COMPARE, null);
        ArrayList<String> allBiblesDownloaded = BibleCreator.getInstance(this).listOfAssetsByType("bibles");
        ArrayList<String> biblesNotSelected = new ArrayList<>();
        for (String bible : allBiblesDownloaded) {
            if(!set.contains(bible)){
                biblesNotSelected.add(bible);
            }
        }
        return biblesNotSelected;
    }
    private void loadSelectedBibles(){
        //Retrieve the values
        Set<String> set = prefs.getStringSet(SELECTED_BIBLES_TO_COMPARE, null);
        ArrayList<String> selectedBiblesToCompare;
        if(set == null){
            selectedBiblesToCompare = new ArrayList<>();
        } else {
            selectedBiblesToCompare = new ArrayList(set);
        }
        Log.d(TAG, "loadSelectedBibles: " + selectedBiblesToCompare);
        if(selectedBiblesToCompare.size() == 0) {
            String mainBibleSelected = BibleDBHelper.getMainBibleName();
            ArrayList<String> latestSelectedBibles = addToBiblesSelectedToCompare(mainBibleSelected);
            viewModel.fetchDataForBibleCompare(book_number, chapter_number, selectedVerses, latestSelectedBibles);//refresh -> load data
            //show popup
            openBibleSelectorToCompare();
        } else if (selectedBiblesToCompare.size() == 1) {
            selectedBibles = selectedBiblesToCompare;
            //show popup
            openBibleSelectorToCompare();
        } else {
            //Return list only
            selectedBibles = selectedBiblesToCompare;
        }
    }
    public ArrayList<String> addToBiblesSelectedToCompare(String mainBibleSelected){
        Set<String> existingSet = new HashSet<String>(prefs.getStringSet(SELECTED_BIBLES_TO_COMPARE, new HashSet<String>()));
        Set<String> mSet;

        if(existingSet != null) {
            mSet = existingSet;
        } else {
            mSet = new HashSet<String>();
        }
        Log.d(TAG, "addToBiblesSelectedToCompare: " + mSet);
        mSet.add(mainBibleSelected);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(SELECTED_BIBLES_TO_COMPARE, mSet);
        editor.commit();

        return new ArrayList(mSet);
    }
    public void removeFromBiblesSelectedToCompare(String mainBibleSelected){
        Set<String> existingSet = new HashSet<String>(prefs.getStringSet(SELECTED_BIBLES_TO_COMPARE, new HashSet<String>()));
        Set<String> mSet;

        if(existingSet != null) {
            mSet = existingSet;
        } else {
            return;
        }
        Log.d(TAG, "removeFromBiblesSelectedToCompare: " + mSet);
        mSet.remove(mainBibleSelected);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(SELECTED_BIBLES_TO_COMPARE, mSet);
        editor.commit();
    }
    public void refreshBiblesSelectedToCompare(){
        loadSelectedBibles();
        viewModel.fetchDataForBibleCompare(book_number, chapter_number, selectedVerses, selectedBibles);//refresh -> load data
    }
    public void openBibleSelectorToCompare(){
        Context context = new ContextThemeWrapper(this, R.style.AppTheme2);
        ArrayList<String> biblesDownloaded = getDownloadedBiblesNotSelected();
        if(biblesDownloaded.size() > 0) {
            String[] arrToShow = biblesDownloaded.toArray(new String[biblesDownloaded.size()]);
            new MaterialAlertDialogBuilder(context)
                    .setTitle("Downloaded Bibles:")
                    .setItems(arrToShow,  new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int position) {
                            Log.d(TAG, "onClick: position dialog: " + position);
                            addToBiblesSelectedToCompare(biblesDownloaded.get(position));
                            refreshBiblesSelectedToCompare();
                        }
                    }).show();
        } else {
            showOneToast("All downloaded bibles are shown");
        }

    }

    //===================================================================
    private void enableSwipeToDeleteAndUndo() {
        SwipeToDelete swipeToDeleteCallback = new SwipeToDelete(BibleCompare.this, true, true) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder dragged, RecyclerView.ViewHolder target) {
                int position_dragged = dragged.getAdapterPosition();
                int position_target = target.getAdapterPosition();
                Collections.swap(rvAdapter.getData(), position_dragged, position_target);
                rvAdapter.notifyItemMoved(position_dragged, position_target);
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                //Remove swiped item from list and notify the RecyclerView
                int position = viewHolder.getAdapterPosition();
                final String[] item = rvAdapter.getData().get(position);
                final String bibleRemoved = item[0];//name of Bible
                //remove from RVadapter
                rvAdapter.removeItem(position);
                //remove Bible from SP biblesSelected
                removeFromBiblesSelectedToCompare(bibleRemoved);

                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //get bibles Downloaded and not Selected in SP biblesSelected
//                        ArrayList<String> biblesDownloaded = getDownloadedBiblesNotSelected();
                        addToBiblesSelectedToCompare(bibleRemoved);//add to SP
                        rvAdapter.restoreItem(item, position);
                        rvView.scrollToPosition(position);
                    }
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(rvView);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        finish();
    }


    public void showOneToast (String st){ //"Toast toast" is declared in the class
        try{
            toast.getView().isShown();     // true if visible
            toast.setText(st);
        } catch (Exception e) {         // invisible if exception
            toast = Toast.makeText(this, st, Toast.LENGTH_SHORT);
        }
        toast.show();  //finally display it
    }
}
