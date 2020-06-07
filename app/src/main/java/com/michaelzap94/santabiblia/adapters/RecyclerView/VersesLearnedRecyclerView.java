package com.michaelzap94.santabiblia.adapters.RecyclerView;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.michaelzap94.santabiblia.DatabaseHelper.ContentDBHelper;
import com.michaelzap94.santabiblia.R;
import com.michaelzap94.santabiblia.fragments.dialogs.VersesLearned;
import com.michaelzap94.santabiblia.models.VersesMarked;
import com.michaelzap94.santabiblia.utilities.BookHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class VersesLearnedRecyclerView extends RecyclerView.Adapter<VersesLearnedRecyclerView.VersesMarkedViewHolder> {
    private static final String TAG = "VersesLearnedRV";
    private ArrayList<VersesMarked> versesMarkedArrayList;
    private Context ctx;
    private VersesLearned listener;
    public VersesLearnedRecyclerView(Context ctx, ArrayList<VersesMarked> versesMarkedArrayList, VersesLearned listener) {
        this.versesMarkedArrayList = versesMarkedArrayList;
        this.ctx = ctx;
        this.listener = listener;
    }

    //function available so View can update the RecyclerView List once the information is available.
    public void updateVersesMarkedRecyclerView(ArrayList<VersesMarked> _versesMarkedArrayList) {
        Log.d(TAG, "VersesFragment: REcyclerview: UPDATE: " + _versesMarkedArrayList.size());
        versesMarkedArrayList.clear();
        versesMarkedArrayList.addAll(_versesMarkedArrayList);
        //I have new data, delete everything and add new data
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VersesLearnedRecyclerView.VersesMarkedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_mem_cardview, parent, false);
        return new VersesLearnedRecyclerView.VersesMarkedViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull VersesLearnedRecyclerView.VersesMarkedViewHolder holder, int position) {
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
        TextView txtView_title;
        TextView txtView_content;
        //        TextView txtView_note;
        FloatingActionButton btn_notlearned;
        public VersesMarkedViewHolder(@NonNull View itemView) {
            super(itemView);
            txtView_title = itemView.findViewById(R.id.verses_marked_cardview_title);
            txtView_content = itemView.findViewById(R.id.verses_marked_cardview_content);
            //txtView_note = itemView.findViewById(R.id.verses_marked_cardview_note);
            btn_notlearned = itemView.findViewById(R.id.main_card_mem_content_notlearned);
        }

        void bind() {
            btn_notlearned.setVisibility(View.VISIBLE);
            VersesMarked versesMarked = versesMarkedArrayList.get(getAdapterPosition());
//            Label label = versesMarked.getLabel();
            String bookName = versesMarked.getBook().getName();
//            int book_number = versesMarked.getBook().getBookNumber();
//            int id = versesMarked.getIdVerseMarked();
            int chapter = versesMarked.getChapter();
            boolean hasNote = versesMarked.hasNote();
//            String note = versesMarked.getNote();
            TreeMap<Integer, String> verseTextDict = versesMarked.getVerseTextDict();
            String content = "";
//            int verseFrom = 1000;//no chapter has more than 1000 verses, therefore this is enough: Infinity
//            int verseTo = 0;
            List<Integer> selectedItems = new ArrayList<>();
            for (Map.Entry<Integer, String> mapElement : verseTextDict.entrySet()) {
                int verseNumber = (Integer) mapElement.getKey();
                selectedItems.add(verseNumber - 1);
                String text = (String) mapElement.getValue();
                content = content + " <b>" + verseNumber + "</b>"  + ". " + text;
            }

            Spanned contentSpanned;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                contentSpanned = Html.fromHtml(content, Html.FROM_HTML_MODE_COMPACT);
            } else {
                contentSpanned = Html.fromHtml(content);
            }

            //String title = bookName + " " + chapter + ":" + verseFrom  + (verseFrom < verseTo ? "-" + verseTo : "");
            String titleChapterVerses = BookHelper.getTitleBookAndCaps(chapter, selectedItems);
            String title = bookName + " " + titleChapterVerses;
            txtView_title.setText(title);
            txtView_content.setText(contentSpanned);
//            if(hasNote) {
//                txtView_note.setText(versesMarked.getNote());
//            }
            btn_notlearned.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    listener.removeFromLearned(versesMarked.getUuid(), getAdapterPosition());
                }
            });
        }
    }

    public void removeItem(int position) {
        versesMarkedArrayList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(VersesMarked item, int position) {
        versesMarkedArrayList.add(position, item);
        notifyItemInserted(position);
    }
}