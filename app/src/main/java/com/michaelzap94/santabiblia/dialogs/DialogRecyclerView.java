package com.michaelzap94.santabiblia.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.michaelzap94.santabiblia.Bible;
import com.michaelzap94.santabiblia.DatabaseHelper.BibleDBHelper;
import com.michaelzap94.santabiblia.R;
import com.michaelzap94.santabiblia.dialogs.GridAdapter;
import com.michaelzap94.santabiblia.models.Verse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DialogRecyclerView extends RecyclerView.Adapter<DialogRecyclerView.VersesDialogViewHolder> {
    private static final String TAG = "DialogRecyclerView";
    ArrayList<Verse> verseDialogArrayList;
    private Context ctxDialog;

    public DialogRecyclerView(Context ctxDialog, ArrayList<Verse> verseDialogArrayList) {
        this.verseDialogArrayList = verseDialogArrayList;
        this.ctxDialog = ctxDialog;
        Log.d(TAG, "VersesFragment: REcyclerview: INIT: "+ verseDialogArrayList.size());
    }

//    //function available so View can update the RecyclerView List once the information is available.
//    public void updateVersesRecyclerView(List<Verse> _verseDialogArrayList) {
//        Log.d(TAG, "VersesFragment: REcyclerview: UPDATE: "+ _verseDialogArrayList.size());
//        verseDialogArrayList.clear();
//        verseDialogArrayList.addAll(_verseDialogArrayList);
//        //I have new data, delete everything and add new data
//        notifyDataSetChanged();
//    }

    @NonNull
    @Override
    public DialogRecyclerView.VersesDialogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "RV onCreateViewHolder: dialog ");
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.verses_dialog_adapter_item,parent, false);
        return new DialogRecyclerView.VersesDialogViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull DialogRecyclerView.VersesDialogViewHolder holder, int position) {
        Log.d(TAG, "RV onBindViewHolder: dialog " +  position);
        holder.bind();
    }
    @Override
    public int getItemViewType(int position) {
        Log.d(TAG, "RV getItemViewType: dialog " +  position);

        return position;

    }
    @Override
    public int getItemCount() {
        Log.d(TAG, "RV getItemCount: dialog " +  verseDialogArrayList.size());

        return verseDialogArrayList.size();
    }

    class VersesDialogViewHolder extends RecyclerView.ViewHolder {
        TextView txtView_title_dialog;
        TextView txtView_verse_dialog;

        public VersesDialogViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "VersesDialogViewHolder: dialog ");
            txtView_title_dialog = itemView.findViewById(R.id.txtView_title_dialog);
            txtView_verse_dialog = itemView.findViewById(R.id.txtView_verse_dialog);
        }

        void bind() {
            Log.d(TAG, "bind: position dialog "+ getAdapterPosition());
            Verse verse = verseDialogArrayList.get(getAdapterPosition());
            Spanned spannedTextVerse = verse.getTextSpanned();
            //String stringTextVerse = spannedTextVerse.toString();
            Log.d(TAG, "bind: inside dialog: " + spannedTextVerse.toString());
            txtView_verse_dialog.setText(spannedTextVerse);

            //Log.d(TAG, "position: verse.getTextTitle()" + getAdapterPosition() + " null: " +  ((boolean) (null == verse.getTextTitle())));
            //Bind data to layout elements
            if(verse.getTextTitle() != null){
                txtView_title_dialog.setText(verse.getTextTitle());
                txtView_title_dialog.setVisibility(View.VISIBLE);
            }

        }
    }


}
