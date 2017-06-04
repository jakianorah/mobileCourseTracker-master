package edu.wgu.aroge35.coursetracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.wgu.aroge35.coursetracker.data.DataSource;
import edu.wgu.aroge35.coursetracker.model.Assessment;
import edu.wgu.aroge35.coursetracker.util.AssessmentArrayAdapter;

public class ViewAssessmentListActivity extends AppCompatActivity {
    private static List<Assessment> assessments;
    private DataSource db = new DataSource(this);
    private long courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_assessment_list);

        Bundle bundle = getIntent().getExtras();
        courseId = bundle.getLong("courseId");
    }

    @Override
    protected void onResume() {
        super.onResume();

        db.open();
        assessments = db.getAllAssessmentsForCourse(courseId);
        String courseName = db.getCourse(courseId).getTitle();
        db.close();

        setTitle("Assessments");
        TextView textView = (TextView) findViewById(R.id.tvViewCoursesTermName);
        textView.setText(courseName);

        ListView listView;
        if (assessments.size() == 0) {
            ArrayList<String> noAssessments = new ArrayList<>();
            noAssessments.add("No assessments here yet. Click to add some.");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, noAssessments);
            listView = (ListView) findViewById(R.id.lvViewAssessments);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getBaseContext(), AddAssessmentActivity.class);
                    intent.putExtra("courseId", courseId);
                    startActivity(intent);
                }
            });
        } else {
            AssessmentArrayAdapter adapter = new AssessmentArrayAdapter(getBaseContext(), assessments);
            listView = (ListView) findViewById(R.id.lvViewAssessments);
            listView.setAdapter(adapter);
            registerForContextMenu(listView);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Assessment assessment = assessments.get(position);
                    Intent intent = new Intent(getBaseContext(), ViewAssessmentDetailActivity.class);
                    intent.putExtra("assessmentId", assessment.getId());
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_assessment_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_assessments_new:
                Intent intent = new Intent(ViewAssessmentListActivity.this, AddAssessmentActivity.class);
                intent.putExtra("action", "add");
                intent.putExtra("courseId", courseId);
                startActivity(intent);
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.menu_assessment_list_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Assessment assessment = assessments.get(info.position);

        switch (item.getItemId()) {
            case R.id.menu_assessments_edit:
                Intent intent = new Intent(this, AddAssessmentActivity.class);
                intent.putExtra("action", "edit");
                intent.putExtra("assessmentId", assessment.getId());
                intent.putExtra("courseId", assessment.getCourseID());
                startActivity(intent);
                return true;
            case R.id.menu_assessments_delete:
                deleteAssessment(assessment);
            default:
                return false;
        }
    }

    private void deleteAssessment(Assessment assessment) {
        db.open();
        DataSource.Result result = db.removeAssessment(this, assessment.getId());
        db.close();

        switch (result) {
            case RESULT_OK:
                Toast.makeText(getBaseContext(), "Assessment has been deleted", Toast.LENGTH_SHORT).show();
                onResume();
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
