package com.michaelzap94.santabiblia.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.michaelzap94.santabiblia.R;

public class LabelColorCreatorRecyclerView extends RecyclerView.Adapter<LabelColorCreatorRecyclerView.ViewHolder> {
    private static final String TAG = "LabelColorCreatorRecycl";
    String[] mColors;
    Context mContext;
    protected ItemListener mListener;
    private int selected = -1;
    View selectedView = null;
    ViewGroup parent;

    public LabelColorCreatorRecyclerView(Context context, String[] colors, ItemListener itemListener) {
        mColors = colors;
        mContext = context;
        mListener=itemListener;
    }
    public int getColorSelected(){
        return selected;
    }
    @Override
    public LabelColorCreatorRecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        View view = LayoutInflater.from(mContext).inflate(R.layout.dashboard_creator_grid_color, parent, false);
        return new LabelColorCreatorRecyclerView.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull LabelColorCreatorRecyclerView.ViewHolder holder, int position) {
        holder.bind();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getItemCount() {
        return mColors.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public FloatingActionButton colorButton;
        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            this.colorButton = (FloatingActionButton) v.findViewById(R.id.dash_creator_grid_color_item);
        }
        void bind() {
            this.colorButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(mColors[getAdapterPosition()])));
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                int position = getAdapterPosition();
                FloatingActionButton selectedColorButton = (FloatingActionButton) view;
                if(selected != -1) {
                    if(selectedView != null ){
                        FloatingActionButton existingColorView = (FloatingActionButton) selectedView;
                        existingColorView.setImageResource(0);
                        selectedColorButton.setImageResource(R.drawable.ic_check_circle);
                        selected = position;
                        selectedView = selectedColorButton;
                    }
                } else {
                    selectedColorButton.setImageResource(R.drawable.ic_check_circle);
                    selectedView = selectedColorButton;
                    selected = position;
                }
                mListener.onItemClick(position);
            }
        }
    }

    public interface ItemListener {
        void onItemClick(int position);
    }
}
