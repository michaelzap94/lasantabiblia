package com.zapatatech.santabiblia.adapters.RecyclerView;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zapatatech.santabiblia.R;
import com.zapatatech.santabiblia.utilities.Util;

import java.util.ArrayList;

public class BibleCompareRVA extends RecyclerView.Adapter<BibleCompareRVA.ViewHolder> {
    private static final String TAG = "BibleCompareRVA";
    private ArrayList<String[]> results;
    private Context context;
    public BibleCompareRVA(ArrayList<String[]> results){
        Log.d(TAG, "BibleCompareRVA: " + results.size());
        this.results = results;
    }
    public void refreshData(ArrayList<String[]> _results){
        Log.d(TAG, "BibleCompareRVA: " + _results.size());
        results.clear();
        results.addAll(_results);
        notifyDataSetChanged();
    }
    public ArrayList<String[]> getData(){
        return results;
    }
    public void removeItem(int position) {
        results.remove(position);
        notifyItemRemoved(position);
    }
    public void restoreItem(String[] item, int position) {
        results.add(position, item);
        notifyItemInserted(position);
    }
    @NonNull
    @Override
    public BibleCompareRVA.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View rootView = LayoutInflater.from(context).inflate(R.layout.search_adapter_item, parent,false);
        return new BibleCompareRVA.ViewHolder(rootView);
    }
    @Override
    public void onBindViewHolder(@NonNull BibleCompareRVA.ViewHolder holder, int position) {
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
            String[] result = results.get(getAdapterPosition());

            String[] resultSplit = result[0].split("_");
            String displayName = Util.joinArrayResourceName(" ", true, resultSplit);
            txtView_title.setText(displayName);

            Spanned textSpanned;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                textSpanned = Html.fromHtml(result[1], Html.FROM_HTML_MODE_COMPACT);
            } else {
                textSpanned = Html.fromHtml(result[1]);
            }
            txtView_content.setText(textSpanned);
        }
    }

}
