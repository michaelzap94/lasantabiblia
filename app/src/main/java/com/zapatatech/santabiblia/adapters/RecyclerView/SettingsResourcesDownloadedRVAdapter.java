package com.zapatatech.santabiblia.adapters.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zapatatech.santabiblia.R;

import java.util.ArrayList;

public class SettingsResourcesDownloadedRVAdapter extends RecyclerView.Adapter<SettingsResourcesDownloadedRVAdapter.ViewHolder> {
    private static final String TAG = "SettingsResourcesDownlo";
    private Context context;
    private ArrayList<String> list;
    private ArrayList<String> listDisplayName;
    public SettingsResourcesDownloadedRVAdapter(Context context, ArrayList<String> list, ArrayList<String> listDisplayName) {
        this.context = context;
        this.list = list;
        this.listDisplayName = listDisplayName;
    }
    public void refreshData(ArrayList<String> _list, ArrayList<String> _listDisplayName){
        list.clear();
        listDisplayName.clear();
        list.addAll(_list);
        listDisplayName.addAll(_listDisplayName);
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public SettingsResourcesDownloadedRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.resource_item, parent, false);
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
        TextView resourceName;
        //TextView resourceInfo;
        TextView resourceState;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            resourceName = itemView.findViewById(R.id.fileName);
            //resourceInfo = itemView.findViewById(R.id.fileInfo);
            resourceState = itemView.findViewById(R.id.fileState);
        }

        void bind() {
            String fileName = list.get(getAdapterPosition());
            String displayName = listDisplayName.get(getAdapterPosition());
            resourceName.setText(displayName);
            //-------------------------------------
            resourceState.setText("Remove");
            resourceState.setTextColor(Color.RED);
            resourceState.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_delete, 0);
            resourceState.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    context.deleteDatabase(fileName);
                    removeAt(getAdapterPosition());
                }
            });

        }
    }

    public void removeAt(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, list.size());
    }
}
