package com.michaelzap94.santabiblia.dialogs;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;

import com.michaelzap94.santabiblia.R;
import com.michaelzap94.santabiblia.models.Book;
import com.michaelzap94.santabiblia.models.Verse;


import java.util.ArrayList;

public class GridAdapter extends BaseAdapter {

    Context mContext;
    int chapters;

    public GridAdapter(Context context, int chapters) {
        this.mContext = context;
        this.chapters = chapters;
    }

    @Override
    public int getCount() {
        return chapters;
    }

    @Override
    public Object getItem(int position) {
        return position + 1;
    }

    @Override
    public long getItemId(int position) {
        return position + 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.dialog_chapter_item, parent, false);
        }

        Button dialogChapterButton = (Button) convertView.findViewById(R.id.dialog_chapter_button);
        dialogChapterButton.setText(String.valueOf(position + 1));
        dialogChapterButton.setOnClickListener(v -> Log.d("TAG", "after onItemClick: button clicked "));

        return convertView;
    }
}