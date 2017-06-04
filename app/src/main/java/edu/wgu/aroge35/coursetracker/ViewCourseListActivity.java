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
import edu.wgu.aroge35.coursetracker.model.Course;
import edu.wgu.aroge35.coursetracker.util.CourseArrayAdapter;

/**
 * Created by alissa on 11/29/15.
 */
public class ViewCourseListActivity extends AppCompatActivity {
    private List<Course> courses;
    private DataSource db = new DataSource(this);
    private long termId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_course_list);

        Bundle bundle = getIntent().getExtras();
        termId = bundle.getLong("termId");

    }

    @Override
    protected void onResume() {
        super.onResume();

        db.open();
        courses = db.getAllCoursesForTerm(termId);
        String termName = db.getTerm(termId).getName();
        db.close();

        setTitle("Courses");
        TextView textView = (TextView) findViewById(R.id.tvViewCoursesTermName);
        textView.setText(termName);

        ListView listView;
        if (courses.size() == 0) {
            ArrayList<String> noCourses = new ArrayList<>();
            noCourses.add("No courses here yet. Click to add some.");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, noCourses);
            listView = (ListView) findViewById(R.id.lvViewCourses);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getBaseContext(), AddCourseActivity.class);
                    intent.putExtra("termId", termId);
                    startActivity(intent);
                }
            });
        } else {
            CourseArrayAdapter adapter = new CourseArrayAdapter(getBaseContext(), courses);
            listView = (ListView) findViewById(R.id.lvViewCourses);
            registerForContextMenu(listView);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Course course = courses.get(position);
                    Intent intent = new Intent(getBaseContext(), ViewCourseDetailActivity.class);
                    intent.putExtra("courseId", course.getId());
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_course_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_courses_new) {
            Intent intent = new Intent(ViewCourseListActivity.this, AddCourseActivity.class);
            intent.putExtra("action", "add");
            intent.putExtra("termId", termId);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == android.R.id.home) {
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
        getMenuInflater().inflate(R.menu.menu_course_list_context, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Course course = courses.get(info.position);

        switch (item.getItemId()) {
            case R.id.menu_courses_edit:
                Intent intent = new Intent(this, AddCourseActivity.class);
                intent.putExtra("action", "edit");
                intent.putExtra("id", course.getId());
                intent.putExtra("termId", termId);
                startActivity(intent);
                return true;
            case R.id.menu_courses_delete:
                deleteCourse(course);
                return true;
            default:
                return false;
        }
    }

    private void deleteCourse(Course course) {
        db.open();
        DataSource.Result result = db.removeCourse(this, course.getId());
        db.close();

        switch (result) {
            case RESULT_OK:
                Toast.makeText(getBaseContext(), "Course has been deleted", Toast.LENGTH_SHORT).show();
                onResume();
                break;
            default:
                Toast.makeText(getBaseContext(), "An error occurred while deleting course", Toast.LENGTH_SHORT).show();
        }
    }
}
