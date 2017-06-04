package edu.wgu.aroge35.coursetracker;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import edu.wgu.aroge35.coursetracker.data.DataSource;
import edu.wgu.aroge35.coursetracker.model.Term;
import edu.wgu.aroge35.coursetracker.util.DatePickerFragment;
import edu.wgu.aroge35.coursetracker.util.DateUtil;
import edu.wgu.aroge35.coursetracker.util.Reminder;

/**
 * Created by alissa on 11/27/15.
 */
public class AddTermActivity extends AppCompatActivity {

    private DataSource db = new DataSource(this);
    private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
    private String action;
    private Term term;

    private Date oldStartDate, oldEndDate;
    private boolean oldIsCheckedStartDate, oldIsCheckedEndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_term);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            action = "edit";
            db.open();
            term = db.getTerm(bundle.getLong("id"));
            oldStartDate = term.getStartDate();
            oldEndDate = term.getEndDate();
            oldIsCheckedStartDate = term.isStartDateReminderSet();
            oldIsCheckedEndDate = term.isEndDateReminderSet();
            db.close();
        } else {
            action = "add";
        }

        // Fill in the term name if we're editing
        if (action.equals("edit")) {
            setTitle("Edit Term");
            EditText editText = (EditText) findViewById(R.id.etTermName);
            editText.setText(term.getName());

            CheckBox checkBox = (CheckBox) findViewById(R.id.cbAddTermStartDate);
            checkBox.setChecked(oldIsCheckedStartDate);
            checkBox = (CheckBox) findViewById(R.id.cbAddTermEndDate);
            checkBox.setChecked(oldIsCheckedEndDate);
        }


        //Use the current date as the default first day in term
        Date startDate, endDate;
        if (action.equals("edit")) {
            startDate = term.getStartDate();
            endDate = term.getEndDate();
        } else {
            Calendar calendar = Calendar.getInstance();
            startDate = calendar.getTime();
            calendar.add(Calendar.MONTH, 6);
            endDate = calendar.getTime();
        }
        String firstDay = sdf.format(startDate);
        String lastDay = sdf.format(endDate);

        TextView textView = (TextView) findViewById(R.id.etStartDate);
        textView.setText(firstDay);

        EditText editText = (EditText) textView;
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                TextView tvStart = (TextView) findViewById(R.id.etStartDate);
                Bundle bundle = new Bundle();
                bundle.putString("activity", "term_start_date");
                bundle.putString("curr_date", tvStart.getText().toString());

                DialogFragment fragment = new DatePickerFragment();
                fragment.setArguments(bundle);

                fragment.show(getFragmentManager(), "Select Start Date");
            }
            }
        });

        textView = (TextView) findViewById(R.id.etEndDate);
        textView.setText(lastDay);

        editText = (EditText) textView;
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                TextView tvEnd = (TextView) findViewById(R.id.etEndDate);

                Bundle bundle = new Bundle();
                bundle.putString("activity", "term_end_date");
                bundle.putString("curr_date", tvEnd.getText().toString());

                DialogFragment fragment = new DatePickerFragment();
                fragment.setArguments(bundle);

                fragment.show(getFragmentManager(), "Select End Date");
            }
            }
        });

    }

    public void onClickSaveTerm(View view) {
        String name;
        Date startDate = new Date();
        Date endDate = new Date();
        boolean invalidDate = false;

        // Get name as string
        // It pains me deeply to not do any input validation here
        // but in the interest of time, and as it's not a requirement
        // I will be leaving it out
        EditText editText = (EditText) findViewById(R.id.etTermName);
        name = editText.getText().toString();

        // Get start date and convert it to a Date object
        editText = (EditText) findViewById(R.id.etStartDate);

        try {
            String datePattern = DateUtil.determineDateFormat(editText.getText().toString());
            if (datePattern == null)
                throw new ParseException("Unparseable date", 0);

            SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern, Locale.getDefault());
            startDate = dateFormat.parse(editText.getText().toString());
        } catch (ParseException e) {
            Toast.makeText(getBaseContext(), "Invalid start date", Toast.LENGTH_LONG).show();
            invalidDate = true;
        }

        CheckBox checkBoxStartDate = (CheckBox) findViewById(R.id.cbAddTermStartDate);

        // Get end date and convert it to a Date object
        editText = (EditText) findViewById(R.id.etEndDate);

        try {
            String datePattern = DateUtil.determineDateFormat(editText.getText().toString());
            if (datePattern == null)
                throw new ParseException("Unparseable date", 0);

            SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern, Locale.getDefault());
            endDate = dateFormat.parse(editText.getText().toString());
        } catch (ParseException e) {
            Toast.makeText(getBaseContext(), "Invalid end date", Toast.LENGTH_LONG).show();
            invalidDate = true;
        }

        CheckBox checkBoxEndDate = (CheckBox) findViewById(R.id.cbAddTermEndDate);

        if (name.equals("")) {
            Toast.makeText(getBaseContext(), "Term name can not be empty", Toast.LENGTH_LONG).show();
        } else if (!invalidDate) {
            db.open();
            Term newTerm = new Term(name, startDate, checkBoxStartDate.isChecked(), endDate, checkBoxEndDate.isChecked());

            if (action.equals("edit")) {
                newTerm.setId(term.getId());
                if (db.updateTerm(newTerm)) {
                    if (oldIsCheckedStartDate) {
                        if (checkBoxStartDate.isChecked()) {
                            if (!oldStartDate.equals(startDate)) {
                                // update alarm
                                Reminder reminder = new Reminder(this);
                                reminder.startReminder(Reminder.REQUEST_HEADER_TERM_STARTDATE, newTerm, startDate);
                            }
                        } else {
                            // remove alarm
                            Reminder reminder = new Reminder(this);
                            reminder.killReminder(Reminder.REQUEST_HEADER_TERM_STARTDATE, newTerm);
                        }
                    } else {
                        if (checkBoxStartDate.isChecked()) {
                            // add alarm with dueDate
                            Reminder reminder = new Reminder(this);
                            reminder.startReminder(Reminder.REQUEST_HEADER_TERM_STARTDATE, newTerm, startDate);
                        }
                    }

                    if (oldIsCheckedEndDate) {
                        if (checkBoxEndDate.isChecked()) {
                            if (!oldEndDate.equals(endDate)) {
                                // update alarm
                                Reminder reminderStart = new Reminder(this);
                                reminderStart.startReminder(Reminder.REQUEST_HEADER_TERM_ENDDATE, newTerm, endDate);
                            }
                        } else {
                            // remove alarm
                            Reminder reminder = new Reminder(this);
                            reminder.killReminder(Reminder.REQUEST_HEADER_TERM_ENDDATE, newTerm);
                        }
                    } else {
                        if (checkBoxEndDate.isChecked()) {
                            // add alarm with dueDate
                            Reminder reminder = new Reminder(this);
                            reminder.startReminder(Reminder.REQUEST_HEADER_TERM_ENDDATE, newTerm, endDate);
                        }
                    }

                    Toast.makeText(getBaseContext(), "Term has been updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "An error occurred while updating", Toast.LENGTH_SHORT).show();
                }
            } else {
                Term returned = db.createTerm(newTerm);
                long id = returned.getId();

                if (checkBoxStartDate.isChecked()) {
                    // add alarm with dueDate
                    Reminder reminder = new Reminder(this);
                    reminder.startReminder(Reminder.REQUEST_HEADER_TERM_STARTDATE, db.getTerm(id), startDate);
                }

                if (checkBoxEndDate.isChecked()) {
                    // add alarm with dueDate
                    Reminder reminder = new Reminder(this);
                    reminder.startReminder(Reminder.REQUEST_HEADER_TERM_ENDDATE, db.getTerm(id), endDate);
                }
            }

            db.close();
            finish();
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

}
