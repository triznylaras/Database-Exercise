package com.example.triznylarasati.firebaserealtimedatabase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Author {

    private String authorId;
    private String authorName;
    private String authorGenre;

    public Author() {
        //this constructor is required
    }

    public Author (String authorId, String authorName, String authorGenre) {
        this.authorId = authorId;
        this.authorName = authorName;
        this.authorGenre = authorGenre;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getAuthorGenre() {
        return authorGenre;
    }
}
