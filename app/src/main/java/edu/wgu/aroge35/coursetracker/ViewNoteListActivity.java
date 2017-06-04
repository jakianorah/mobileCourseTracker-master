package edu.wgu.aroge35.coursetracker;

/**
 * Created by alissa on 11/26/15.
 */

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import edu.wgu.aroge35.coursetracker.data.DataSource;
import edu.wgu.aroge35.coursetracker.data.NotesDataSource;
import edu.wgu.aroge35.coursetracker.model.Assessment;
import edu.wgu.aroge35.coursetracker.model.Course;
import edu.wgu.aroge35.coursetracker.model.Note;

public class ViewNoteListActivity extends AppCompatActivity {

    private static final int EDITOR_ACTIVITY_REQUEST = 1001;
    private NotesDataSource datasource;
    private List<Note> notesList;
    private long courseId;
    private long assessmentId;
    private Course course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notes);

        Bundle bundle = getIntent().getExtras();
        courseId = bundle.getLong("courseId", -1);
        assessmentId = bundle.getLong("assessmentId", -1);

        DataSource db = new DataSource(this);
        db.open();
        if (courseId > -1) {
            // get a course object
            course = db.getCourse(courseId);
            // open the datasource with it
            datasource = new NotesDataSource(this, course);
        } else if (assessmentId > -1) {
            // get an assessment object
            Assessment assessment = db.getAssessment(assessmentId);
            // open the datasource with it
            datasource = new NotesDataSource(this, assessment);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        notesList = datasource.findAll();
        ArrayAdapter<Note> adapter =
                new ArrayAdapter<>(getBaseContext(), R.layout.list_item_note, notesList);
        ListView listView = (ListView) findViewById(R.id.lvViewNotes);
        registerForContextMenu(listView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Note note = notesList.get(position);
                Intent intent = new Intent(ViewNoteListActivity.this, AddNoteActivity.class);
                intent.putExtra("key", note.getKey());
                intent.putExtra("text", note.getText());
                String emailSubject;
                String parentModel;
                if (courseId > -1) {
                    parentModel = "course";
                    emailSubject = "Notes from " + course.getTitle();
                } else if (assessmentId > -1) {
                    parentModel = "assessment";
                    emailSubject = "Assessment Notes";
                } else {
                    parentModel = "";
                    emailSubject = "";
                }
                intent.putExtra("model", parentModel);
                intent.putExtra("email_subject", emailSubject);
                startActivityForResult(intent, EDITOR_ACTIVITY_REQUEST);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_create:
                createNote();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.menu_note_list_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Note note = notesList.get(info.position);

        switch (item.getItemId()) {
            case R.id.menu_note_delete:
                if (datasource.remove(note)) {
                    removeImages(note.getKey());
                    Toast.makeText(this, "Note has been deleted", Toast.LENGTH_LONG).show();
                    onResume();
                } else {
                    Toast.makeText(this, "Error deleting note", Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return false;
        }
    }

    private void createNote() {
        Note note = Note.getNew();
        Intent intent = new Intent(ViewNoteListActivity.this, AddNoteActivity.class);
        intent.putExtra("key", note.getKey());
        intent.putExtra("text", note.getText());

        String emailSubject;
        String parentModel;
        if (courseId > -1) {
            parentModel = "course";
            emailSubject = "Sharing Notes from " + course.getTitle();
        } else if (assessmentId > -1) {
            parentModel = "assessment";
            emailSubject = "Sharing Notes";
        } else {
            parentModel = "";
            emailSubject = "";
        }
        intent.putExtra("model", parentModel);
        intent.putExtra("email_subject", emailSubject);
        startActivityForResult(intent, EDITOR_ACTIVITY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDITOR_ACTIVITY_REQUEST && resultCode == RESULT_OK) {

            Note note = new Note();
            note.setKey(data.getStringExtra("key"));
            note.setText(data.getStringExtra("text"));
            datasource.update(note);
        }
    }

    private void removeImages(String key) {
        File dir = new File(Environment.getExternalStorageDirectory() + "/" + getPackageName() + "/" + key);
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f :
                    files) {
                f.delete();
            }
        }

        dir.delete();
    }

}
