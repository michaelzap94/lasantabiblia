package com.michaelzap94.santabiblia.adapters.dialogs;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.michaelzap94.santabiblia.R;
import com.michaelzap94.santabiblia.models.Label;

import java.util.ArrayList;

public class DialogVersesMarkedInOneVerseRV extends RecyclerView.Adapter<DialogVersesMarkedInOneVerseRV.VersesDialogViewHolder> {
    private static final String TAG = "DialogVersesMarkedInORV";
    private ArrayList<Label> listOfLabels;
    private Context ctxDialog;
    int totalSize;

    public DialogVersesMarkedInOneVerseRV(Context ctxDialog, ArrayList<Label> listOfLabels) {
        this.listOfLabels = listOfLabels;
        this.ctxDialog = ctxDialog;
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
        return this.listOfLabels.size();
    }

    class VersesDialogViewHolder extends RecyclerView.ViewHolder {
        MaterialButton labelButton;
        TextView txtView_verse_title;

        public VersesDialogViewHolder(@NonNull View itemView) {
            super(itemView);
            labelButton = itemView.findViewById(R.id.verses_marked_in_one_label);
            txtView_verse_title = itemView.findViewById(R.id.verses_marked_in_one_tv);
        }

        void bind() {
            Log.d(TAG, "bind: position dialog "+ getAdapterPosition());
            Label mLabel = listOfLabels.get(getAdapterPosition());

            try{
                int color = Color.parseColor(mLabel.getColor());
                ColorStateList colorState = ColorStateList.valueOf(color);
                labelButton.setTextColor(color);
                labelButton.setIconTint(colorState);
                labelButton.setStrokeColor(colorState);
            } catch (IllegalArgumentException e){
            }

        }
    }


}