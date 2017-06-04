package edu.wgu.aroge35.coursetracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import edu.wgu.aroge35.coursetracker.data.DataSource;
import edu.wgu.aroge35.coursetracker.model.CourseMentor;

/**
 * Created by alissa on 11/28/15.
 */
public class AddCourseMentorActivity extends AppCompatActivity {
    private String action;
    private long cmId;
    private long courseId;
    private int position;
    private CourseMentor courseMentor = new CourseMentor();
    private DataSource db = new DataSource(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_coursementor);

        Bundle bundle = getIntent().getExtras();

        action = bundle.getString("action", "add");
        cmId = bundle.getLong("cmId", 0);
        courseId = bundle.getLong("courseId");
        position = bundle.getInt("position", -1);

        if (action.equals("edit")) {
            // we came in from the course creation screen
            courseMentor.setCourseId(bundle.getLong("courseId"));
            courseMentor.setName(bundle.getString("name"));
            courseMentor.setWorkPhone(bundle.getString("workPhone"));
            courseMentor.setHomePhone(bundle.getString("homePhone"));
            courseMentor.setCellPhone(bundle.getString("cellPhone"));
            courseMentor.setWorkEmail(bundle.getString("workEmail"));
            courseMentor.setHomeEmail(bundle.getString("homeEmail"));
            courseMentor.setOtherEmail(bundle.getString("otherEmail"));
        } else if (action.equals("edit_direct")) {
            // we came in from the detail page or the coursementors list page
            db.open();
            courseMentor = db.getCourseMentor(cmId);
            courseId = courseMentor.getCourseId();
            db.close();

        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (action.equals("edit") || action.equals("edit_direct")) {
            setTitle("Edit Course Mentor");

            EditText editText = (EditText) findViewById(R.id.etCMName);
            editText.setText(courseMentor.getName());

            editText = (EditText) findViewById(R.id.etWorkPhone);
            editText.setText(courseMentor.getWorkPhone());

            editText = (EditText) findViewById(R.id.etHomePhone);
            editText.setText(courseMentor.getHomePhone());

            editText = (EditText) findViewById(R.id.etCellPhone);
            editText.setText(courseMentor.getCellPhone());

            editText = (EditText) findViewById(R.id.etEmailWork);
            editText.setText(courseMentor.getWorkEmail());

            editText = (EditText) findViewById(R.id.etHomeEmail);
            editText.setText(courseMentor.getHomeEmail());

            editText = (EditText) findViewById(R.id.etOtherEmail);
            editText.setText(courseMentor.getOtherEmail());

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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

    public void onClickSaveMentor(View view) {
        if (action.equals("add") || action.equals("edit")) {
            Intent intent = new Intent();
            intent.putExtra("courseId", courseMentor.getCourseId());
            intent.putExtra("cmId", cmId);
            intent.putExtra("position", position);

            EditText editText = (EditText) findViewById(R.id.etCMName);
            intent.putExtra("etCMName", editText.getText().toString());

            editText = (EditText) findViewById(R.id.etWorkPhone);
            intent.putExtra("etWorkPhone", editText.getText().toString());

            editText = (EditText) findViewById(R.id.etHomePhone);
            intent.putExtra("etHomePhone", editText.getText().toString());

            editText = (EditText) findViewById(R.id.etCellPhone);
            intent.putExtra("etCellPhone", editText.getText().toString());

            editText = (EditText) findViewById(R.id.etEmailWork);
            intent.putExtra("etEmailWork", editText.getText().toString());

            editText = (EditText) findViewById(R.id.etHomeEmail);
            intent.putExtra("etHomeEmail", editText.getText().toString());

            editText = (EditText) findViewById(R.id.etOtherEmail);
            intent.putExtra("etOtherEmail", editText.getText().toString());

            setResult(RESULT_OK, intent);
            finish();
        } else if (action.equals("edit_direct") || action.equals("add_direct")) {
            courseMentor.setId(cmId);
            courseMentor.setCourseId(courseId);

            EditText editText = (EditText) findViewById(R.id.etCMName);
            courseMentor.setName(editText.getText().toString());

            editText = (EditText) findViewById(R.id.etWorkPhone);
            courseMentor.setWorkPhone(editText.getText().toString());

            editText = (EditText) findViewById(R.id.etHomePhone);
            courseMentor.setHomePhone(editText.getText().toString());

            editText = (EditText) findViewById(R.id.etCellPhone);
            courseMentor.setCellPhone(editText.getText().toString());

            editText = (EditText) findViewById(R.id.etEmailWork);
            courseMentor.setWorkEmail(editText.getText().toString());

            editText = (EditText) findViewById(R.id.etHomeEmail);
            courseMentor.setHomeEmail(editText.getText().toString());

            editText = (EditText) findViewById(R.id.etOtherEmail);
            courseMentor.setOtherEmail(editText.getText().toString());

            if (courseMentor.getName().equals("")) {
                Toast.makeText(getBaseContext(), "Mentor name can not be empty", Toast.LENGTH_LONG).show();
            } else {
                db.open();
                if (action.equals("edit_direct")) {
                    if (db.updateCourseMentor(courseMentor)) {
                        Toast.makeText(getBaseContext(), "Mentor has been updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getBaseContext(), "An error occurred while updating", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    db.addCourseMentor(courseMentor);

                }
                db.close();
                finish();
            }
        }
    }
}
