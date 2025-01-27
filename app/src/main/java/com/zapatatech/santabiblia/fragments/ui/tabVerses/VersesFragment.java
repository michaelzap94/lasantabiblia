package com.zapatatech.santabiblia.fragments.ui.tabVerses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.zapatatech.santabiblia.Bible;
import com.zapatatech.santabiblia.BibleCompare;
import com.zapatatech.santabiblia.DatabaseHelper.ContentDBHelper;
import com.zapatatech.santabiblia.R;
import com.zapatatech.santabiblia.adapters.RecyclerView.DashboardRecyclerViewAdapter;
import com.zapatatech.santabiblia.adapters.RecyclerView.VersesRecyclerViewAdapter;
import com.zapatatech.santabiblia.fragments.dialogs.VersesLabelNoteDialog;
import com.zapatatech.santabiblia.fragments.dialogs.VersesMarkedInOneVerse;
import com.zapatatech.santabiblia.models.Label;
import com.zapatatech.santabiblia.models.Verse;
import com.zapatatech.santabiblia.utilities.BookHelper;
import com.zapatatech.santabiblia.utilities.CommonMethods;
import com.zapatatech.santabiblia.utilities.RecyclerItemClickListener;
import com.zapatatech.santabiblia.viewmodel.VersesViewModel;

import java.util.ArrayList;
import java.util.List;

public class VersesFragment extends Fragment implements RecyclerItemClickListener.OnRecyclerClickListener {
    private static final String TAG = "VersesFragment";
    public static final String TAB_VERSES_NUMBER = "tab_verses_number";
    View root;
    private int book_number;
    private String currentBookName;
    private int chapter_number;
    private int verse_number;
    private Activity mActivity;
    ///////////////////////////////////////////////////////////
    private ActionMode actionMode;
    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private BottomSheetBehavior bottomSheetBehavior;
    private TextView bottomSheetTextview;
    private ImageView bottomSheetIcon;
    ///////////////////////////////////////////////////////////
    private ArrayList<Verse> list = new ArrayList();
    private RecyclerView rvView;
    private VersesViewModel viewModel;
    //Instantiate the RecyclerViewAdapter, passing an empty list initially.
    // this data will not be shown until you setAdapter to the RecyclerView view in the Layout
    private VersesRecyclerViewAdapter rvAdapter;
    //==========================================================
    private ArrayList<Label> arrLabels;
    private DashboardRecyclerViewAdapter rvAdapterLabels;
    private RecyclerView rvViewLabels;
    //=============================================================
    private ViewPager viewPager;

    public static VersesFragment newInstance(int book, int chapter, int verse) {
        Log.d(TAG, "VersesFragment: newInstance" + + book + " " + chapter);
        VersesFragment f = new VersesFragment();
        Bundle args = new Bundle();
        args.putInt("book", book);
        args.putInt("chapter", chapter);
        args.putInt("verse", verse);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mActivity = getActivity();
        this.book_number = getArguments().getInt("book");
        this.chapter_number = getArguments().getInt("chapter");
        this.verse_number = getArguments().getInt("verse");
        this.currentBookName = BookHelper.getInstance().getBook(book_number).getName();
        Log.d(TAG, "onCreate: VersesFragment " + chapter_number);
        rvAdapter = new VersesRecyclerViewAdapter( mActivity, new ArrayList<>());

        //get viewmodel class and properties, pass this context so LifeCycles are handled by ViewModel,
        // in case the Activity is destroyed and recreated(screen roation)
        //ViewModel will help us show the exact same data, and resume the application from when the user left last time.
        viewModel = new ViewModelProvider(this).get(VersesViewModel.class);
        //Use when we need to reload data
        viewModel.fetchData(this.book_number, this.chapter_number);//refresh -> load data
        //viewModel.getUserMutableLiveData().observe(context, verseListUpdateObserver);
        //observerViewModel();
        //=========================================================================================

    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: VersesFragment " + this.chapter_number);
        root = inflater.inflate(R.layout.verses_fragment, container, false);
        viewPager = (ViewPager) container;
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: VersesFragment " + this.chapter_number);
        //////////////////////////////////////////////
        this.rvView = (RecyclerView) root.findViewById(R.id.verses_list_view);
        rvView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        rvView.addOnItemTouchListener(new RecyclerItemClickListener(mActivity, rvView,VersesFragment.this));
        rvView.setAdapter(rvAdapter);//attach the RecyclerView adapter to the RecyclerView View
//        BottomNavigationView bottomNavigationView = ((Bible) mActivity).getBottomNavigationView();
//        rvView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                Log.d(TAG, "onScrolled: "+dy);
//                if (dy > 0 && bottomNavigationView.isShown()) {
//                    bottomNavigationView.setVisibility(View.GONE);
//                } else if (dy < 0 ) {
//                    bottomNavigationView.setVisibility(View.VISIBLE);
//                }
//            }
//
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//        });
        /////////////////////////////////////////
        ///////////////////////////////////////////////////////////

        if(viewPager != null){
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){
                @Override
                public void onPageScrolled ( int position, float positionOffset, int positionOffsetPixels){
                    if(actionMode!=null){
                        actionMode.finish();
                    }
                }
                @Override
                public void onPageSelected(int position) {

                }
                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }

        View bottomSheet = root.findViewById(R.id.bottom_sheet_nestedscrollview);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) rvView.getLayoutParams();
            int rvViewMarginBefore = 0;
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState){
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        if(rvAdapterLabels == null){
                            rvAdapterLabels = new DashboardRecyclerViewAdapter(mActivity, arrLabels, book_number, chapter_number, actionMode, rvAdapter, viewModel);
                        } else {
                            rvAdapterLabels.setActionMode(actionMode);
//                            if(arrLabels != null && arrLabels.size() > 0){
//                                rvAdapterLabels.refreshData(arrLabels);
//                            }
                        }
                        if(rvViewLabels == null) {
                            rvViewLabels = (RecyclerView) view.findViewById(R.id.bottom_sheet_recycler_view);
                            rvViewLabels.setLayoutManager(new LinearLayoutManager(VersesFragment.this.getContext()));
                            rvViewLabels.setAdapter(rvAdapterLabels);//attach the RecyclerView adapter to the RecyclerView View
                        }
                        if(bottomSheetIcon == null){
                            bottomSheetIcon = (ImageView) view.findViewById(R.id.bottom_sheet_arrow_icon);
                        }
                        bottomSheetIcon.setImageResource(R.drawable.ic_keyboard_arrow_up);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        bottomSheetIcon.setImageResource(R.drawable.ic_keyboard_arrow_down);
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        ((Bible) mActivity).showBottomNavigationView();
                        ((Bible) mActivity).showFloatingActionButton();
                        if(actionMode!=null){
                            actionMode.finish();
                        }

                        params.bottomMargin = rvViewMarginBefore;
                        rvView.setLayoutParams(params);
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                        break;
                    default:
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                int margin = (int) (bottomSheetBehavior.getPeekHeight() + (bottomSheet.getHeight() - bottomSheetBehavior.getPeekHeight()) * slideOffset);
                params.bottomMargin = margin;
                rvView.setLayoutParams(params);
            }
        });
        ///////////////////////////////////////////////////////////
        observerViewModel();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
        int book_bookmarked = prefs.getInt(CommonMethods.BOOK_BOOKMARKED, 0);
        int chapter_bookmarked = prefs.getInt(CommonMethods.CHAPTER_BOOKMARKED, 0);
        MenuItem item = menu.add(Menu.NONE, 1, Menu.NONE, "Bookmark");
        if((chapter_bookmarked != 0 && book_bookmarked != 0) && (chapter_bookmarked == chapter_number && book_bookmarked == book_number)){
            item.setIcon(R.drawable.ic_bookmarked);
        } else {
            item.setIcon(R.drawable.ic_notbookmarked);
        }
        item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        item.setOnMenuItemClickListener (new MenuItem.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick (MenuItem item){
                Log.d(TAG, "onMenuItemClick: " + item.getIcon().toString());
                int chapter_bookmarked = prefs.getInt(CommonMethods.CHAPTER_BOOKMARKED, 0);
                int book_bookmarked = prefs.getInt(CommonMethods.BOOK_BOOKMARKED, 0);
                Log.d(TAG, "onMenuItemClick: chapter_bookmarked: " + chapter_bookmarked + " book_bookmarked: " + book_bookmarked);
                if((chapter_bookmarked != 0 && book_bookmarked != 0) && (chapter_bookmarked == chapter_number && book_bookmarked == book_number)) {
                    CommonMethods.setBookmark(prefs, 0, 0);
                    item.setIcon(R.drawable.ic_notbookmarked);
                } else {
                    //when clicked it was not bookmarked, so bookmark it
                    CommonMethods.setBookmark(prefs, book_number, chapter_number);
                    item.setIcon(R.drawable.ic_bookmarked);
                }
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }



//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        Log.d(TAG, "onOptionsItemSelected: " + item.getItemId());
//        // Handle item selection
//        switch (item.getItemId()) {
//            case R.id.dash_label_menu_edit:
//                goToEdit();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    @Override
    public void onResume() {
//        if (mActivity instanceof BaseActivityTopDrawer) {
//            ((BaseActivityTopDrawer) mActivity).getSupportActionBar().setTitle(this.currentBookName + ": "+ this.chapter_number);
//        }
        super.onResume();
    }

    private void observerViewModel() {
        viewModel.getUserMutableLiveData().observe(getViewLifecycleOwner(), (verseArrayList) -> {
            Log.d(TAG, "observerViewModel: VersesFragment GOT DATA" + verseArrayList.size() + "in fragment: " + this.chapter_number);
            //WHEN data is created  pass data and set it in the recyclerview VIEW
            rvAdapter.updateVersesRecyclerView(verseArrayList);

            if (VersesFragment.this.verse_number != 0) {
                VersesFragment.this.rvView.scrollToPosition(VersesFragment.this.verse_number - 1);
            }
        });
    }
    public VersesViewModel getViewModel(){
        return viewModel;
    }
    //==========================================================================================
    private class ActionModeCallback implements ActionMode.Callback {
        private ActionModeCallback() {
        }
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.verse_menu_share:
                    String shareTitle = VersesFragment.this.currentBookName + " " + VersesFragment.this.actionMode.getSubtitle();
                    List<Integer> selectedVersesContent = rvAdapter.getSelectedItems();
                    StringBuilder body = new StringBuilder();
                    for (Integer verse:selectedVersesContent) {
                        body.append(rvAdapter.getVerseArrayListItem(verse).getTextSpanned().toString());
                    }
                    CommonMethods.share(VersesFragment.this.mActivity, shareTitle, body.toString());
                    VersesFragment.this.actionMode.finish();
                    return true;
                case R.id.verse_menu_copy:
                    String title = VersesFragment.this.currentBookName + " " + VersesFragment.this.actionMode.getSubtitle();
                    List<Integer> selectedVersesContentCopy = rvAdapter.getSelectedItems();
                    StringBuilder bodyCopy = new StringBuilder();
                    for (Integer verse:selectedVersesContentCopy) {
                        bodyCopy.append(rvAdapter.getVerseArrayListItem(verse).getTextSpanned().toString());
                    }
                    CommonMethods.copyText(VersesFragment.this.mActivity, title, bodyCopy.toString());
                    VersesFragment.this.actionMode.finish();
                    return true;
                case R.id.verse_menu_compare:
                    VersesFragment.this.goToCompare(VersesFragment.this.book_number, VersesFragment.this.chapter_number, VersesFragment.this.rvAdapter.getItemCount(), VersesFragment.this.rvAdapter.getSelectedItems());
                    VersesFragment.this.actionMode.finish();
                    return true;
                default:
                    return false;
            }
        }
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_verses_selected, menu);
            return true;
        }
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            VersesFragment.this.actionMode = null;
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            VersesFragment.this.rvAdapter.clearSelections();
            //arrLabels = null;
        }
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }
    }
    @Override
    public void onItemClick(View view, int position) {
        Log.d(TAG, "onItemClick: " + position);
//        Toast.makeText(mActivity, "onItemClick" + position, Toast.LENGTH_SHORT).show();
        if (actionMode != null) {
            myToggleSelection(position);
        } else {
            Verse verse = rvAdapter.getVerseArrayListItem(position);
            ArrayList<Label> listOfLabels = verse.getListOfLabels();
            if(listOfLabels != null && listOfLabels.size() > 0){
                openDialogVersesMarkedInOneVerse(verse);
            }
        }
    }
    @Override
    public void onItemLongClick(View view, int position) {
        Log.d(TAG, "onItemLongClick: " + position);
        //===============================================================
        //Create the actionMode only on LongClick
        if (actionMode == null) {
            actionMode = ((AppCompatActivity) mActivity).startSupportActionMode(VersesFragment.this.actionModeCallback);
        }
        myToggleSelection(position);
        //===============================================================
        if(arrLabels == null){
            //TODO: move it to the background
            arrLabels = ContentDBHelper.getInstance(mActivity).getAllLabels();
        }
        if(actionMode != null) {
            ((Bible) mActivity).hideBottomNavigationView();
            ((Bible) mActivity).hideFloatingActionButton();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }
    private void myToggleSelection(int idx) {
        if(VersesFragment.this.actionMode != null){
            int items = rvAdapter.toggleSelection(idx);
            if(items > 0){
                String title = BookHelper.getTitleBookAndCaps(this.chapter_number, rvAdapter.getSelectedItems());
                actionMode.setTitle(this.currentBookName);
                actionMode.setSubtitle(title);
            } else {
                actionMode.finish();
            }
        }
    }
    //=================================================================================================
    public void openDialogVersesMarkedInOneVerse(Verse verse){
        VersesMarkedInOneVerse vid = new VersesMarkedInOneVerse(verse);
        vid.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.CustomDialog);
        vid.show(((AppCompatActivity) mActivity).getSupportFragmentManager(),"anything");
    }
    //===============================================================================================
    public static void onLabelClickedFromList(Context ctx, Label mLabel, int book_number, int chapter_number, ActionMode actionMode, VersesRecyclerViewAdapter rvAdapter, VersesViewModel viewModel) {
        VersesLabelNoteDialog vid = new VersesLabelNoteDialog(ctx, mLabel, book_number, chapter_number, actionMode, rvAdapter, viewModel);
        vid.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
        vid.show(((AppCompatActivity) ctx).getSupportFragmentManager(),"anything");
    }
    //===============================================================================================
    public void goToCompare(int book, int chapter, int totalVerses, @NonNull List<Integer> selectedVerses){
        Log.d(TAG, "goToCompare: ");
        if(book != 0 && chapter != 0 && totalVerses != 0 && selectedVerses.size() > 0){
            Intent myIntent = new Intent(getActivity(), BibleCompare.class);
            myIntent.putExtra("book", book);
            myIntent.putExtra("chapter", chapter);
            myIntent.putExtra("totalVerses", totalVerses);
            myIntent.putIntegerArrayListExtra("selectedVerses", (ArrayList<Integer>) selectedVerses);
            startActivity(myIntent);
            //getActivity().overridePendingTransition(0,0);
        }
    }
}
