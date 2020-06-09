package com.zapatatech.santabiblia.models;


import android.os.Parcel;
import android.os.Parcelable;
import android.text.SpannableString;
import android.text.Spanned;

import java.util.ArrayList;

public class Verse implements Parcelable {
    public static final Creator<Verse> CREATOR = new Creator<Verse>() {
        public Verse createFromParcel(Parcel in) {
            return new Verse(in);
        }

        public Verse[] newArray(int size) {
            return new Verse[size];
        }
    };
    public static final int FAVORITO = 1;
    public static final int NO_FAVORITO = 0;
    //protected int id;
    protected int is_fav;
    protected int book_number;
    protected int chapter_number;
    protected int verse;
    protected String text;
    protected String textTitle;
    protected Spanned textSpanned;
    protected SpannableString ssTextVerse;
    protected ArrayList<Label> listOfLabels;

    public Verse(int book_number, int chapter_number, int verse, Spanned text, SpannableString ssTextVerse, String textTitle, ArrayList<Label> listOfLabels) {
        this.book_number = book_number;
        this.chapter_number = chapter_number;
        this.verse = verse;
        //this.text = text;
        this.textSpanned = text;
        this.textTitle = textTitle;
        this.listOfLabels = listOfLabels;
        this.ssTextVerse = ssTextVerse;
    }

    public Verse(int book_number, int chapter_number, int verse, Spanned text, String textTitle, ArrayList<Label> listOfLabels) {
        this.book_number = book_number;
        this.chapter_number = chapter_number;
        this.verse = verse;
        //this.text = text;
        this.textSpanned = text;
        this.textTitle = textTitle;
        this.listOfLabels = listOfLabels;
    }

    public Verse(int book_number, int chapter_number, int verse, Spanned text, ArrayList<Label> listOfLabels) {
        this.book_number = book_number;
        this.chapter_number = chapter_number;
        this.verse = verse;
        this.textTitle = null;
        this.textSpanned = text;
        this.listOfLabels = listOfLabels;
    }

    public Verse(Parcel in) {
        this.book_number = in.readInt();
        this.chapter_number = in.readInt();
        this.verse = in.readInt();
        this.text = in.readString();
        this.is_fav = in.readInt();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        //dest.writeInt(this.id);
        dest.writeInt(this.book_number);
        dest.writeInt(this.chapter_number);
        dest.writeInt(this.verse);
        dest.writeString(this.text);
        dest.writeInt(this.is_fav);
    }

    public ArrayList<Label> getListOfLabels(){ return this.listOfLabels; }

    public int getBookNumber() {
        return this.book_number;
    }
    public int getChapterNumber() {
        return this.chapter_number;
    }
    public int getVerse() {
        return this.verse;
    }

    public Spanned getTextSpanned(){
        return this.textSpanned;
    }

    public SpannableString getTextSpannableString() {return this.ssTextVerse;}

//    public void setTextSpanned(String textWithHTML){
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            this.textSpanned = Html.fromHtml(textWithHTML, Html.FROM_HTML_MODE_COMPACT);
//        } else {
//            this.textSpanned = Html.fromHtml(textWithHTML);
//        }
//    }

//    public String getText() {
//        return this.text;
//    }

    public String getTextTitle() {
        return this.textTitle;
    }

    public void setTextTitle(String textTitle){
        this.textTitle = textTitle;
    }
//
//    public SpannableString getEscrituraSpanneada(Context ctx, boolean underline, boolean higtlight) {
//        SpannableString sb = getEscrituraSpanneada();
//        if (underline) {
//            sb.setSpan(new UnderlineSpan(), String.valueOf(getVerse()).length() + 2, sb.length(), 33);
//        }
//        if (higtlight) {
//            int color = 0;
//            TypedValue typedValue = new TypedValue();
//            if (ctx.getTheme().resolveAttribute(R.attr.color_favorito, typedValue, true)) {
//                color = typedValue.data;
//            }
//            sb.setSpan(new BackgroundColorSpan(color), 0, sb.length(), 33);
//        }
//        return sb;
//    }
//
//    public SpannableString getEscrituraSpanneada() {
//        SpannableString sb = new SpannableString(getVerse() + ". " + getText());
//        sb.setSpan(new StyleSpan(FAVORITO), 0, String.valueOf(getVerse()).length() + FAVORITO, 33);
//        return sb;
//    }

    public void setIsFav(int is_fav) {
        this.is_fav = is_fav;
    }

    public boolean is_fav() {
        return this.is_fav == FAVORITO;
    }
}
