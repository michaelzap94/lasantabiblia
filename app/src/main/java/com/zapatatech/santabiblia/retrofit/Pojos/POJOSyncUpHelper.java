package com.zapatatech.santabiblia.retrofit.Pojos;

import java.util.List;

public class POJOSyncUpHelper {
    private List<POJOLabel> labels;
    private List<POJOVersesMarked> verses_marked;
    private List<POJOVersesLearned> verses_learned;
    private List<POJONote> notes;
    private String status;
    private int state;
    private int version;

    public POJOSyncUpHelper(List<POJOLabel> labels, List<POJOVersesMarked> verses_marked, List<POJOVersesLearned> verses_learned, List<POJONote> notes, int state, int version) {
        this.labels = labels;
        this.verses_marked = verses_marked;
        this.verses_learned = verses_learned;
        this.notes = notes;
        this.state = state;
        this.version = version;
    }

    public List<POJOLabel> getLabels() {
        return labels;
    }

    public List<POJOVersesMarked> getVerses_marked() {
        return verses_marked;
    }

    public List<POJOVersesLearned> getVerses_learned() {
        return verses_learned;
    }

    public List<POJONote> getNotes() {
        return notes;
    }

    public String getStatus() {
        return status;
    }

    public int getState() {
        return state;
    }

    public int getVersion() {
        return version;
    }

}
