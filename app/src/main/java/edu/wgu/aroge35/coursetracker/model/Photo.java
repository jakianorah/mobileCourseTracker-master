package edu.wgu.aroge35.coursetracker.model;

import android.graphics.Bitmap;

/**
 * Created by alissa on 12/4/15.
 */
public class Photo {
    private Bitmap image;
    private String title;
    private String filename = "";

    public Photo(Bitmap image, String title) {
        this.image = image;
        this.title = title;
    }

    public Photo(Bitmap image, String title, String filename) {
        this.image = image;
        this.title = title;
        this.filename = filename;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}