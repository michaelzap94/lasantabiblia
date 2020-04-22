package com.michaelzap94.santabiblia.adapters;

import android.app.Activity;
import android.app.Dialog;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.michaelzap94.santabiblia.Bible;
import com.michaelzap94.santabiblia.R;
import com.michaelzap94.santabiblia.dialogs.GridAdapter;
import com.michaelzap94.santabiblia.models.Verse;

import java.util.ArrayList;
import java.util.List;

public class VersesRecyclerViewAdapter extends RecyclerView.Adapter<VersesRecyclerViewAdapter.VersesViewHolder> {
    private static final String TAG = "VersesRecyclerViewAdapt";
    ArrayList<Verse> verseArrayList;
    private Activity ctx;

    public VersesRecyclerViewAdapter(Activity ctx, ArrayList<Verse> verseArrayList) {
        this.verseArrayList = verseArrayList;
        this.ctx = ctx;
        Log.d(TAG, "VersesFragment: REcyclerview: INIT: "+ verseArrayList.size());
    }

    //function available so View can update the RecyclerView List once the information is available.
    public void updateVersesRecyclerView(List<Verse> _verseArrayList) {
        Log.d(TAG, "VersesFragment: REcyclerview: UPDATE: "+ _verseArrayList.size());
        verseArrayList.clear();
        verseArrayList.addAll(_verseArrayList);
        //I have new data, delete everything and add new data
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VersesRecyclerViewAdapter.VersesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.verses_adapter_item,parent,false);
        return new VersesRecyclerViewAdapter.VersesViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull VersesRecyclerViewAdapter.VersesViewHolder holder, int position) {
//        Log.d(TAG, "onBindViewHolder: position "+position);
//        Log.d(TAG, "holder: position "+holder.getAdapterPosition());
//        //Verse verse = verseArrayList.get(holder.getAdapterPosition());
//        //VersesRecyclerViewAdapter.VersesViewHolder viewHolder = (VersesRecyclerViewAdapter.VersesViewHolder) holder;
//        //viewHolder.txtView_title.setText(verse.getText());
        holder.bind();
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getItemCount() {
        return verseArrayList.size();
    }

    class VersesViewHolder extends RecyclerView.ViewHolder {
        TextView txtView_title;
        TextView txtView_verse;

        public VersesViewHolder(@NonNull View itemView) {
            super(itemView);
            txtView_title = itemView.findViewById(R.id.txtView_title);
            txtView_verse = itemView.findViewById(R.id.txtView_verse);
        }

        void bind() {
            //Log.d(TAG, "bind: position "+ getAdapterPosition());
            Verse verse = verseArrayList.get(getAdapterPosition());

            Spanned spannedTextVerse = verse.getTextSpanned();

            if(spannedTextVerse.toString().indexOf("[") > -1 && spannedTextVerse.toString().indexOf("†]") > -1){
                SpannableString ss = new SpannableString(spannedTextVerse);
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View textView) {
                        if(textView instanceof TextView){
                            TextView tv = (TextView) textView;
                            if(tv.getText() instanceof Spanned) {
                                Spanned s = (Spanned) tv.getText();
                                int start = s.getSpanStart(this);
                                int end = s.getSpanEnd(this);
                                Log.d(TAG, "onClick " +verse.getBookId() + " " + s.subSequence(start, end));
                                openDialogConc(verse.getBookId(), s.subSequence(start, end).toString());
                            }
                        }
                    }
                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(false);
                    }
                };
                ss.setSpan(clickableSpan, spannedTextVerse.toString().indexOf("["), spannedTextVerse.toString().indexOf("]") + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                txtView_verse.setText(ss, TextView.BufferType.SPANNABLE);
                txtView_verse.setMovementMethod(LinkMovementMethod.getInstance());
                txtView_verse.setHighlightColor(Color.TRANSPARENT);

            } else {
                txtView_verse.setText(spannedTextVerse);
            }

            //Log.d(TAG, "position: verse.getTextTitle()" + getAdapterPosition() + " null: " +  ((boolean) (null == verse.getTextTitle())));


            //Bind data to layout elements
            if(verse.getTextTitle() != null){
                txtView_title.setText(verse.getTextTitle());
                txtView_title.setVisibility(View.VISIBLE);
            }

        }
    }

    public void openDialogConc(int bookId, String elementClicked) {


        DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();

        int DeviceTotalWidth = metrics.widthPixels;
        int DeviceTotalHeight = metrics.heightPixels;

        final Dialog dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_chapters_grid);
        dialog.getWindow().setLayout(DeviceTotalWidth ,DeviceTotalHeight);
        dialog.show();
    }

//    public void openDialogConc(View textView) {
//        Log.d(TAG, "openDialogConc: " + textView);
//        DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
//
//        int DeviceTotalWidth = metrics.widthPixels;
//        int DeviceTotalHeight = metrics.heightPixels;
//
//        final Dialog dialog = new Dialog(ctx);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.dialog_chapters_grid);
//        dialog.getWindow().setLayout(DeviceTotalWidth ,DeviceTotalHeight);
//        dialog.show();
//    }
}
