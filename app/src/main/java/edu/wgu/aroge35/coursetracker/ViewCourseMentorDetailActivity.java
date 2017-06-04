package edu.wgu.aroge35.coursetracker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import edu.wgu.aroge35.coursetracker.data.DataSource;
import edu.wgu.aroge35.coursetracker.model.CourseMentor;

/**
 * Created by alissa on 11/30/15.
 */
public class ViewCourseMentorDetailActivity extends AppCompatActivity {
    private long cmId;
    private String action;
    private CourseMentor courseMentor;
    private DataSource db = new DataSource(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_coursementor_detail);

        Bundle bundle = getIntent().getExtras();
        action = bundle.getString("action");
        cmId = bundle.getLong("cmId");

    }

    @Override
    protected void onResume() {
        super.onResume();

        db.open();
        courseMentor = db.getCourseMentor(cmId);
        db.close();

        TextView textView = (TextView) findViewById(R.id.tvViewMentorName);
        textView.setText(courseMentor.getName());

        textView = (TextView) findViewById(R.id.tvViewMentorWorkPhone);
        if (courseMentor.getWorkPhone().equals("")) {
            textView.setText("-");
        } else {
            textView.setText(courseMentor.getWorkPhone());
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                    phoneIntent.setData(Uri.parse("tel:" + courseMentor.getWorkPhone()));
                    startActivity(phoneIntent);
                }
            });
        }

        textView = (TextView) findViewById(R.id.tvViewMentorHomePhone);
        if (courseMentor.getHomePhone().equals("")) {
            textView.setText("-");
        } else {
            textView.setText(courseMentor.getHomePhone());
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                    phoneIntent.setData(Uri.parse("tel:" + courseMentor.getHomePhone()));
                    startActivity(phoneIntent);
                }
            });
        }

        textView = (TextView) findViewById(R.id.tvViewMentorCellPhone);
        if (courseMentor.getCellPhone().equals("")) {
            textView.setText("-");
        } else {
            textView.setText(courseMentor.getCellPhone());
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                    phoneIntent.setData(Uri.parse("tel:" + courseMentor.getCellPhone()));
                    startActivity(phoneIntent);
                }
            });
        }

        textView = (TextView) findViewById(R.id.tvViewMentorWorkEmail);
        if (courseMentor.getWorkEmail().equals("")) {
            textView.setText("-");
        } else {
            textView.setText(courseMentor.getWorkEmail());
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                    String[] recipients = new String[]{courseMentor.getWorkEmail()};
                    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, recipients);
                    emailIntent.setType("message/rfc822");
                    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                }
            });
        }

        textView = (TextView) findViewById(R.id.tvViewMentorHomeEmail);
        if (courseMentor.getHomeEmail().equals("")) {
            textView.setText("-");
        } else {
            textView.setText(courseMentor.getHomeEmail());
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                    String[] recipients = new String[]{courseMentor.getHomeEmail()};
                    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, recipients);
                    emailIntent.setType("message/rfc822");
                    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                }
            });
        }

        textView = (TextView) findViewById(R.id.tvViewMentorOtherEmail);
        if (courseMentor.getOtherEmail().equals("")) {
            textView.setText("-");
        } else {
            textView.setText(courseMentor.getOtherEmail());
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                    String[] recipients = new String[]{courseMentor.getOtherEmail()};
                    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, recipients);
                    emailIntent.setType("message/rfc822");
                    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                }
            });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_coursementor_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_coursementor_edit:
                Intent intent = new Intent(this, AddCourseMentorActivity.class);
                intent.putExtra("action", "edit_direct");
                intent.putExtra("cmId", courseMentor.getId());
                startActivity(intent);
                return true;
            case R.id.menu_coursementor_delete:
                deleteCM(courseMentor);
                finish();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteCM(CourseMentor courseMentor) {
        db.open();
        DataSource.Result result = db.removeCourseMentor(courseMentor.getId());
        db.close();

        switch (result) {
            case RESULT_OK:
                Toast.makeText(getBaseContext(), "Mentor has been deleted", Toast.LENGTH_SHORT).show();
                finish();
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
