package com.michaelzap94.santabiblia.adapters;

import android.app.ListActivity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.michaelzap94.santabiblia.BaseActivityTopDrawer;
import com.michaelzap94.santabiblia.Bible;
import com.michaelzap94.santabiblia.Dashboard;
import com.michaelzap94.santabiblia.R;
import com.michaelzap94.santabiblia.fragments.dashboard.DashboardMainFragment;
import com.michaelzap94.santabiblia.fragments.ui.tabVerses.VersesFragment;
import com.michaelzap94.santabiblia.models.Book;
import com.michaelzap94.santabiblia.models.Label;
import com.michaelzap94.santabiblia.models.Verse;

import java.util.ArrayList;

public class DashboardRecyclerViewAdapter extends RecyclerView.Adapter<DashboardRecyclerViewAdapter.DashboardViewHolder> {
    private static final String TAG = "DashboardRecyclerViewAd";
    private ArrayList<Label> labelArrayList;
    private Context ctx;
    private int chapter_number;
    private int book_number;
    private VersesRecyclerViewAdapter rvAdapter;
    private ActionMode actionMode;
    public DashboardRecyclerViewAdapter(Context ctx, ArrayList<Label> labelArrayList) {
        this.ctx = ctx;
        this.labelArrayList = labelArrayList;
        this.chapter_number = -1;
        this.rvAdapter = null;
    }

    public DashboardRecyclerViewAdapter(Context ctx, ArrayList<Label> labelArrayList, int book_number,  int chapter_number, ActionMode actionMode, VersesRecyclerViewAdapter rvAdapter) {
        this.ctx = ctx;
        this.labelArrayList = labelArrayList;
        this.book_number = book_number;
        this.chapter_number = chapter_number;
        this.actionMode = actionMode;
        this.rvAdapter = rvAdapter;
    }

    @NonNull
    @Override
    public DashboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_adapter_item,parent,false);
        return new DashboardRecyclerViewAdapter.DashboardViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardViewHolder holder, int position) {
        holder.bind();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getItemCount() {
        return labelArrayList.size();
    }

    class DashboardViewHolder extends RecyclerView.ViewHolder {
        MaterialButton labelButton;

        public DashboardViewHolder(@NonNull View itemView) {
            super(itemView);
            labelButton = itemView.findViewById(R.id.dash_label_button);
        }

        void bind() {

            //ColorStateList myList = new ColorStateList(states, colors);
            int labelPosition = getAdapterPosition();
            Label mLabel = labelArrayList.get(labelPosition);
            labelButton.setText(mLabel.getName());

            try{
                int color = Color.parseColor(mLabel.getColor());
                ColorStateList colorState = ColorStateList.valueOf(color);
                labelButton.setTextColor(color);
                labelButton.setIconTint(colorState);
                labelButton.setStrokeColor(colorState);
            } catch (IllegalArgumentException e){
            }

            labelButton.setOnClickListener(v -> {
                if(ctx instanceof Dashboard){
                    DashboardMainFragment.onLabelClickedFromList(ctx, mLabel);
                } else if(ctx instanceof Bible && chapter_number > -1 && rvAdapter != null){
                    VersesFragment.onLabelClickedFromList(ctx, mLabel, book_number, chapter_number, actionMode, rvAdapter);
                }
            });


        }
    }

//    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.DOWN | ItemTouchHelper.UP) {
//
//        @Override
//        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//            Toast.makeText(ListActivity.this, "on Move", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        @Override
//        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
//            Toast.makeText(ListActivity.this, "on Swiped ", Toast.LENGTH_SHORT).show();
//            //Remove swiped item from list and notify the RecyclerView
//            int position = viewHolder.getAdapterPosition();
//            arrayList.remove(position);
//            adapter.notifyDataSetChanged();
//
//        }
//    };
}
