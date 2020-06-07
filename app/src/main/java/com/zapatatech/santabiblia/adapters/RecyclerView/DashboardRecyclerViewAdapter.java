package com.zapatatech.santabiblia.adapters.RecyclerView;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.zapatatech.santabiblia.Bible;
import com.zapatatech.santabiblia.Dashboard;
import com.zapatatech.santabiblia.R;
import com.zapatatech.santabiblia.fragments.dashboard.DashboardMainFragment;
import com.zapatatech.santabiblia.fragments.ui.tabVerses.VersesFragment;
import com.zapatatech.santabiblia.models.Label;
import com.zapatatech.santabiblia.viewmodel.VersesViewModel;

import java.util.ArrayList;

public class DashboardRecyclerViewAdapter extends RecyclerView.Adapter<DashboardRecyclerViewAdapter.DashboardViewHolder> {
    private static final String TAG = "DashboardRecyclerViewAd";
    private ArrayList<Label> labelArrayList;
    private Context ctx;
    private int chapter_number;
    private int book_number;
    private VersesRecyclerViewAdapter rvAdapter;
    private VersesViewModel viewModel;
    private ActionMode actionMode;
    public void setActionMode(ActionMode actionMode) {
        this.actionMode = actionMode;
    }
    public DashboardRecyclerViewAdapter(Context ctx, ArrayList<Label> labelArrayList) {
        this.ctx = ctx;
        this.labelArrayList = labelArrayList;
        this.chapter_number = 0;
        this.rvAdapter = null;
    }

    public DashboardRecyclerViewAdapter(Context ctx, ArrayList<Label> labelArrayList, int book_number,  int chapter_number, ActionMode actionMode, VersesRecyclerViewAdapter rvAdapter, VersesViewModel viewModel) {
        this.ctx = ctx;
        this.labelArrayList = labelArrayList;
        this.book_number = book_number;
        this.chapter_number = chapter_number;
        this.actionMode = actionMode;
        this.rvAdapter = rvAdapter;
        this.viewModel = viewModel;
    }

    public void refreshData(ArrayList<Label> _labelArrayList){
        labelArrayList.clear();
        labelArrayList.addAll(_labelArrayList);
        notifyDataSetChanged();
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
                } else if(ctx instanceof Bible && chapter_number > 0 && rvAdapter != null && viewModel != null){
                    VersesFragment.onLabelClickedFromList(ctx, mLabel, book_number, chapter_number, actionMode, rvAdapter, viewModel);
                }
            });


        }
    }


}
