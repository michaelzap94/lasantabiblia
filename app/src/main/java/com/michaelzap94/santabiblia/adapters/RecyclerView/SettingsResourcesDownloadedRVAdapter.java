package com.michaelzap94.santabiblia.adapters.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.michaelzap94.santabiblia.R;

import java.util.ArrayList;

public class SettingsResourcesDownloadedRVAdapter extends RecyclerView.Adapter<SettingsResourcesDownloadedRVAdapter.ViewHolder> {
    private static final String TAG = "SettingsResourcesDownlo";
    private Context context;
    private ArrayList<String> list;
    public SettingsResourcesDownloadedRVAdapter(Context context, ArrayList<String> list) {
        this.context = context;
        this.list = list;
    }
    public void refreshData(ArrayList<String> _list){
        list.clear();
        list.addAll(_list);
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public SettingsResourcesDownloadedRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.verses_marked_dialog_edit_item, parent, false);
        return new SettingsResourcesDownloadedRVAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull SettingsResourcesDownloadedRVAdapter.ViewHolder holder, int position) {
        holder.bind();
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        public ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.verses_marked_dialog_edit_item_tv);
        }
        void bind() {
            String nameValue = list.get(getAdapterPosition());
            name.setText(nameValue);
        }
    }
}
