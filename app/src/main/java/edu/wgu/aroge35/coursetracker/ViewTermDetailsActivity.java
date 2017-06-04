package edu.wgu.aroge35.coursetracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import edu.wgu.aroge35.coursetracker.data.DataSource;
import edu.wgu.aroge35.coursetracker.model.Term;

public class ViewTermDetailsActivity extends AppCompatActivity {
    private DataSource db = new DataSource(this);
    private long termId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_term_details);

        Bundle bundle = getIntent().getExtras();
        termId = bundle.getLong("termId");
    }


    @Override
    protected void onResume() {
        super.onResume();

        db.open();
        Term term = db.getTerm(termId);
        db.close();

        String termName = term.getName();
        setTitle(termName);
        TextView textView = (TextView) findViewById(R.id.tvViewMentorName);
        textView.setText(termName);

        SimpleDateFormat sdf = new SimpleDateFormat("LLLL dd, yyyy", Locale.getDefault());

        Date date = term.getStartDate();
        String displayText = sdf.format(date);

        textView = (TextView) findViewById(R.id.tvViewCourseStartDate);
        textView.setText(displayText);

        date = term.getEndDate();
        displayText = sdf.format(date);

        textView = (TextView) findViewById(R.id.tvViewCourseEndDate);
        textView.setText(displayText);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_term_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_term_edit:
                Intent intent = new Intent(this, AddTermActivity.class);
                intent.putExtra("action", "edit");
                intent.putExtra("id", termId);
                startActivity(intent);
                return true;
            case R.id.menu_term_delete:
                deleteTerm(termId);
                finish();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteTerm(long id) {
        db.open();
        DataSource.Result result = db.removeTerm(this, id);
        db.close();

        switch (result) {
            case RESULT_OK:
                Toast.makeText(getBaseContext(), "Term has been deleted", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case ERROR_TERM_NOT_EMPTY:
                Toast.makeText(getBaseContext(), "Term not empty\nDelete all courses and try again.", Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(getBaseContext(), "An error occurred while deleting term", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void onClickShowCoursesforTerm(View view) {
        Intent intent = new Intent(ViewTermDetailsActivity.this, ViewCourseListActivity.class);
        intent.putExtra("termId", termId);
        startActivity(intent);
    }
}
