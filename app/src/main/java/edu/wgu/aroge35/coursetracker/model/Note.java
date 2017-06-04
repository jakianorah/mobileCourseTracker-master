package edu.wgu.aroge35.coursetracker.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Note {
    private String key;
    private String text;

    public static Note getNew() {

        Locale locale = new Locale("en_US");
        Locale.setDefault(locale);

        String pattern = "yyyyMMddHHmmssZ";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.getDefault());
        String key = formatter.format(new Date());

        Note note = new Note();
        note.setKey(key);
        note.setText("");
        return note;

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return this.getText();
    }

}
