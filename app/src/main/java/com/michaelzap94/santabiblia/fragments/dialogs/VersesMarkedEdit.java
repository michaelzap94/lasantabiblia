package com.michaelzap94.santabiblia.fragments.dialogs;



import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.michaelzap94.santabiblia.R;
public class VersesMarkedEdit extends DialogFragment {

    private static final String TAG = "VersesMarkedEdit";
    private Toolbar toolbar;
    @Override
    public void onPause() {
        super.onPause();
        getActivity().getSupportFragmentManager().popBackStack();
        this.dismissAllowingStateLoss();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.verses_marked_dialog_edit, container, false);
        toolbar = rootView.findViewById(R.id.toolbar);
//        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
//        Log.d(TAG, "onCreateView: " + actionBar);
//         if( actionBar == null) {
//            toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
//            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
//        }

//        if (actionBar != null) {
//            actionBar.setTitle("Edit Verses Marked");
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setHomeButtonEnabled(true);
//            actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
//        }
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setNavigationOnClickListener(v -> {
                getActivity().getSupportFragmentManager().popBackStack();
                dismiss();
        });
        toolbar.setTitle("Some Title");
        toolbar.inflateMenu(R.menu.menu_save);
        toolbar.setOnMenuItemClickListener(item -> {
            getActivity().getSupportFragmentManager().popBackStack();
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

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        menu.clear();
//        getActivity().getMenuInflater().inflate(R.menu.menu_save, menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == R.id.action_save) {
//            // handle confirmation button click here
//            return true;
//        } else if (id == android.R.id.home) {
//            // handle close button click here
//            getActivity().getSupportFragmentManager().popBackStack();
//            dismiss();
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}