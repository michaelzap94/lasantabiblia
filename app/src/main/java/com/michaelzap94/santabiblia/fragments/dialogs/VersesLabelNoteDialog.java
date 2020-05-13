package com.michaelzap94.santabiblia.fragments.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ActionMode;
import androidx.fragment.app.DialogFragment;

import com.michaelzap94.santabiblia.DatabaseHelper.ContentDBHelper;
import com.michaelzap94.santabiblia.R;
import com.michaelzap94.santabiblia.adapters.VersesRecyclerViewAdapter;
import com.michaelzap94.santabiblia.models.Label;
import com.michaelzap94.santabiblia.utilities.BookHelper;
import com.michaelzap94.santabiblia.viewmodel.VersesViewModel;

import java.util.List;

public class VersesLabelNoteDialog extends DialogFragment {
    private static final String TAG = "VersesLabelNoteDialog";
    Context ctx;
    Label mLabel;
    int book_number;
    int chapter_number;
    ActionMode actionMode;
    VersesRecyclerViewAdapter rvAdapter;
    TextView titleTxtView;
    EditText input;
    String title;
    String dialogTitle;
    VersesViewModel viewModel;

    public VersesLabelNoteDialog(Context ctx, Label mLabel, int book_number, int chapter_number, ActionMode actionMode, VersesRecyclerViewAdapter rvAdapter, VersesViewModel viewModel) {
        this.ctx = ctx;
        this.mLabel = mLabel;
        this.book_number = book_number;
        this.chapter_number = chapter_number;
        this.actionMode = actionMode;
        this.rvAdapter = rvAdapter;
        this.viewModel = viewModel;

        List<Integer> selectedItems = rvAdapter.getSelectedItems();
        String titleBookAndCaps = BookHelper.getTitleBookAndCaps(chapter_number, selectedItems);
        String currentBookName = BookHelper.getBook(book_number).getName();

        this.title = currentBookName + " " + titleBookAndCaps;
        this.dialogTitle = "Add Note to \""+mLabel.getName() + "\":";
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onPause() {
        super.onPause();
        this.dismissAllowingStateLoss();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.verses_dialog_label_note, null, false);
        Log.d(TAG, "onCreateDialog: inside dialog");
        titleTxtView = rootView.findViewById(R.id.verses_dialog_label_note_title);
        input = rootView.findViewById(R.id.verses_dialog_label_note_input);

        titleTxtView.setText(dialogTitle);

        return new AlertDialog.Builder(getActivity())
                .setTitle(this.title)
                .setView(rootView)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String note = input.getText().toString();
                                boolean success = ContentDBHelper.getInstance(ctx).insertSelectedItemsBulkTransaction(mLabel, book_number, chapter_number, note, rvAdapter.getSelectedItems());
                                if(success) {
                                    viewModel.fetchData(book_number, chapter_number);
                                } else {
                                    Toast.makeText(ctx, "Not all elements could be inserted", Toast.LENGTH_SHORT).show();
                                }
                                dialog.dismiss();
                                if(actionMode != null){
                                    actionMode.finish();
                                }
                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                                if(actionMode != null){
                                    actionMode.finish();
                                }
                            }
                        }
                )
                .create();
    }
}
