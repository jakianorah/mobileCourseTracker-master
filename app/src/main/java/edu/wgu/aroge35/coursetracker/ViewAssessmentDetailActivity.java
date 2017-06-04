package edu.wgu.aroge35.coursetracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import edu.wgu.aroge35.coursetracker.data.DataSource;
import edu.wgu.aroge35.coursetracker.model.Assessment;

/**
 * Created by alissa on 11/29/15.
 */
public class ViewAssessmentDetailActivity extends AppCompatActivity {
    private DataSource db = new DataSource(this);
    private long assessmentId;
    private Assessment assessment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_assessment_detail);

        Bundle bundle = getIntent().getExtras();
        assessmentId = bundle.getLong("assessmentId");

    }

    @Override
    protected void onResume() {
        super.onResume();

        db.open();
        assessment = db.getAssessment(assessmentId);
        db.close();

        TextView textView = (TextView) findViewById(R.id.tvViewAssessmentName);
        textView.setText(assessment.getName());

        textView = (TextView) findViewById(R.id.tvViewAssessmentType);
        textView.setText(assessment.getType().toString());

        SimpleDateFormat sdf = new SimpleDateFormat("LLLL dd, yyyy", Locale.getDefault());

        Date date = assessment.getDueDate();
        String displayText = sdf.format(date);

        textView = (TextView) findViewById(R.id.tvViewAssessmentDueDate);
        textView.setText(displayText);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.llNotesIcon);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ViewNoteListActivity.class);
                intent.putExtra("assessmentId", assessmentId);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_assessment_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_assessment_edit:
                Intent intent = new Intent(this, AddAssessmentActivity.class);
                intent.putExtra("action", "edit");
                intent.putExtra("assessmentId", assessment.getId());
                intent.putExtra("courseId", assessment.getCourseID());
                startActivity(intent);
                return true;
            case R.id.menu_assessment_delete:
                deleteAssessment(assessmentId);
                finish();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAssessment(long id) {
        db.open();
        DataSource.Result result = db.removeAssessment(this, id);
        db.close();

        switch (result) {
            case RESULT_OK:
                Toast.makeText(getBaseContext(), "Assessment has been deleted", Toast.LENGTH_SHORT).show();
                finish();
                break;
            default:
                Toast.makeText(getBaseContext(), "An error occurred while deleting assessment", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
