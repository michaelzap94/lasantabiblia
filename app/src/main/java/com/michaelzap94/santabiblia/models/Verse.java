package com.michaelzap94.santabiblia.models;


import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;

import com.michaelzap94.santabiblia.R;

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
    protected int book_id;
    protected int chapter_number;
    protected int verse;
    protected String text;


    public Verse(int book_id, int chapter_number, int verse, String text, int is_fav) {
        //this.id = id;
        this.book_id = book_id;
        this.chapter_number = chapter_number;
        this.verse = verse;
        this.text = text;
        this.is_fav = is_fav;
    }

    public Verse(Parcel in) {
        //this.id = in.readInt();
        this.book_id = in.readInt();
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
        dest.writeInt(this.book_id);
        dest.writeInt(this.chapter_number);
        dest.writeInt(this.verse);
        dest.writeString(this.text);
        dest.writeInt(this.is_fav);
    }

    //public int getId() {
//        return this.id;
//    }

    public int getVerse() {
        return this.verse;
    }

    public String getText() {
        return this.text;
    }

    public SpannableString getEscrituraSpanneada(Context ctx, boolean underline, boolean higtlight) {
        SpannableString sb = getEscrituraSpanneada();
        if (underline) {
            sb.setSpan(new UnderlineSpan(), String.valueOf(getVerse()).length() + 2, sb.length(), 33);
        }
        if (higtlight) {
            int color = 0;
            TypedValue typedValue = new TypedValue();
            if (ctx.getTheme().resolveAttribute(R.attr.color_favorito, typedValue, true)) {
                color = typedValue.data;
            }
            sb.setSpan(new BackgroundColorSpan(color), 0, sb.length(), 33);
        }
        return sb;
    }

    public SpannableString getEscrituraSpanneada() {
        SpannableString sb = new SpannableString(getVerse() + ". " + getText());
        sb.setSpan(new StyleSpan(FAVORITO), 0, String.valueOf(getVerse()).length() + FAVORITO, 33);
        return sb;
    }

    public void setIsFav(int is_fav) {
        this.is_fav = is_fav;
    }

    public boolean is_fav() {
        return this.is_fav == FAVORITO;
    }
}
