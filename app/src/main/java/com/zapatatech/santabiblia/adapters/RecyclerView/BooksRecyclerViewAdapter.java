package com.zapatatech.santabiblia.adapters.RecyclerView;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zapatatech.santabiblia.BaseActivityTopDrawer;
import com.zapatatech.santabiblia.R;
import com.zapatatech.santabiblia.models.Book;

import java.util.ArrayList;
import java.util.List;

public class BooksRecyclerViewAdapter extends RecyclerView.Adapter<BooksRecyclerViewAdapter.BooksViewHolder> {
    private static final String TAG = "BooksRecyclerViewAdapt";
    ArrayList<Book> bookArrayList;
    Activity activity;
    public BooksRecyclerViewAdapter(Activity activity, ArrayList<Book> bookArrayList) {
        this.bookArrayList = bookArrayList;
        this.activity = activity;
        Log.d(TAG, "BooksFragment: Recyclerview: INIT: "+ bookArrayList.size());
    }

    //function available so View can update the RecyclerView List once the information is available.
    public void updateBooksRecyclerView(List<Book> _bookArrayList) {
        Log.d(TAG, "BooksFragment: Recyclerview: UPDATE: "+ _bookArrayList.size());
        bookArrayList.clear();
        bookArrayList.addAll(_bookArrayList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BooksRecyclerViewAdapter.BooksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_adapter_item,parent,false);
        return new BooksRecyclerViewAdapter.BooksViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull BooksRecyclerViewAdapter.BooksViewHolder holder, int position) {
        Book book = bookArrayList.get(holder.getAdapterPosition());
        //BooksRecyclerViewAdapter.BooksViewHolder viewHolder = (BooksRecyclerViewAdapter.BooksViewHolder) holder;
        //viewHolder.txtView_title.setText(book.getText());
        holder.bind(book);
        holder.mView.setOnClickListener(v -> {
            Log.d(TAG, "onBindViewHolder: CLICK "+position);
            Log.d(TAG, "onBindViewHolder: CLICK "+holder.getAdapterPosition());
            BaseActivityTopDrawer.onChapterClickedFromDrawer(this.activity, book.getBookNumber());
        });
    }

    @Override
    public int getItemCount() {
        return bookArrayList.size();
    }

    class BooksViewHolder extends RecyclerView.ViewHolder {
        TextView txtView_title;
        View mView;
        public BooksViewHolder(@NonNull View itemView) {
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