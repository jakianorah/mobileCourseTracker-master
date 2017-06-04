package edu.wgu.aroge35.coursetracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.wgu.aroge35.coursetracker.data.DataSource;
import edu.wgu.aroge35.coursetracker.model.Assessment;
import edu.wgu.aroge35.coursetracker.model.Course;
import edu.wgu.aroge35.coursetracker.model.CourseMentor;

/**
 * Created by alissa on 11/27/15.
 */
public class ViewCourseDetailActivity extends AppCompatActivity {

    private DataSource db = new DataSource(this);
    private Course course;
    private long courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_course_detail);

        Bundle bundle = getIntent().getExtras();
        courseId = bundle.getLong("courseId");
    }

    @Override
    protected void onResume() {
        super.onResume();

        db.open();
        course = db.getCourse(courseId);
        List<Assessment> assessments = db.getAllAssessmentsForCourse(courseId);
        List<CourseMentor> courseMentors = db.getAllCourseMentorsForCourse(courseId);
        db.close();

        String courseName = course.getTitle();
        TextView textView = (TextView) findViewById(R.id.tvCourseDetailName);
        textView.setText(courseName);
        setTitle(courseName);

        SimpleDateFormat sdf = new SimpleDateFormat("LLLL dd, yyyy", Locale.getDefault());

        Date date = course.getStartDate();
        String displayText = sdf.format(date);

        textView = (TextView) findViewById(R.id.tvCourseDetailStartDate);
        textView.setText(displayText);

        date = course.getEndDate();
        displayText = sdf.format(date);

        textView = (TextView) findViewById(R.id.tvCourseDetailEndDate);
        textView.setText(displayText);

        String[] statusImages = new String[]{
                "ic_status_in_progress",
                "ic_status_complete",
                "ic_status_dropped",
                "ic_status_to_take"
        };



        textView = (TextView) findViewById(R.id.tvCourseDetailStatusLabel);
        textView.setText(course.getStatus().toString());

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.llAssessmentIcon);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickViewAssessments(v);
            }
        });

        textView = (TextView) findViewById(R.id.tvCourseDetailAssessmentLabel);
        String text;
        if (assessments.size() == 0) {
            text = "Add Assessments";
        } else if (assessments.size() == 1) {
            text = "View " + assessments.size() + " Assessment";
        } else {
            text = "View " + assessments.size() + " Assessments";
        }
        textView.setText(text);

        linearLayout = (LinearLayout) findViewById(R.id.llMentorIcon);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickViewCM(v);
            }
        });

        textView = (TextView) findViewById(R.id.tvCourseDetailMentorLabel);
        if (courseMentors.size() == 0) {
            text = "Add Mentor";
        } else if (courseMentors.size() == 1) {
            text = "View " + courseMentors.size() + " Mentor";
        } else {
            text = "View " + courseMentors.size() + " Mentors";
        }
        textView.setText(text);

        linearLayout = (LinearLayout) findViewById(R.id.llNotesIcon);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ViewNoteListActivity.class);
                intent.putExtra("courseId", courseId);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_course_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_course_edit:
                Intent intent = new Intent(this, AddCourseActivity.class);
                intent.putExtra("action", "edit");
                intent.putExtra("id", courseId);
                intent.putExtra("termId", course.getTermId());
                startActivity(intent);
                return true;
            case R.id.menu_course_delete:
                deleteCourse(courseId);
                finish();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteCourse(long id) {
        db.open();
        DataSource.Result result = db.removeCourse(this, id);
        db.close();

        switch (result) {
            case RESULT_OK:
                Toast.makeText(getBaseContext(), "Course has been deleted", Toast.LENGTH_SHORT).show();
                finish();
                break;
            default:
                Toast.makeText(getBaseContext(), "An error occurred while deleting course", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void onClickViewAssessments(View view) {
        Intent intent = new Intent(ViewCourseDetailActivity.this, ViewAssessmentListActivity.class);
        intent.putExtra("courseId", courseId);
        startActivity(intent);
    }

    public void onClickViewCM(View view) {
        Intent intent = new Intent(this, ViewCourseMentorListActivity.class);
        intent.putExtra("courseId", courseId);
        startActivity(intent);
    }
}
