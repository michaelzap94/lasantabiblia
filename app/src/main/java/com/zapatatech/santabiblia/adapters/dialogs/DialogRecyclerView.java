package com.zapatatech.santabiblia.adapters.dialogs;

import android.content.Context;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zapatatech.santabiblia.R;
import com.zapatatech.santabiblia.models.Verse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DialogRecyclerView extends RecyclerView.Adapter<DialogRecyclerView.VersesDialogViewHolder> {
    private static final String TAG = "DialogRecyclerView";
    HashMap<String, ArrayList<Verse>> verseDialogHashMap;
    private Context ctxDialog;
    int totalSize;
    ArrayList<Verse> versesArrayList = new ArrayList<>();
    public DialogRecyclerView(Context ctxDialog, HashMap<String, ArrayList<Verse>> verseDialogHashMap) {
        this.verseDialogHashMap = verseDialogHashMap;
        this.ctxDialog = ctxDialog;
        //this.totalSize = verseDialogHashMap.size();
        for (Map.Entry mapElement : this.verseDialogHashMap.entrySet()) {
            String title = (String) mapElement.getKey();
            ArrayList<Verse> innerVersesArrayList = (ArrayList<Verse>) mapElement.getValue();
            this.versesArrayList.add(new Verse(0, 0, 0, null, title, null));
            this.versesArrayList.addAll(innerVersesArrayList);
            //Log.d(TAG,title + " : " + verse.get(0).getTextSpanned().toString());
//            Log.d(TAG, "SIZE: " + verse.size());
            //this.totalSize = this.totalSize + versesArrayList.size();
        }

        Log.d(TAG, "VersesFragment: REcyclerview: INIT: "+ verseDialogHashMap.size());
    }

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
        return position;

    }
    @Override
    public int getItemCount() {
        Log.d(TAG, "RV getItemCount: dialog " +  this.versesArrayList.size());
        return this.versesArrayList.size();
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
            Verse verse = versesArrayList.get(getAdapterPosition());
            if(verse.getTextSpanned() != null){
                Spanned spannedTextVerse = verse.getTextSpanned();
                //String stringTextVerse = spannedTextVerse.toString();
                Log.d(TAG, "bind: inside dialog: " + spannedTextVerse.toString());
                txtView_verse_dialog.setText(spannedTextVerse);
            }
            //Log.d(TAG, "position: verse.getTextTitle()" + getAdapterPosition() + " null: " +  ((boolean) (null == verse.getTextTitle())));
            //Bind data to layout elements
            if(verse.getTextTitle() != null){
                txtView_title_dialog.setText(verse.getTextTitle());
                txtView_title_dialog.setVisibility(View.VISIBLE);
            }

        }
    }


}
