package com.michaelzap94.santabiblia.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.ViewPager;

import com.michaelzap94.santabiblia.Bible;
import com.michaelzap94.santabiblia.R;
import com.michaelzap94.santabiblia.models.Book;
import com.michaelzap94.santabiblia.models.Verse;
import com.michaelzap94.santabiblia.utilities.BookHelper;


import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class GridAdapter extends BaseAdapter {

    Context mContext;
    int chapters;
    ViewPager viewPager;
    Dialog dialog;

    public GridAdapter(Context context, ViewPager viewPager, Dialog  dialog, int chapters) {
        this.mContext = context;
        this.chapters = chapters;
        this.viewPager = viewPager;
        this.dialog = dialog;
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
        dialogChapterButton.setOnClickListener(v -> {
            this.viewPager.setCurrentItem(position);
            this.dialog.dismiss();
        });
        return convertView;
    }
}