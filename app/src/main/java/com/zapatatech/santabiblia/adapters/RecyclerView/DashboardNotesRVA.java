package com.zapatatech.santabiblia.adapters.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.zapatatech.santabiblia.R;
import com.zapatatech.santabiblia.retrofit.Pojos.POJONote;
import com.zapatatech.santabiblia.utilities.CommonFields;

import java.util.ArrayList;
import java.util.Random;

public class DashboardNotesRVA extends RecyclerView.Adapter<DashboardNotesRVA.ViewHolder> {
    private static final String TAG = "DashboardNotesRVA";
    private ArrayList<POJONote> notes;
    private Context context;

    public DashboardNotesRVA(ArrayList<POJONote> notes){
        this.notes = notes;
    }

    public void refreshData(ArrayList<POJONote> _results){
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
        holder.bind();
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
        ImageView options;
        View view;
        CardView mCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.titles);
            noteContent = itemView.findViewById(R.id.content);
            mCardView = itemView.findViewById(R.id.noteCard);
            options = itemView.findViewById(R.id.menuIcon);
            view = itemView;
        }

        public void bind(){
            int position = getAdapterPosition();
            noteTitle.setText(notes.get(position).getTitle());
            noteContent.setText(notes.get(position).getContent());
            String color = getRandomColor();
            mCardView.setCardBackgroundColor(Color.parseColor(color));

            options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(context, options);
                    popupMenu.inflate(R.menu.dash_item_menu);
                    popupMenu.show();
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu_edit:
                                    Toast.makeText(context, "edit", Toast.LENGTH_SHORT).show();
                                    return true;
                                case R.id.menu_copy:
                                    Toast.makeText(context, "copy", Toast.LENGTH_SHORT).show();
                                    return true;
                                case R.id.menu_share:
                                    Toast.makeText(context, "share", Toast.LENGTH_SHORT).show();
                                    return true;
                                case R.id.menu_delete:
                                    Toast.makeText(context, "delete", Toast.LENGTH_SHORT).show();
                                    return true;
                                default: return false;
                            }
                        }
                    });
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Clicked pos: " + position, Toast.LENGTH_SHORT).show();
//                Intent i = new Intent(v.getContext(), NoteDetails.class);
//                i.putExtra("note_id",notes.get(position).getId());
////                i.putExtra("code", color);
//                v.getContext().startActivity(i);
                }
            });
        }
    }
}