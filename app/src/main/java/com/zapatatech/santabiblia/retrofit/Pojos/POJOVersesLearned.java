package com.zapatatech.santabiblia.retrofit.Pojos;

public class POJOVersesLearned {
    private int user;
    private String _id;
    private String label_id;
    private String UUID;
    private int learned;
    private int priority;
    private int state;

    public POJOVersesLearned(int user, String _id, String label_id, String UUID, int learned, int priority, int state) {
        this.user = user;
        this._id = _id;
        this.label_id = label_id;
        this.UUID = UUID;
        this.learned = learned;
        this.priority = priority;
        this.state = state;
    }

    public int getUserId() {
        return user;
    }

    public String get_id() {
        return _id;
    }

    public String getLabel_id() {
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
