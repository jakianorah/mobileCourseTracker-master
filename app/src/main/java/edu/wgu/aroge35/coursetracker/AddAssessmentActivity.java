package edu.wgu.aroge35.coursetracker;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import edu.wgu.aroge35.coursetracker.data.DataSource;
import edu.wgu.aroge35.coursetracker.model.Assessment;
import edu.wgu.aroge35.coursetracker.util.DatePickerFragment;
import edu.wgu.aroge35.coursetracker.util.DateUtil;
import edu.wgu.aroge35.coursetracker.util.Reminder;

/**
 * Created by alissa on 11/29/15.
 */
public class AddAssessmentActivity extends AppCompatActivity {
    private DataSource db = new DataSource(this);
    private long courseId;
    private String name;
    private long assessmentId;
    private Assessment assessment;
    private String action;
    private Date oldDate;
    private boolean oldIsChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_assessment);

        Bundle bundle = getIntent().getExtras();
        assessmentId = bundle.getLong("assessmentId", 0);
        courseId = bundle.getLong("courseId");
        action = bundle.getString("action", "add");

        if (action.equals("edit")) {
            setTitle("Edit Assessment");
            db.open();
            assessment = db.getAssessment(assessmentId);
            oldDate = assessment.getDueDate();
            oldIsChecked = assessment.isReminderSet();
            db.close();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (action.equals("edit")) {
            EditText editText = (EditText) findViewById(R.id.etAddAssessmentName);
            editText.setText(assessment.getName());
        }

        // Set up the start date textview
        //Use the current date as the default first day in term
        final Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        String dueDate;
        if (action.equals("edit")) {
            dueDate = sdf.format(oldDate);
        } else {
            dueDate = sdf.format(calendar.getTime());
        }

        TextView textView = (TextView) findViewById(R.id.etAddAssessmentDueDate);
        textView.setText(dueDate);

        // Pop up a date chooser dialog when user clicks in the edittext field
        EditText et = (EditText) textView;
        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    TextView tvStart = (TextView) findViewById(R.id.etAddAssessmentDueDate);
                    Bundle bundle = new Bundle();
                    bundle.putString("activity", "assessment_due_date");
                    bundle.putString("curr_date", tvStart.getText().toString());

                    DialogFragment fragment = new DatePickerFragment();
                    fragment.setArguments(bundle);

                    fragment.show(getFragmentManager(), "Select Due Date");
                }
            }
        });

        if (action.equals("edit")) {
            CheckBox checkBox = (CheckBox) findViewById(R.id.cbAddAssessment);
            checkBox.setChecked(oldIsChecked);
        }

        // Set up the status spinner
        Spinner spinner;
        ArrayList<String> typeArray = new ArrayList<>();
        for (Assessment.Type status :
                Assessment.Type.values()) {
            typeArray.add(status.name());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, typeArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner = (Spinner) findViewById(R.id.assessmentTypeSpinner);
//        spinner.setFocusable(true);
//        spinner.setFocusableInTouchMode(true);
//        spinner.requestFocus();
        spinner.setAdapter(adapter);
        if (action.equals("edit")) {
            spinner.setSelection(assessment.getType().ordinal());
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

    public void onClickSaveAssessment(View view) throws ParseException {
        db.open();

        String name = ((EditText)findViewById(R.id.etAddAssessmentName)).getText().toString();

        Date dueDate = new Date();
        boolean invalidDate = false;

        // Get due date and convert it to a Date object
        EditText editText = (EditText) findViewById(R.id.etAddAssessmentDueDate);

        try {
            String datePattern = DateUtil.determineDateFormat(editText.getText().toString());
            if (datePattern == null)
                throw new ParseException("Unparseable date", 0);

            SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern, Locale.getDefault());
            dueDate = dateFormat.parse(editText.getText().toString());
        } catch (ParseException e) {
            Toast.makeText(getBaseContext(), "Invalid start date", Toast.LENGTH_LONG).show();
            invalidDate = true;
        }

        CheckBox checkBox = (CheckBox) findViewById(R.id.cbAddAssessment);

        // Convert the spinner type string to a type object
        Spinner spinner = (Spinner) findViewById(R.id.assessmentTypeSpinner);
        String str = spinner.getSelectedItem().toString();

        if (!invalidDate) {
            Assessment assessment = new Assessment(assessmentId, courseId, name, Assessment.Type.valueOf(str), dueDate, checkBox.isChecked());

            if (action.equals("edit")) {
                if (db.updateAssessment(assessment)) {
                    Toast.makeText(getBaseContext(), "Assessment has been updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "An error occurred while updating", Toast.LENGTH_SHORT).show();
                }

                if (oldIsChecked) {
                    if (checkBox.isChecked()) {
                        if (!oldDate.equals(dueDate)) {
                            // update alarm
                            // add new alarm
                            Reminder reminder = new Reminder(this);
                            reminder.startReminder(Reminder.REQUEST_HEADER_ASSESSMENT_DUEDATE, assessment, dueDate);
                        }
                    } else {
                        // remove alarm using oldDate
                        Reminder reminder = new Reminder(this);
                        reminder.killReminder(Reminder.REQUEST_HEADER_ASSESSMENT_DUEDATE, assessment);
                    }
                } else {
                    if (checkBox.isChecked()) {
                        // add alarm with dueDate
                        Reminder reminder = new Reminder(this);
                        reminder.startReminder(Reminder.REQUEST_HEADER_ASSESSMENT_DUEDATE, assessment, dueDate);
                    }
                }

            } else {
                long id = db.addAssessment(assessment).getId();
                if (checkBox.isChecked()) {
                    // add alarm with dueDate
                    Reminder reminder = new Reminder(this);
                    reminder.startReminder(Reminder.REQUEST_HEADER_ASSESSMENT_DUEDATE, db.getAssessment(id), dueDate);
                }
            }

            db.close();
            finish();
        }
    }
}
