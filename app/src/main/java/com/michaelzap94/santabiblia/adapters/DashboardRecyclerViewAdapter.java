package com.michaelzap94.santabiblia.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.michaelzap94.santabiblia.R;
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

            int[][] states = new int[][] {
                    new int[] { android.R.attr.state_enabled}, // enabled
                    new int[] {-android.R.attr.state_enabled}, // disabled
                    new int[] {-android.R.attr.state_checked}, // unchecked
                    new int[] { android.R.attr.state_pressed}  // pressed
            };

            int[] colors = new int[] {
                    Color.BLACK,
                    Color.RED,
                    Color.GREEN,
                    Color.BLUE
            };

            ColorStateList myList = new ColorStateList(states, colors);

            int labelPosition = getAdapterPosition();
            Label mLabel = labelArrayList.get(labelPosition);
            labelButton.setText(mLabel.getName());
            labelButton.setTextColor(myList);
            labelButton.setStrokeColor(myList);

        }
    }

}
