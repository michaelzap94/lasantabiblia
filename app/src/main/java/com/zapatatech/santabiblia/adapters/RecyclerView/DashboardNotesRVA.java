package com.zapatatech.santabiblia.adapters.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.zapatatech.santabiblia.Login;
import com.zapatatech.santabiblia.R;
import com.zapatatech.santabiblia.models.Note;
import com.zapatatech.santabiblia.utilities.CommonFields;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DashboardNotesRVA extends RecyclerView.Adapter<DashboardNotesRVA.ViewHolder> {
    private static final String TAG = "DashboardNotesRVA";
    private ArrayList<Note> notes;
    private Context context;

    public DashboardNotesRVA(ArrayList<Note> notes){
        this.notes = notes;
    }

    public void refreshData(ArrayList<Note> _results){
        Log.d(TAG, "refreshData: " + _results.size());
        notes.clear();
        notes.addAll(_results);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.note_view_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.noteTitle.setText(notes.get(position).getTitle());
        holder.noteContent.setText(notes.get(position).getContent());
        String color = getRandomColor();
        holder.mCardView.setCardBackgroundColor(Color.parseColor(color));


        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Clicked pos: " + position, Toast.LENGTH_SHORT).show();

//                Intent i = new Intent(v.getContext(), NoteDetails.class);
//                i.putExtra("note_id",notes.get(position).getId());
//                i.putExtra("code", color);
//                v.getContext().startActivity(i);
            }
        });
    }

    private String getRandomColor() {
        Random randomColor = new Random();
        int number = randomColor.nextInt(CommonFields.colors.length);
        return CommonFields.colors[number];
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView noteTitle,noteContent;
        View view;
        CardView mCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.titles);
            noteContent = itemView.findViewById(R.id.content);
            mCardView = itemView.findViewById(R.id.noteCard);
            view = itemView;
        }
    }
}