package com.zapatatech.santabiblia.adapters.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.zapatatech.santabiblia.R;
import com.zapatatech.santabiblia.fragments.dialogs.VersesMarkedEdit;
import com.zapatatech.santabiblia.models.Label;
import com.zapatatech.santabiblia.models.VersesMarked;
import com.zapatatech.santabiblia.utilities.BookHelper;

import java.util.ArrayList;

public class DialogVersesMarkedInOneVerseRV extends RecyclerView.Adapter<DialogVersesMarkedInOneVerseRV.VersesDialogViewHolder> {
    private static final String TAG = "DialogVersesMarkedInORV";
    private ArrayList<VersesMarked> listOfVersesMarked;
    private Context ctxDialog;
    private Dialog dialog;
    int totalSize;

    public DialogVersesMarkedInOneVerseRV(Context ctxDialog, Dialog dialog, ArrayList<VersesMarked> listOfVersesMarked) {
        if(listOfVersesMarked == null ){
            this.listOfVersesMarked = new ArrayList<>();
        } else {
            this.listOfVersesMarked = listOfVersesMarked;
        }
        this.ctxDialog = ctxDialog;
        this.dialog = dialog;
    }

    public void setDialog(Dialog dialog){
        this.dialog = dialog;
    }

    //function available so View can update the RecyclerView List once the information is available.
    public void updateVersesMarkedRecyclerView(ArrayList<VersesMarked> _versesMarkedArrayList) {
        Log.d(TAG, "VersesFragment: REcyclerview: UPDATE: " + _versesMarkedArrayList.size());
        listOfVersesMarked.clear();
        listOfVersesMarked.addAll(_versesMarkedArrayList);
        //I have new data, delete everything and add new data
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DialogVersesMarkedInOneVerseRV.VersesDialogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.verses_marked_in_one_verse_item,parent, false);
        return new DialogVersesMarkedInOneVerseRV.VersesDialogViewHolder(rootView);
    }
    @Override
    public void onBindViewHolder(@NonNull DialogVersesMarkedInOneVerseRV.VersesDialogViewHolder holder, int position) {
        holder.bind();
    }
    @Override
    public int getItemViewType(int position) {
        return position;

    }
    @Override
    public int getItemCount() {
        return this.listOfVersesMarked.size();
    }

    class VersesDialogViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        CardView cardview;
        MaterialButton labelButton;
        TextView txtView_verse_title;
        TextView noteTextView;

        public VersesDialogViewHolder(@NonNull View itemView) {
            super(itemView);
            cardview = itemView.findViewById(R.id.verses_marked_in_one_cardview);
            labelButton = itemView.findViewById(R.id.verses_marked_in_one_label);
            txtView_verse_title = itemView.findViewById(R.id.verses_marked_in_one_tv);
            noteTextView = itemView.findViewById(R.id.verses_marked_in_one_note);
            itemView.setOnClickListener(this);
        }

        void bind() {
            Log.d(TAG, "bind: position dialog "+ getAdapterPosition());
            VersesMarked versesMarked = listOfVersesMarked.get(getAdapterPosition());
            if(versesMarked.hasNote()){
                noteTextView.setText(versesMarked.getNote());
            }
            ArrayList<Integer> selectedItems = new ArrayList<>();
            for (Integer key : versesMarked.getVerseTextDict().keySet()) {
                selectedItems.add(key - 1);
            }
            String titleChapterVerses = BookHelper.getTitleBookAndCaps(versesMarked.getChapter(), selectedItems);
            txtView_verse_title.setText(versesMarked.getBook().getName() + " " + titleChapterVerses);
            Label mLabel = versesMarked.getLabel();
            labelButton.setText(mLabel.getName());
            try{
                int color = Color.parseColor(mLabel.getColor());
                ColorStateList colorState = ColorStateList.valueOf(color);
                labelButton.setTextColor(color);
                labelButton.setIconTint(colorState);
                labelButton.setStrokeColor(colorState);
            } catch (IllegalArgumentException e){
            }
        }

        @Override
        public void onClick(View v) {
            // here you can get your item by calling getAdapterPosition();int id, int book_number, String name, int numcap
            //Book book = BookHelper.getBook(230);
            VersesMarked verseMarked = listOfVersesMarked.get(getAdapterPosition());
            //VersesMarked test = new VersesMarked("uuid", book, label, 4, 2, "ANY TEXT", "ANY NOTE");
            goToEditVersesMarked(verseMarked);
            dialog.dismiss();
        }
    }

    public void goToEditVersesMarked(VersesMarked versesMarked){
        FragmentManager fragmentManager = ((AppCompatActivity) ctxDialog).getSupportFragmentManager();
        VersesMarkedEdit newFragment = VersesMarkedEdit.newInstance(versesMarked);
        // fragment fullscreen
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // For a little polish, specify a transition animation
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        // To make it fullscreen, use the 'content' root view as the container
        // for the fragment, which is always the root view for the activity
        transaction.add(android.R.id.content, newFragment)//R.id.dashboard_fragment-> remove margin in verses_marked_dialog_edit.xml
                .addToBackStack(null).commit();
    }


}