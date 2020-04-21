package com.michaelzap94.santabiblia.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.michaelzap94.santabiblia.BaseActivityTopDrawer;
import com.michaelzap94.santabiblia.R;
import com.michaelzap94.santabiblia.models.Book;

import java.util.ArrayList;
import java.util.List;

public class BooksRecyclerViewAdapter extends RecyclerView.Adapter<BooksRecyclerViewAdapter.VersesViewHolder> {
    private static final String TAG = "VersesRecyclerViewAdapt";
    ArrayList<Book> verseArrayList;
    Activity activity;
    public BooksRecyclerViewAdapter(Activity activity, ArrayList<Book> verseArrayList) {
        this.verseArrayList = verseArrayList;
        this.activity = activity;
        Log.d(TAG, "BooksFragment: Recyclerview: INIT: "+ verseArrayList.size());
    }

    //function available so View can update the RecyclerView List once the information is available.
    public void updateVersesRecyclerView(List<Book> _verseArrayList) {
        Log.d(TAG, "BooksFragment: Recyclerview: UPDATE: "+ _verseArrayList.size());
        verseArrayList.clear();
        verseArrayList.addAll(_verseArrayList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BooksRecyclerViewAdapter.VersesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_adapter_item,parent,false);
        return new BooksRecyclerViewAdapter.VersesViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull BooksRecyclerViewAdapter.VersesViewHolder holder, int position) {
        Book book = verseArrayList.get(position);
        //BooksRecyclerViewAdapter.VersesViewHolder viewHolder = (BooksRecyclerViewAdapter.VersesViewHolder) holder;
        //viewHolder.txtView_title.setText(book.getText());
        holder.bind(book);
        holder.mView.setOnClickListener(v -> {
            // Here You Do Your Click Magic
            BaseActivityTopDrawer.onChapterClickedFromDrawer(this.activity.getApplicationContext(), position);
        });
    }

    @Override
    public int getItemCount() {
        return verseArrayList.size();
    }

    class VersesViewHolder extends RecyclerView.ViewHolder {
        TextView txtView_title;
        View mView;
        public VersesViewHolder(@NonNull View itemView) {
            super(itemView);
            txtView_title = itemView.findViewById(R.id.book_adapter_textview);
            mView = itemView;
        }
        void bind(Book book) {
            //Bind data to layout elements
            txtView_title.setText(book.getName());
        }
    }
}