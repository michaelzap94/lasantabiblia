package com.zapatatech.santabiblia.adapters.RecyclerView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zapatatech.santabiblia.R;
import com.zapatatech.santabiblia.SearchSpecific;
import com.zapatatech.santabiblia.models.SearchResult;

import java.util.ArrayList;

public class SearchResultsRecyclerView extends RecyclerView.Adapter<SearchResultsRecyclerView.ViewHolder> {
    private static final String TAG = "SearchResultsRecyclerVi";
    ArrayList<SearchResult> results;
    private Context context;
    private SearchSpecific searchSpecificActivity;
    public SearchResultsRecyclerView(ArrayList<SearchResult> results, SearchSpecific searchSpecific){
        Log.d(TAG, "SearchResultsRecyclerView: " + results.size());
        this.results = results;
        this.searchSpecificActivity = searchSpecific;
    }

    public void refreshSearchResults(ArrayList<SearchResult> _results){
        Log.d(TAG, "SearchResultsRecyclerView: " + _results.size());
        results.clear();
        results.addAll(_results);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchResultsRecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View rootView = LayoutInflater.from(context).inflate(R.layout.search_adapter_item, parent,false);
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
        View _itemView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            _itemView = itemView;
            txtView_title = itemView.findViewById(R.id.search_result_title);
            txtView_content = itemView.findViewById(R.id.search_result_content);
        }
        void bind() {
            //Bind data to layout elements
            SearchResult result = results.get(getAdapterPosition());
            Log.d(TAG, "bind: " + getAdapterPosition() + " " + result.getTitle());
            txtView_title.setText(result.getTitle());
            txtView_content.setText(result.getContent());

            if(result.getIsClickable()){
                _itemView.setOnClickListener(v -> {
                    searchSpecificActivity.onClickSearchSpecific(result);
                });
            }
        }
    }
}
