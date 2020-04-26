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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.michaelzap94.santabiblia.BaseActivityTopDrawer;
import com.michaelzap94.santabiblia.R;
import com.michaelzap94.santabiblia.fragments.dashboard.DashboardMainFragment;
import com.michaelzap94.santabiblia.models.Book;
import com.michaelzap94.santabiblia.models.Label;
import com.michaelzap94.santabiblia.models.Verse;

import java.util.ArrayList;

public class DashboardRecyclerViewAdapter extends RecyclerView.Adapter<DashboardRecyclerViewAdapter.DashboardViewHolder> {
    private static final String TAG = "DashboardRecyclerViewAd";
    ArrayList<Label> labelArrayList;
    Context ctx;

    public DashboardRecyclerViewAdapter(Context ctx, ArrayList<Label> labelArrayList) {
        this.ctx = ctx;
        this.labelArrayList = labelArrayList;
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
                DashboardMainFragment.onLabelClickedFromList(ctx, mLabel);
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
