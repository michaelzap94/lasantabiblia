package com.michaelzap94.santabiblia.fragments.dialogs;



import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.michaelzap94.santabiblia.R;
import com.michaelzap94.santabiblia.adapters.RecyclerView.VersesMarkedEditRecyclerViewAdapter;
import com.michaelzap94.santabiblia.adapters.RecyclerView.VersesMarkedRecyclerViewAdapter;
import com.michaelzap94.santabiblia.fragments.dashboard.DashboardLabelFragment;
import com.michaelzap94.santabiblia.models.Label;
import com.michaelzap94.santabiblia.models.VersesMarked;
import com.michaelzap94.santabiblia.utilities.BookHelper;
import com.michaelzap94.santabiblia.utilities.SwipeToDelete;
import com.michaelzap94.santabiblia.viewmodel.VersesMarkedViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE;

public class VersesMarkedEdit extends DialogFragment {

    private static final String TAG = "VersesMarkedEdit";
    private Toolbar toolbar;
    private Context ctx;
    private VersesMarked versesMarked;
    private ItemTouchHelper itemTouchHelper;
    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<Integer> selectedItems = new ArrayList<>();
    private RecyclerView rvView;
    private VersesMarkedViewModel viewModel;
    //Instantiate the RecyclerViewAdapter, passing an empty list initially.
    // this data will not be shown until you setAdapter to the RecyclerView view in the Layout
    private VersesMarkedEditRecyclerViewAdapter rvAdapter;
    private CoordinatorLayout coordinatorLayout;;

    public static VersesMarkedEdit newInstance(VersesMarked versesMarked) {
        Bundle args = new Bundle();
        args.putParcelable("VersesMarked", versesMarked);
        VersesMarkedEdit fragment = new VersesMarkedEdit();
        fragment.setArguments(args);
        return fragment;
    }
//    public static VersesMarkedEdit newInstance(ArrayList<VersesMarked> versesMarked) {
//        Bundle args = new Bundle();
//        args.putParcelableArrayList("VersesMarked", versesMarked);
//        VersesMarkedEdit fragment = new VersesMarkedEdit();
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = getActivity();
//        list = (ArrayList) getArguments().getParcelableArrayList("VersesMarked");
        versesMarked = (VersesMarked) getArguments().getParcelable("VersesMarked");

        for (Map.Entry<Integer, String> mapElement : versesMarked.getVerseTextDict().entrySet()) {
            int verseNumber = (Integer) mapElement.getKey();
            selectedItems.add(verseNumber - 1);
            String text = (String) mapElement.getValue();
            list.add(" <b>" + verseNumber + "</b>"  + ". " + text);
        }
        //itemTouchHelper = new ItemTouchHelper(SwipeToDelete);
        rvAdapter = new VersesMarkedEditRecyclerViewAdapter(this.ctx, list);
        //get viewmodel class and properties, pass this context so LifeCycles are handled by ViewModel,
        // in case the Activity is destroyed and recreated(screen roation)
        //ViewModel will help us show the exact same data, and resume the application from when the user left last time.
//        viewModel = new ViewModelProvider(this).get(VersesMarkedViewModel.class);
//        //Use when we need to reload data
//        viewModel.fetchData(this.mLabel.getId());//refresh -> load data
//        //viewModel.getUserMutableLiveData().observe(context, verseListUpdateObserver);
//        //observerViewModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.verses_marked_dialog_edit, container, false);
        toolbar = rootView.findViewById(R.id.toolbar);
        rvView = rootView.findViewById(R.id.verses_marked_dialog_rv);
        coordinatorLayout = rootView.findViewById(R.id.verses_marked_dialog_coordinatorLayout);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvView.setLayoutManager(new LinearLayoutManager(this.getContext()));
//        rvView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), rvView, VersesFragment.this));
        rvView.setAdapter(rvAdapter);//attach the RecyclerView adapter to the RecyclerView View
//        itemTouchHelper.attachToRecyclerView(rvView);
        enableSwipeToDeleteAndUndo();

        toolbar.setNavigationOnClickListener(v -> {
                dismiss();
        });


        String titleChapterVerses = BookHelper.getTitleBookAndCaps(versesMarked.getChapter(), selectedItems);
        String title = versesMarked.getBook().getName() + " " + titleChapterVerses;
        toolbar.setTitle(title);

        toolbar.inflateMenu(R.menu.menu_save);
        toolbar.setOnMenuItemClickListener(item -> {
            dismiss();
            return true;
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().getSupportFragmentManager().popBackStack();
        this.dismissAllowingStateLoss();
    }
    //===================================================================
    private void enableSwipeToDeleteAndUndo() {
        SwipeToDelete swipeToDeleteCallback = new SwipeToDelete(getActivity()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                //Remove swiped item from list and notify the RecyclerView
                int position = viewHolder.getAdapterPosition();
                final String item = rvAdapter.getData().get(position);
                rvAdapter.removeItem(position);

                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
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
}