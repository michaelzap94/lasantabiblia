package com.zapatatech.santabiblia.adapters.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.zapatatech.santabiblia.DatabaseHelper.BibleDBHelper;
import com.zapatatech.santabiblia.R;
import com.zapatatech.santabiblia.fragments.dialogs.VersesInsideDialog;
import com.zapatatech.santabiblia.fragments.settings.InnerPreferencesFragment;
import com.zapatatech.santabiblia.models.Verse;
import com.zapatatech.santabiblia.utilities.BookHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VersesRecyclerViewAdapter extends RecyclerView.Adapter<VersesRecyclerViewAdapter.VersesViewHolder> {
    private static final String TAG = "VersesRecyclerViewAdapt";
    private ArrayList<Verse> verseArrayList;
    private Context ctx;
    private SparseBooleanArray selectedItems;

    public VersesRecyclerViewAdapter(Context ctx, ArrayList<Verse> verseArrayList) {
        this.verseArrayList = verseArrayList;
        this.ctx = ctx;
        this.selectedItems = new SparseBooleanArray();
    }

    public Verse getVerseArrayListItem(int position){
        return verseArrayList.get(position);
    }

    //function available so View can update the RecyclerView List once the information is available.
    public void updateVersesRecyclerView(List<Verse> _verseArrayList) {
        Log.d(TAG, "VersesFragment: REcyclerview: UPDATE: "+ _verseArrayList.size());
        verseArrayList.clear();
        verseArrayList.addAll(_verseArrayList);
        //I have new data, delete everything and add new data
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VersesRecyclerViewAdapter.VersesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.ctx = parent.getContext();
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.verses_adapter_item,parent,false);
        return new VersesRecyclerViewAdapter.VersesViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull VersesRecyclerViewAdapter.VersesViewHolder holder, int position) {
        boolean selected = selectedItems.get(position, false);
        holder.bind(selected);
        //sets the state of the LinearLayout to activated
        holder.itemView.setActivated(selected);

    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getItemCount() {
        return verseArrayList.size();
    }

    class VersesViewHolder extends RecyclerView.ViewHolder {
        TextView txtView_title;
        //TextView txtView_title_refs;
        TextView txtView_verse;

        public VersesViewHolder(@NonNull View itemView) {
            super(itemView);
            txtView_title = itemView.findViewById(R.id.txtView_title);
            //txtView_title_refs = itemView.findViewById(R.id.txtView_title_refs);
            txtView_verse = itemView.findViewById(R.id.txtView_verse);
        }

        void bind(boolean isSelected) {
            Verse verse = verseArrayList.get(getAdapterPosition());
            Spanned spannedTextVerse = verse.getTextSpanned();
            SpannableString ssTextVerse = verse.getTextSpannableString();
            String stringTextVerse = spannedTextVerse.toString();

            if(stringTextVerse.indexOf("[") > -1 && stringTextVerse.indexOf("]") > -1){

                int start = stringTextVerse.indexOf("[");
                int end = stringTextVerse.indexOf("]");
                //int index = stringTextVerse.indexOf("†");

                while (start >= 0 && end >= 0) {
                    Log.d(TAG, "start: " + start + "char: " + stringTextVerse.charAt(start));
                    Log.d(TAG, "end: " + end + "char: " + stringTextVerse.charAt(end));
                    //Log.d(TAG, "index: " + index + "char: " + stringTextVerse.charAt(index));

                    ClickableSpan clickableSpan = new ClickableSpan() {
                        @Override
                        public void onClick(View textView) {
                            if(textView instanceof TextView){
                                TextView tv = (TextView) textView;
                                if(tv.getText() instanceof Spanned) {
                                    Spanned s = (Spanned) tv.getText();
                                    int start = s.getSpanStart(this);
                                    int end = s.getSpanEnd(this);
                                    Log.d(TAG, "ClickableSpan onClick " +verse.getBookNumber() + " " + s.subSequence(start, end));
                                    openDialogReferencesMaterial(verse.getBookNumber(), s.subSequence(start, end).toString(), null);
                                }
                            }
                        }
                        @Override
                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setUnderlineText(false);
                        }
                    };
                    ssTextVerse.setSpan(clickableSpan, start, end + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ssTextVerse.setSpan(new ForegroundColorSpan(Color.BLUE), start, end + 1, 0);
                    txtView_verse.setMovementMethod(LinkMovementMethod.getInstance());
                    txtView_verse.setHighlightColor(Color.TRANSPARENT);

                    start = stringTextVerse.indexOf("[", start + 1);
                    end = stringTextVerse.indexOf("]", end + 1);
                    //index = stringTextVerse.indexOf("†", index + 1);
                }
                txtView_verse.setText(ssTextVerse, TextView.BufferType.SPANNABLE);

            } else {
                txtView_verse.setText(ssTextVerse);
            }

            if(isSelected){
                txtView_verse.setPaintFlags(txtView_verse.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            } else {
                //if text is underlined and it should not be selected, unselect it
                if ((txtView_verse.getPaintFlags() & Paint.UNDERLINE_TEXT_FLAG) > 0) {
                    txtView_verse.setPaintFlags( txtView_verse.getPaintFlags() & (~ Paint.UNDERLINE_TEXT_FLAG));
                }
            }
            //Log.d(TAG, "position: verse.getTextTitle()" + getAdapterPosition() + " null: " +  ((boolean) (null == verse.getTextTitle())));
            //Bind data to layout elements
//            if(verse.getTextTitle() != null){
//                txtView_title.setText(verse.getTextTitle());
//                txtView_title.setVisibility(View.VISIBLE);
//            }
//
//            if(verse.getTextTitleRefs() != null) {
//                txtView_title_refs.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        openDialogReferencesMaterial(verse.getBookNumber(), null, verse.getTextTitleRefs());
//                    }
//                });
//                txtView_title_refs.setVisibility(View.VISIBLE);
//            }

            String textTitle = verse.getTextTitle();
            if(textTitle != null){
                txtView_title.setVisibility(View.VISIBLE);
                String[] textTitleRefs = verse.getTextTitleRefs();
                if(textTitleRefs != null){
                    String refs = "[Ref]";
                    int startRef = refs.indexOf("[");
                    int endRef = refs.indexOf("]");

                    SpannableString ssTextTitleOriginal = new SpannableString(textTitle);
                    SpannableString ssTextTitle = new SpannableString(refs);

                    ClickableSpan clickableSpan = new ClickableSpan() {
                        @Override
                        public void onClick(View textView) {
                            openDialogReferencesMaterial(verse.getBookNumber(), null, textTitleRefs);
                        }
                        @Override
                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setUnderlineText(false);
                            ds.setColor(Color.BLUE);
                        }
                    };
                    ssTextTitle.setSpan(clickableSpan, startRef, endRef + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    txtView_title.setMovementMethod(LinkMovementMethod.getInstance());
                    txtView_title.setHighlightColor(Color.TRANSPARENT);
                    txtView_title.setText(TextUtils.concat(ssTextTitle," ",ssTextTitleOriginal), TextView.BufferType.SPANNABLE);
                } else {
                    txtView_title.setText(textTitle);
                }
            }
        }
    }
    public void openDialogVerses2(String title, HashMap<String, ArrayList<Verse>> versesFromCommentaries){
        VersesInsideDialog vid = new VersesInsideDialog(title, versesFromCommentaries);
        vid.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
        vid.show(((AppCompatActivity) ctx).getSupportFragmentManager(),"anything");
    }
    public void openDialogReferencesMaterial(int bookNumber, String elementClicked, String[] textTitleRefs){
        Context context = new ContextThemeWrapper(ctx, R.style.AppTheme2);
        final String[] arrReturned;
        final String[] arrToShow;
        if(textTitleRefs!=null) {
            arrReturned = textTitleRefs;
            arrToShow = new String[arrReturned.length];
            for (int i = 0; i < arrReturned.length ; i++) {
                try{
                    String[] parsed = Html.fromHtml(arrReturned[i]).toString().split(" ");
                    int book_number = Integer.parseInt(parsed[0]);
                    String bookName = BookHelper.getBook(book_number).getName();
                    arrToShow[i] = bookName + " " + parsed[1];
                } catch (Exception e) {
                    continue;
                }
            }
        } else {
            arrReturned = BibleDBHelper.getInstance(ctx).getReferences(bookNumber, elementClicked);
            arrToShow = new String[arrReturned.length];
            for (int i = 0; i < arrReturned.length ; i++) {
                arrToShow[i] = Html.fromHtml(arrReturned[i]).toString();
            }
        }
        new MaterialAlertDialogBuilder(context)
                .setTitle("Referencias:")
                .setItems(arrToShow,  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {
                        Log.d(TAG, "onClick: position dialog: " + position);
                        //TODO: MOVE IT TO THE BACKGROUND
                        HashMap<String, ArrayList<Verse>> versesFromCommentaries = BibleDBHelper.getInstance(ctx).getVersesFromCommentaries(arrReturned[position]);
                        Log.d(TAG,"Created hashmap size: " + versesFromCommentaries.size());
                        openDialogVerses2(arrToShow[position], versesFromCommentaries);

                    }
                }).show();
    }

    //ITEM SELECTED========================================================================
    public int toggleSelection(int pos) {
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
        }
        else {
            selectedItems.put(pos, true);
        }
        notifyItemChanged(pos);
        return getSelectedItemCount();
    }

    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }
//========================================================================

}
