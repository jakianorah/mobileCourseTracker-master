package edu.wgu.aroge35.coursetracker.data;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import edu.wgu.aroge35.coursetracker.model.Assessment;
import edu.wgu.aroge35.coursetracker.model.Course;
import edu.wgu.aroge35.coursetracker.model.Note;

public class NotesDataSource {

    private String prefKey = "notes";
    private SharedPreferences notePrefs;

    public NotesDataSource(Context context, Course course) {
        notePrefs = context.getSharedPreferences(prefKey + "course" + course.getId(), Context.MODE_PRIVATE);
    }

    public NotesDataSource(Context context, Assessment assessment) {
        notePrefs = context.getSharedPreferences(prefKey + "assessment" + assessment.getId(), Context.MODE_PRIVATE);
    }

    public List<Note> findAll() {
        Map<String, ?> notesMap = notePrefs.getAll();

        SortedSet<String> keys = new TreeSet<>(notesMap.keySet());

        List<Note> noteList = new ArrayList<>();
        for (String key : keys) {
            Note note = new Note();
            note.setKey(key);
            note.setText((String) notesMap.get(key));
            noteList.add(note);
        }

        return noteList;

    }

    public boolean update(Note note) {
        SharedPreferences.Editor editor = notePrefs.edit();
        editor.putString(note.getKey(), note.getText());
        editor.apply();
        return true;
    }

    public boolean remove(Note note) {
        if (notePrefs.contains(note.getKey())) {
            SharedPreferences.Editor editor = notePrefs.edit();
            editor.remove(note.getKey());
            editor.apply();
        }

        return true;
    }
}
