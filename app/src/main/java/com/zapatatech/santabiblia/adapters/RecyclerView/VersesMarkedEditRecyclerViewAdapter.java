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

import java.util.ArrayList;

public class VersesMarkedEditRecyclerViewAdapter extends RecyclerView.Adapter<VersesMarkedEditRecyclerViewAdapter.VersesMarkedViewHolder> {
    private static final String TAG = "VersesMarkedEditRV";
    private ArrayList<String> versesMarkedArrayList;
    private Context ctx;
//    private SparseBooleanArray selectedItems;

    public VersesMarkedEditRecyclerViewAdapter(Context ctx, ArrayList<String> versesMarkedArrayList) {
        this.versesMarkedArrayList = versesMarkedArrayList;
        this.ctx = ctx;
//        this.selectedItems = new SparseBooleanArray();
    }

    //function available so View can update the RecyclerView List once the information is available.
    public void updateVersesMarkedRecyclerView(ArrayList<String> _versesMarkedArrayList) {
        Log.d(TAG, "VersesFragment: REcyclerview: UPDATE: " + _versesMarkedArrayList.size());
        versesMarkedArrayList.clear();
        versesMarkedArrayList.addAll(_versesMarkedArrayList);
        //I have new data, delete everything and add new data
        notifyDataSetChanged();
    }

    public ArrayList<String> getData(){
        return versesMarkedArrayList;
    }

    @NonNull
    @Override
    public VersesMarkedEditRecyclerViewAdapter.VersesMarkedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.verses_marked_dialog_edit_item, parent, false);
        return new VersesMarkedEditRecyclerViewAdapter.VersesMarkedViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull VersesMarkedEditRecyclerViewAdapter.VersesMarkedViewHolder holder, int position) {
        holder.bind();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return versesMarkedArrayList.size();
    }

    class VersesMarkedViewHolder extends RecyclerView.ViewHolder {
        TextView txtView_content;

        public VersesMarkedViewHolder(@NonNull View itemView) {
            super(itemView);
            txtView_content = itemView.findViewById(R.id.verses_marked_dialog_edit_item_tv);
        }

        void bind() {
            Spanned contentSpanned;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                contentSpanned = Html.fromHtml(versesMarkedArrayList.get(getAdapterPosition()), Html.FROM_HTML_MODE_COMPACT);
            } else {
                contentSpanned = Html.fromHtml(versesMarkedArrayList.get(getAdapterPosition()));
            }
            txtView_content.setText(contentSpanned);
        }
    }

    public void removeItem(int position) {
        versesMarkedArrayList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(String item, int position) {
        versesMarkedArrayList.add(position, item);
        notifyItemInserted(position);
    }
}