package com.michaelzap94.santabiblia.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.michaelzap94.santabiblia.R;

import java.util.ArrayList;

public class SearchResultsRecyclerView extends RecyclerView.Adapter<SearchResultsRecyclerView.ViewHolder> {
    private static final String TAG = "SearchResultsRecyclerVi";
    ArrayList<String[]> results;
    public SearchResultsRecyclerView(ArrayList<String[]> results){
        Log.d(TAG, "SearchResultsRecyclerView: " + results.size());
        this.results = results;
    }

    public void refreshSearchResults(ArrayList<String[]> _results){
        Log.d(TAG, "SearchResultsRecyclerView: " + _results.size());
        results.clear();
        results.addAll(_results);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchResultsRecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_adapter_item, parent,false);
        return new SearchResultsRecyclerView.ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultsRecyclerView.ViewHolder holder, int position) {
        holder.bind();
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtView_title;
        TextView txtView_content;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtView_title = itemView.findViewById(R.id.search_result_title);
            txtView_content = itemView.findViewById(R.id.search_result_content);
        }
        void bind() {
            //Bind data to layout elements
            int i = getAdapterPosition();
            Log.d(TAG, "bind: " + i + " " + results.get(i)[0]);
            txtView_title.setText(results.get(i)[0]);
            txtView_content.setText(results.get(i)[1]);

        }
    }
}
