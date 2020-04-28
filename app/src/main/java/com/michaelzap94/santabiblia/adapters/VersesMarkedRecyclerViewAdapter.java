package com.michaelzap94.santabiblia.adapters;

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

import com.michaelzap94.santabiblia.BuildConfig;
import com.michaelzap94.santabiblia.R;
import com.michaelzap94.santabiblia.models.Label;
import com.michaelzap94.santabiblia.models.VersesMarked;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VersesMarkedRecyclerViewAdapter extends RecyclerView.Adapter<VersesMarkedRecyclerViewAdapter.VersesMarkedViewHolder> {
    private static final String TAG = "VersesRecyclerViewAdapt";
    ArrayList<VersesMarked> versesMarkedArrayList;
    private Context ctx;
//    private SparseBooleanArray selectedItems;

    public VersesMarkedRecyclerViewAdapter(Context ctx, ArrayList<VersesMarked> versesMarkedArrayList) {
        this.versesMarkedArrayList = versesMarkedArrayList;
        this.ctx = ctx;
//        this.selectedItems = new SparseBooleanArray();
    }

    //function available so View can update the RecyclerView List once the information is available.
    public void updateVersesRecyclerView(List<VersesMarked> _versesMarkedArrayList) {
        Log.d(TAG, "VersesFragment: REcyclerview: UPDATE: " + _versesMarkedArrayList.size());
        versesMarkedArrayList.clear();
        versesMarkedArrayList.addAll(_versesMarkedArrayList);
        //I have new data, delete everything and add new data
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VersesMarkedRecyclerViewAdapter.VersesMarkedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_label_adapter_item, parent, false);
        return new VersesMarkedRecyclerViewAdapter.VersesMarkedViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull VersesMarkedRecyclerViewAdapter.VersesMarkedViewHolder holder, int position) {
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
        TextView txtView_note;
        TextView txtView_content;
        public VersesMarkedViewHolder(@NonNull View itemView) {
            super(itemView);
            txtView_title = itemView.findViewById(R.id.verses_marked_cardview_title);
            txtView_note = itemView.findViewById(R.id.verses_marked_cardview_note);
            txtView_content = itemView.findViewById(R.id.verses_marked_cardview_content);
        }

        void bind() {
            VersesMarked versesMarked = versesMarkedArrayList.get(getAdapterPosition());
            Label label = versesMarked.getLabel();
            String bookName = versesMarked.getBook().getName();
            int book_number = versesMarked.getBook().getBookNumber();
            int id = versesMarked.getIdVerseMarked();
            int chapter = versesMarked.getChapter();
            boolean hasNote = versesMarked.hasNote();
//            String note = versesMarked.getNote();
            HashMap<Integer, String> verseTextDict = versesMarked.getVerseTextDict();
            String content = "";
            int verseFrom = 1000;//no chapter has more than 1000 verses, therefore this is enough: Infinity
            int verseTo = 0;
            for (Map.Entry mapElement : verseTextDict.entrySet()) {
                int verseNumber = (Integer) mapElement.getKey();
                if(verseNumber < verseFrom){
                    verseFrom = verseNumber;
                }
                if(verseNumber > verseTo){
                    verseTo = verseNumber;
                }
                String text = (String) mapElement.getValue();
                content = content + " <b>" + verseNumber + "</b>"  + ". " + text;
            }

            Spanned contentSpanned;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                contentSpanned = Html.fromHtml(content, Html.FROM_HTML_MODE_COMPACT);
            } else {
                contentSpanned = Html.fromHtml(content);
            }

            String title = bookName + " " + chapter + ":" + verseFrom  + (verseFrom < verseTo ? "-" + verseTo : BuildConfig.FLAVOR);
            txtView_title.setText(title);
            txtView_content.setText(contentSpanned);
            if(hasNote) {
                txtView_note.setText(versesMarked.getNote());
            }

        }
    }

}
