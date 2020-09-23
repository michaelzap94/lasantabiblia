package com.zapatatech.santabiblia.adapters.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.zapatatech.santabiblia.R;
import com.zapatatech.santabiblia.fragments.dialogs.NoteDialog;
import com.zapatatech.santabiblia.models.Label;
import com.zapatatech.santabiblia.retrofit.Pojos.POJONote;
import com.zapatatech.santabiblia.utilities.CommonFields;
import com.zapatatech.santabiblia.utilities.CommonMethods;
import com.zapatatech.santabiblia.viewmodel.NotesRepository;

import java.util.ArrayList;
import java.util.Random;

public class DashboardNotesRVA extends RecyclerView.Adapter<DashboardNotesRVA.ViewHolder> {
    private static final String TAG = "DashboardNotesRVA";
    private ArrayList<POJONote> notes;
    private Label mLabel;
    private Context context;

    public DashboardNotesRVA(Label mLabel, ArrayList<POJONote> notes){
        this.notes = notes;
        this.mLabel = mLabel;
    }

    public void refreshData(ArrayList<POJONote> _results){
        Log.d(TAG, "refreshData: " + _results.size());
        notes.clear();
        notes.addAll(_results);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        notes.remove(position);
        notifyItemRemoved(position);
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
    public int getItemViewType(int position) {
        return position;
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
            POJONote mNote = notes.get(position);
            noteTitle.setText(mNote.getTitle());

            Spanned textSpanned;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                textSpanned = Html.fromHtml(mNote.getContent(), Html.FROM_HTML_MODE_COMPACT);
            } else {
                textSpanned = Html.fromHtml(mNote.getContent());
            }
            noteContent.setText(textSpanned);

            //String color = getRandomColor();
            mCardView.setCardBackgroundColor(Color.parseColor(getRandomColor()));

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
                                    goToViewOrEditNotes(mNote, "edit");
                                    return true;
                                case R.id.menu_copy:
                                    CommonMethods.copyText(context, mNote.getTitle(), mNote.getContent());
                                    return true;
                                case R.id.menu_share:
                                    CommonMethods.share(context, mNote.getTitle(), mNote.getContent());
                                    return true;
                                case R.id.menu_delete:
                                    NotesRepository.getInstance().deleteNote(context, DashboardNotesRVA.this, position, mNote.getLabel_id(), mNote.get_id());
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
                    goToViewOrEditNotes(mNote, "view");
                }
            });
        }
    }

    public void goToViewOrEditNotes(POJONote mNote, String actionType){

        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
        NoteDialog newFragment = NoteDialog.newInstance(mLabel, mNote, actionType);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // For a little polish, specify a transition animation
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        // To make it fullscreen, use the 'content' root view as the container
        // for the fragment, which is always the root view for the activity
        transaction.add(R.id.dashboard_fragment, newFragment, "modifyNoteFragmentTag")
                .addToBackStack(null).commit();
    }
}