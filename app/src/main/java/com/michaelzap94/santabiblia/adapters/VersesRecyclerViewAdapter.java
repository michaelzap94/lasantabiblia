package com.michaelzap94.santabiblia.adapters;

import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.michaelzap94.santabiblia.R;
import com.michaelzap94.santabiblia.models.Verse;

import java.util.ArrayList;
import java.util.List;

public class VersesRecyclerViewAdapter extends RecyclerView.Adapter<VersesRecyclerViewAdapter.VersesViewHolder> {
    private static final String TAG = "VersesRecyclerViewAdapt";
    ArrayList<Verse> verseArrayList;

    public VersesRecyclerViewAdapter(ArrayList<Verse> verseArrayList) {
        this.verseArrayList = verseArrayList;
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
        Verse verse = verseArrayList.get(position);
        //VersesRecyclerViewAdapter.VersesViewHolder viewHolder = (VersesRecyclerViewAdapter.VersesViewHolder) holder;
        //viewHolder.txtView_title.setText(verse.getText());
        holder.bind(verse);
    }

    @Override
    public int getItemCount() {
        return verseArrayList.size();
    }

    class VersesViewHolder extends RecyclerView.ViewHolder {
        TextView txtView_title;

        public VersesViewHolder(@NonNull View itemView) {
            super(itemView);
            txtView_title = itemView.findViewById(R.id.txtView_title);
        }

        void bind(Verse verse) {
//            SpannableString ss = new SpannableString("Android is a Software stack");
//            ClickableSpan clickableSpan = new ClickableSpan() {
//                @Override
//                public void onClick(View textView) {
//                    startActivity(new Intent(MyActivity.this, NextActivity.class));
//                }
//                @Override
//                public void updateDrawState(TextPaint ds) {
//                    super.updateDrawState(ds);
//                    ds.setUnderlineText(false);
//                }
//            };
//            ss.setSpan(clickableSpan, 22, 27, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//            TextView textView = (TextView) findViewById(R.id.hello);
//            textView.setText(ss);
//            textView.setMovementMethod(LinkMovementMethod.getInstance());
//            textView.setHighlightColor(Color.TRANSPARENT);
            //Bind data to layout elements
            txtView_title.setText(verse.getText());

        }
    }
}
