package com.michaelzap94.santabiblia.adapters.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.michaelzap94.santabiblia.DatabaseHelper.ContentDBHelper;
import com.michaelzap94.santabiblia.R;
import com.michaelzap94.santabiblia.fragments.dialogs.VersesMarkedEdit;
import com.michaelzap94.santabiblia.models.VersesMarked;
import com.michaelzap94.santabiblia.utilities.BookHelper;
import com.michaelzap94.santabiblia.utilities.CommonMethods;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
    public void updateVersesMarkedRecyclerView(List<VersesMarked> _versesMarkedArrayList) {
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
        View btn_copy;
        View btn_share;
        View btn_edit;
        View btn_delete;
        public VersesMarkedViewHolder(@NonNull View itemView) {
            super(itemView);
            txtView_title = itemView.findViewById(R.id.verses_marked_cardview_title);
            txtView_note = itemView.findViewById(R.id.verses_marked_cardview_note);
            txtView_content = itemView.findViewById(R.id.verses_marked_cardview_content);
            btn_copy = itemView.findViewById(R.id.label_item_copy);
            btn_share = itemView.findViewById(R.id.label_item_share);
            btn_edit = itemView.findViewById(R.id.label_item_edit);
            btn_delete = itemView.findViewById(R.id.label_item_delete);
        }

        void bind() {
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

            //String title = bookName + " " + chapter + ":" + verseFrom  + (verseFrom < verseTo ? "-" + verseTo : BuildConfig.FLAVOR);
            String titleChapterVerses = BookHelper.getTitleBookAndCaps(chapter, selectedItems);
            String title = bookName + " " + titleChapterVerses;
            txtView_title.setText(title);
            txtView_content.setText(contentSpanned);
            if(hasNote) {
                txtView_note.setText(versesMarked.getNote());
            }
            this.bindButtonListeners(versesMarked, title, contentSpanned.toString());
        }

        void bindButtonListeners(VersesMarked versesMarked, String title, String content){
            btn_copy.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CommonMethods.copyText(ctx,title, content);
                }
            });
            btn_share.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CommonMethods.share(ctx,title, content);
                }
            });
            btn_edit.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    goToEditVersesMarked(versesMarked);
                }
            });
            btn_delete.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    new AlertDialog.Builder(ctx).setMessage("Desea quitar " + title + " de sus favoritos?").setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            deleteOneFromVersesMarked(versesMarked.getLabel().getId(), versesMarked.getUuid(), getAdapterPosition());
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                }
            });
        }
    }

    public void goToEditVersesMarked(VersesMarked versesMarked){
        FragmentManager fragmentManager = ((AppCompatActivity) ctx).getSupportFragmentManager();
        VersesMarkedEdit newFragment = VersesMarkedEdit.newInstance(versesMarked);
        // fragment fullscreen
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // For a little polish, specify a transition animation
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        // To make it fullscreen, use the 'content' root view as the container
        // for the fragment, which is always the root view for the activity
        transaction.add(android.R.id.content, newFragment)//R.id.dashboard_fragment-> remove margin in verses_marked_dialog_edit.xml
                .addToBackStack(null).commit();
    }

    public void deleteOneFromVersesMarked(int label_id, String uuid, int position){new VersesMarkedRecyclerViewAdapter.RemoveVersesMarked(position).execute(String.valueOf(label_id), uuid);}
    private class RemoveVersesMarked extends AsyncTask<String, Void, Boolean> {
        private int position;
        private RemoveVersesMarked(int position) {
            this.position = position;
        }
        protected Boolean doInBackground(String... args) {
            Log.d(TAG, "doInBackground: " + args[0]);
            return ContentDBHelper.getInstance(VersesMarkedRecyclerViewAdapter.this.ctx).deleteVersesMarkedGroup(Integer.parseInt(args[0]), args[1]);
        }
        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if(success){
                versesMarkedArrayList.remove(this.position);
                notifyItemRemoved(this.position);
            } else {
                Toast.makeText(VersesMarkedRecyclerViewAdapter.this.ctx, "This item could not be deleted", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
