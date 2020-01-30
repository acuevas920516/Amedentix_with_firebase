package com.example.amedentix_with_firebase.activities;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class HistoryId {
    @Exclude
    public String HistoryId;

    public <T extends HistoryId> T withId(@NonNull final String id) {
        this.HistoryId = id;
        return (T) this;
    }
}
