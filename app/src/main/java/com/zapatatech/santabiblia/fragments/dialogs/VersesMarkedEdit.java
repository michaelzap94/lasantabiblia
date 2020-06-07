package com.zapatatech.santabiblia.fragments.dialogs;



import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.zapatatech.santabiblia.Bible;
import com.zapatatech.santabiblia.R;
import com.zapatatech.santabiblia.adapters.RecyclerView.VersesMarkedEditRecyclerViewAdapter;
import com.zapatatech.santabiblia.models.VersesMarked;
import com.zapatatech.santabiblia.utilities.BookHelper;
import com.zapatatech.santabiblia.utilities.SwipeToDelete;
import com.zapatatech.santabiblia.viewmodel.VersesMarkedViewModel;

import java.util.ArrayList;
import java.util.Map;

public class VersesMarkedEdit extends DialogFragment {

    private static final String TAG = "VersesMarkedEdit";
    private Toolbar toolbar;
    private Context ctx;
    private VersesMarked versesMarked;
    private ItemTouchHelper itemTouchHelper;
    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<Integer> selectedItems = new ArrayList<>();
    private int selectedItemsInitialSize = 0;
    private TextInputLayout note;
    private RecyclerView rvView;
    private VersesMarkedViewModel viewModelVersesMarked;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = getActivity();
        versesMarked = (VersesMarked) getArguments().getParcelable("VersesMarked");

        for (Map.Entry<Integer, String> mapElement : versesMarked.getVerseTextDict().entrySet()) {
            int verseNumber = (Integer) mapElement.getKey();
            selectedItems.add(verseNumber - 1);
            list.add(" <b>" + verseNumber + "</b>"  + ". " + mapElement.getValue());
        }
        selectedItemsInitialSize = selectedItems.size();
        //itemTouchHelper = new ItemTouchHelper(SwipeToDelete);
        rvAdapter = new VersesMarkedEditRecyclerViewAdapter(this.ctx, list);
        viewModelVersesMarked = new ViewModelProvider(getActivity()).get(VersesMarkedViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.verses_marked_dialog_edit, container, false);
        toolbar = rootView.findViewById(R.id.toolbar);
        rvView = rootView.findViewById(R.id.verses_marked_dialog_rv);
        note = rootView.findViewById(R.id.verses_marked_dialog_note);
        coordinatorLayout = rootView.findViewById(R.id.verses_marked_dialog_coordinatorLayout);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        note.setVisibility(View.VISIBLE);
        rvView.setLayoutManager(new LinearLayoutManager(this.getContext()));
//        rvView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), rvView, VersesFragment.this));
        rvView.setAdapter(rvAdapter);//attach the RecyclerView adapter to the RecyclerView View
//        itemTouchHelper.attachToRecyclerView(rvView);
        enableSwipeToDeleteAndUndo();

        toolbar.setNavigationOnClickListener(v -> {
                dismiss();
        });


        updateTitle();

        note.getEditText().setText(versesMarked.getNote());

        toolbar.inflateMenu(R.menu.menu_save);
        toolbar.setOnMenuItemClickListener(item -> {
//            boolean error = false;
//            if (note.getEditText().getText().toString().trim().equalsIgnoreCase("")) {
//                note.getEditText().setError("This field can not be blank");
//                error = true;
//            }
//            if(!error){
                //if something changed, update, otherwise do not
                String noteValue = note.getEditText().getText().toString();
                if(selectedItems.size() != selectedItemsInitialSize || !noteValue.equals(versesMarked.getNote())){
                    if (ctx instanceof Bible) {
                        viewModelVersesMarked.updateVersesMarked((Bible) ctx, versesMarked.getUuid(), versesMarked.getLabel(), versesMarked.getBook().getBookNumber(), versesMarked.getChapter(), noteValue , selectedItems);
                    } else {
                        viewModelVersesMarked.updateVersesMarked(null, versesMarked.getUuid(), versesMarked.getLabel(), versesMarked.getBook().getBookNumber(), versesMarked.getChapter(), noteValue , selectedItems);
                    }
                }
                dismiss();
//            }
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
        SwipeToDelete swipeToDeleteCallback = new SwipeToDelete(getActivity(), false, true) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                //Remove swiped item from list and notify the RecyclerView
                int position = viewHolder.getAdapterPosition();

                final String item = rvAdapter.getData().get(position);
                rvAdapter.removeItem(position);

                final int verseNumber = selectedItems.get(position);
                selectedItems.remove(position);
                updateTitle();

                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectedItems.add(position, verseNumber);
                        updateTitle();
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

    private void updateTitle(){
        String titleChapterVerses = BookHelper.getTitleBookAndCaps(versesMarked.getChapter(), selectedItems);
        toolbar.setTitle(versesMarked.getBook().getName());
        toolbar.setSubtitle(titleChapterVerses);
    }
}