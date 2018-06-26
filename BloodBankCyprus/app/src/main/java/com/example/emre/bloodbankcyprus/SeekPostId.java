package com.example.emre.bloodbankcyprus;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class SeekPostId {
        @Exclude
    public String SeekPostId;
        public <T extends SeekPostId> T withId(@NonNull final String id) {

            this.SeekPostId = id;
            return (T) this;
        }

}
