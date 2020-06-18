package com.zapatatech.santabiblia.retrofit.Pojos;

public class POJOVersesLearned {
    private int _id;
    private int label_id;
    private String UUID;
    private int learned;
    private int priority;
    private int state;

    public POJOVersesLearned(int _id, int label_id, String UUID, int learned, int priority, int state) {
        this._id = _id;
        this.label_id = label_id;
        this.UUID = UUID;
        this.learned = learned;
        this.priority = priority;
        this.state = state;
    }

    public int get_id() {
        return _id;
    }

    public int getLabel_id() {
        return label_id;
    }

    public String getUUID() {
        return UUID;
    }

    public int getLearned() {
        return learned;
    }

    public int getPriority() {
        return priority;
    }

    public int getState() {
        return state;
    }
}
