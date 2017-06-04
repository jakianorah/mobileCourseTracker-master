package edu.wgu.aroge35.coursetracker;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;

import edu.wgu.aroge35.coursetracker.model.Note;

public class AddNoteActivity extends AppCompatActivity {

    private Note note;
    private String emailSubject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        Intent intent = this.getIntent();
        note = new Note();
        note.setKey(intent.getStringExtra("key"));
        note.setText(intent.getStringExtra("text"));
        emailSubject = intent.getStringExtra("email_subject");
        String model = intent.getStringExtra("model");

        EditText et = (EditText) findViewById(R.id.noteText);
        et.setText(note.getText());
        et.setSelection(note.getText().length());

        if (model.equals("assessment")) {
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.llButtons);
            Button viewImageButton = new Button(this);
            viewImageButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            viewImageButton.setText(R.string.view_photos);
            viewImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AddNoteActivity.this, ViewPhotoGridActivity.class);
                    intent.putExtra("key", note.getKey());
                    startActivity(intent);
                }
            });
            linearLayout.addView(viewImageButton);
        }

    }

    private void saveAndFinish() {
        EditText et = (EditText) findViewById(R.id.noteText);
        String noteText = et.getText().toString();

        if (!noteText.equals("")) {
            Intent intent = new Intent();
            intent.putExtra("key", note.getKey());
            intent.putExtra("text", noteText);
            setResult(RESULT_OK, intent);
        }

        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_notes_share:
                String email = "";
                Uri uri = Uri.parse("mailto:" + email)
                        .buildUpon()
                        .appendQueryParameter("subject", emailSubject)
                        .appendQueryParameter("body", note.getText())
                        .build();
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, uri);

                ComponentName emailApp = emailIntent.resolveActivity(getPackageManager());
                ComponentName unsupportedAction = ComponentName.unflattenFromString("com.android.fallback/.Fallback");
                if (emailApp != null && !emailApp.equals(unsupportedAction))
                    try {
                        // Needed to customise the chooser dialog title since it might default to "Share with"
                        // Note that the chooser will still be skipped if only one app is matched
                        startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                        return true;
                    }
                    catch (ActivityNotFoundException ignored) {
                    }

                Toast.makeText(this, "Couldn't find an email app and account", Toast.LENGTH_LONG).show();


                return true;
            case android.R.id.home:
                if (note.getText().equals("")) {
                    removeImages();
                }
                finish();
                return true;
            default:
                return false;
        }
    }

    private void removeImages() {
        File dir = new File(Environment.getExternalStorageDirectory() + "/" + getPackageName() + "/" + note.getKey());
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f :
                    files) {
                f.delete();
            }
        }

        dir.delete();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void onClickSaveNote(View view) {
        saveAndFinish();
    }

}
