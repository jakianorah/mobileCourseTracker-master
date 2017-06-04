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
import edu.wgu.aroge35.coursetracker.model.CourseMentor;
import edu.wgu.aroge35.coursetracker.util.CourseMentorArrayAdapter;

/**
 * Created by alissa on 11/30/15.
 */
public class ViewCourseMentorListActivity extends AppCompatActivity {
    private DataSource db = new DataSource(this);
    private long courseId;
    private List<CourseMentor> courseMentors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        courseId = bundle.getLong("courseId");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_coursementor_list);

    }

    @Override
    protected void onResume() {
        super.onResume();

        db.open();
        courseMentors = db.getAllCourseMentorsForCourse(courseId);
        String courseName = db.getCourse(courseId).getTitle();
        db.close();

        setTitle("Mentors");
        TextView textView = (TextView) findViewById(R.id.tvViewCourseMentorsTermName);
        textView.setText(courseName);

        if (courseMentors.size() == 0) {
            ArrayList<String> noCourseMentors = new ArrayList<>();
            noCourseMentors.add("No course mentors here yet. Click to add some.");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, noCourseMentors);
            ListView listView = (ListView) findViewById(R.id.lvViewCourseMentors);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getBaseContext(), AddCourseMentorActivity.class);
                    intent.putExtra("action", "add_direct");
                    intent.putExtra("courseId", courseId);
                    startActivity(intent);
                }
            });
        } else {
            CourseMentorArrayAdapter adapter = new CourseMentorArrayAdapter(getBaseContext(), courseMentors);
            ListView listView = (ListView) findViewById(R.id.lvViewCourseMentors);
            listView.setAdapter(adapter);
            registerForContextMenu(listView);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    CourseMentor courseMentor = courseMentors.get(position);
                    Intent intent = new Intent(getBaseContext(), ViewCourseMentorDetailActivity.class);
                    intent.putExtra("cmId", courseMentor.getId());
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_coursementor_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_coursementors_new:
                Intent intent = new Intent(this, AddCourseMentorActivity.class);
                intent.putExtra("action", "add_direct");
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
        getMenuInflater().inflate(R.menu.menu_coursementor_list_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        CourseMentor courseMentor = courseMentors.get(info.position);

        switch (item.getItemId()) {
            case R.id.menu_cm_edit:
                Intent intent = new Intent(this, AddCourseMentorActivity.class);
                intent.putExtra("action", "edit_direct");
                intent.putExtra("cmId", courseMentor.getId());
                startActivity(intent);
                return true;
            case R.id.menu_cm_delete:
                deleteCM(courseMentor);
            default:
                return false;
        }
    }

    private void deleteCM(CourseMentor courseMentor) {
        db.open();
        DataSource.Result result = db.removeCourseMentor(courseMentor.getId());
        db.close();

        switch (result) {
            case RESULT_OK:
                Toast.makeText(getBaseContext(), "Mentor has been deleted", Toast.LENGTH_SHORT).show();
                onResume();
                break;
            default:
                Toast.makeText(getBaseContext(), "An error occurred while deleting mentor", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
