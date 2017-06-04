package edu.wgu.aroge35.coursetracker;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.wgu.aroge35.coursetracker.data.DataSource;
import edu.wgu.aroge35.coursetracker.model.Course;
import edu.wgu.aroge35.coursetracker.model.CourseMentor;
import edu.wgu.aroge35.coursetracker.util.DatePickerFragment;
import edu.wgu.aroge35.coursetracker.util.DateUtil;
import edu.wgu.aroge35.coursetracker.util.Reminder;

/**
 * Created by alissa on 11/27/15.
 */
public class AddCourseActivity extends AppCompatActivity {
    private static final int CREATE_MENTOR_RESULT = 100;
    private long termId;
    private long courseId;
    private Course course;
    private String action;
    private Date startDate, endDate;
    private Date oldStartDate, oldEndDate;
    private boolean oldIsCheckedStartDate, oldIsCheckedEndDate;
    private DataSource db = new DataSource(this);
    private List<CourseMentor> courseMentors = new ArrayList<>();
    private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        Bundle bundle = getIntent().getExtras();
        termId = bundle.getLong("termId");
        action = bundle.getString("action", "add");
        courseId = bundle.getLong("id");

        if (action.equals("edit")) {
            setTitle("Edit Course");
            db.open();
            course = db.getCourse(courseId);
            oldStartDate = course.getStartDate();
            oldEndDate = course.getEndDate();
            oldIsCheckedStartDate = course.isStartDateReminderSet();
            oldIsCheckedEndDate = course.isEndDateReminderSet();
            courseMentors = db.getAllCourseMentorsForCourse(courseId);
            db.close();
        }

        if (action.equals("edit")) {
            EditText editText = (EditText) findViewById(R.id.etCourseName);
            editText.setText(course.getTitle());
        }

        //Use the current date as the default first day in term
        if (action.equals("edit")) {
            startDate = course.getStartDate();
            endDate = course.getEndDate();
        } else {
            Calendar calendar = Calendar.getInstance();
            startDate = calendar.getTime();
            calendar.add(Calendar.MONTH, 1);
            endDate = calendar.getTime();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        String firstDay = sdf.format(startDate);
        String lastDay = sdf.format(endDate);

        TextView textView = (TextView) findViewById(R.id.etCourseStartDate);
        textView.setText(firstDay);

        // Pop up a date chooser dialog when user clicks in the edittext field
//        EditText et = (EditText) findViewById(R.id.etCourseStartDate);
        EditText et = (EditText) textView;
        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    TextView tvStart = (TextView) findViewById(R.id.etCourseStartDate);
                    Bundle bundle = new Bundle();
                    bundle.putString("activity", "course_start_date");
                    bundle.putString("curr_date", tvStart.getText().toString());

                    DialogFragment fragment = new DatePickerFragment();
                    fragment.setArguments(bundle);

                    fragment.show(getFragmentManager(), "Select Start Date");
                }
            }
        });

        if (action.equals("edit")) {
            CheckBox checkBox = (CheckBox) findViewById(R.id.cbAddCourseStartDate);
            checkBox.setChecked(oldIsCheckedStartDate);
            checkBox = (CheckBox) findViewById(R.id.cbAddCourseEndDate);
            checkBox.setChecked(oldIsCheckedEndDate);
        }

        // Set up the end date textview
        textView = (TextView) findViewById(R.id.etCourseEndDate);
        textView.setText(lastDay);

        // Pop up a date chooser dialog when user clicks in the edittext field
        et = (EditText) textView;
        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    TextView tvEnd = (TextView) findViewById(R.id.etCourseEndDate);

                    Bundle bundle = new Bundle();
                    bundle.putString("activity", "course_end_date");
                    bundle.putString("curr_date", tvEnd.getText().toString());

                    DialogFragment fragment = new DatePickerFragment();
                    fragment.setArguments(bundle);

                    fragment.show(getFragmentManager(), "Select End Date");
                }
            }
        });

        // Set up the status spinner
        Spinner spinner;
        List<String> statusArray = new ArrayList<>();
        for (Course.Status status :
                Course.Status.values()) {
            statusArray.add(status.name());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner = (Spinner) findViewById(R.id.courseStatusSpinner);
        spinner.setAdapter(adapter);
        if (action.equals("edit")) {
            spinner.setSelection(course.getStatus().ordinal());
        }

        // Fill the course mentor list if we're editing
        int count = 0;
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.llCourseMentorArray);
        linearLayout.removeAllViews();
        for (final CourseMentor cm : courseMentors) {
            final int position = count;
            LinearLayout ll = new LinearLayout(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, dpToPixels(10));
            ll.setLayoutParams(params);
            ll.setGravity(Gravity.CENTER_VERTICAL);
            ll.setOrientation(LinearLayout.HORIZONTAL);

            ImageView iv = new ImageView(this);
            iv.setLayoutParams(new LinearLayout.LayoutParams(dpToPixels(15), dpToPixels(15)));
            iv.setImageResource(R.drawable.ic_edit);
            iv.setPadding(0, 0, 0, 0);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AddCourseActivity.this, AddCourseMentorActivity.class);
                    intent.putExtra("action", "edit");

                    intent.putExtra("cmId", cm.getId());
                    intent.putExtra("position", position);
                    intent.putExtra("courseId", cm.getCourseId());
                    intent.putExtra("name", cm.getName());
                    intent.putExtra("workPhone", cm.getWorkPhone());
                    intent.putExtra("homePhone", cm.getHomePhone());
                    intent.putExtra("cellPhone", cm.getCellPhone());
                    intent.putExtra("workEmail", cm.getWorkEmail());
                    intent.putExtra("homeEmail", cm.getHomeEmail());
                    intent.putExtra("otherEmail", cm.getOtherEmail());

                    startActivityForResult(intent, CREATE_MENTOR_RESULT);
                }
            });
            ll.addView(iv);

            iv = new ImageView(this);
            params = new LinearLayout.LayoutParams(dpToPixels(15), dpToPixels(15));
            params.setMargins(dpToPixels(5), 0, 0, 0);
            iv.setLayoutParams(params);
            iv.setImageResource(R.drawable.ic_delete);
            iv.setPadding(0, 0, 0, 0);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteCM(position, cm);
                }
            });
            ll.addView(iv);

            TextView tv = new TextView(this);
            tv.setText(cm.getName());
            params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            tv.setLayoutParams(params);
            tv.setPadding(dpToPixels(10), 0, 0, 0);
            tv.setGravity(Gravity.CENTER_VERTICAL);
            tv.setTextSize(18);
            tv.setSingleLine(true);
            tv.setEllipsize(TextUtils.TruncateAt.END);
            ll.addView(tv);

            linearLayout.addView(ll);
            count++;
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
                intent.putExtra("action", "edit");

                intent.putExtra("cmId", courseMentor.getId());
                intent.putExtra("position", info.position);
                intent.putExtra("courseId", courseMentor.getCourseId());
                intent.putExtra("name", courseMentor.getName());
                intent.putExtra("workPhone", courseMentor.getWorkPhone());
                intent.putExtra("homePhone", courseMentor.getHomePhone());
                intent.putExtra("cellPhone", courseMentor.getCellPhone());
                intent.putExtra("workEmail", courseMentor.getWorkEmail());
                intent.putExtra("homeEmail", courseMentor.getHomeEmail());
                intent.putExtra("otherEmail", courseMentor.getOtherEmail());

                startActivityForResult(intent, CREATE_MENTOR_RESULT);
                return true;
            case R.id.menu_cm_delete:
                deleteCM(info.position, courseMentor);
                return true;
            default:
                return false;
        }
    }

    private void deleteCM(int position, CourseMentor courseMentor) {
        DataSource.Result result;

        if (courseMentor.getId() > 0) {
            db.open();
            result = db.removeCourseMentor(courseMentor.getId());
            db.close();
            courseMentors.remove(position);
        } else {
            // Not in the database yet, so just remove it from the List
            courseMentors.remove(position);
            result = DataSource.Result.RESULT_OK;
        }

        switch (result) {
            case RESULT_OK:
                Toast.makeText(getBaseContext(), "Mentor has been deleted", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(getBaseContext(), "An error occurred while deleting mentor", Toast.LENGTH_SHORT).show();
        }

        onResume();
    }

    public void onClickSaveCourse(View view) {
        db.open();
        // I'm going to assume that all added terms are new
        // because error handling isn't required
        String name;
        Date startDate = new Date();
        Date endDate = new Date();
        boolean invalidDate = false;

        // Get name as string
        // It pains me deeply to not do any input validation here
        // but in the interest of time, and as it's not a requirement
        // I will be leaving it out
        EditText editText = (EditText) findViewById(R.id.etCourseName);
        name = editText.getText().toString();

        // Get start date and convert it to a Date object
        editText = (EditText) findViewById(R.id.etCourseStartDate);

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

        CheckBox checkBoxStartDate = (CheckBox) findViewById(R.id.cbAddCourseStartDate);

        // Get end date and convert it to a Date object
        editText = (EditText) findViewById(R.id.etCourseEndDate);

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

        CheckBox checkBoxEndDate = (CheckBox) findViewById(R.id.cbAddCourseEndDate);

        // Convert the spinner status string to a status object
        Spinner spinner = (Spinner) findViewById(R.id.courseStatusSpinner);
        String str = spinner.getSelectedItem().toString();

        if (name.equals("")) {
            Toast.makeText(getBaseContext(), "Course name can not be empty", Toast.LENGTH_LONG).show();
        } else if (!invalidDate) {
            Course newCourse = new Course(termId, name, startDate, checkBoxStartDate.isChecked(), endDate, checkBoxEndDate.isChecked(), Course.Status.valueOf(str));

            if (action.equals("edit")) {
                newCourse.setId(courseId);
                if (db.updateCourse(newCourse)) {
                    for (CourseMentor c : courseMentors) {
                        if (c.getId() == 0) {
                            c.setCourseId(courseId);
                            db.addCourseMentor(c);
                        } else {
                            db.updateCourseMentor(c);
                        }
                    }

                    if (oldIsCheckedStartDate) {
                        if (checkBoxStartDate.isChecked()) {
                            if (!oldStartDate.equals(startDate)) {
                                // update alarm
                                // remove old alarm
                                //Reminder reminderKill = new Reminder(this);
                                //reminderKill.killReminder(Reminder.REQUEST_HEADER_COURSE_STARTDATE, newCourse);
                                // add new alarm
                                Reminder reminderStart = new Reminder(this);
                                reminderStart.startReminder(Reminder.REQUEST_HEADER_COURSE_STARTDATE, newCourse, startDate);
                            }
                        } else {
                            // remove alarm
                            Reminder reminder = new Reminder(this);
                            reminder.killReminder(Reminder.REQUEST_HEADER_COURSE_STARTDATE, newCourse);
                        }
                    } else {
                        if (checkBoxStartDate.isChecked()) {
                            // add alarm with dueDate
                            Reminder reminder = new Reminder(this);
                            reminder.startReminder(Reminder.REQUEST_HEADER_COURSE_STARTDATE, newCourse, startDate);
                        }
                    }

                    if (oldIsCheckedEndDate) {
                        if (checkBoxEndDate.isChecked()) {
                            if (!oldEndDate.equals(endDate)) {
                                // update alarm
                                // remove old alarm
                                //Reminder reminderKill = new Reminder(this);
                                //reminderKill.killReminder(Reminder.REQUEST_HEADER_COURSE_ENDDATE, newCourse);
                                // add new alarm
                                Reminder reminderStart = new Reminder(this);
                                reminderStart.startReminder(Reminder.REQUEST_HEADER_COURSE_ENDDATE, newCourse, endDate);
                            }
                        } else {
                            // remove alarm
                            Reminder reminder = new Reminder(this);
                            reminder.killReminder(Reminder.REQUEST_HEADER_COURSE_ENDDATE, newCourse);
                        }
                    } else {
                        if (checkBoxEndDate.isChecked()) {
                            // add alarm with dueDate
                            Reminder reminder = new Reminder(this);
                            reminder.startReminder(Reminder.REQUEST_HEADER_COURSE_ENDDATE, newCourse, endDate);
                        }
                    }

                    Toast.makeText(getBaseContext(), "Course has been updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "An error occurred while updating", Toast.LENGTH_SHORT).show();
                }
            } else {
                Course returned = db.addCourse(newCourse);
                long id = returned.getId();

                if (courseMentors.size() > 0) {
                    for (CourseMentor cm : courseMentors) {
                        cm.setCourseId(id);
                        db.addCourseMentor(cm);
                    }
                }

                if (checkBoxStartDate.isChecked()) {
                    // add alarm with dueDate
                    Reminder reminder = new Reminder(this);
                    reminder.startReminder(Reminder.REQUEST_HEADER_COURSE_STARTDATE, db.getCourse(id), startDate);
                }

                if (checkBoxEndDate.isChecked()) {
                    // add alarm with dueDate
                    Reminder reminder = new Reminder(this);
                    reminder.startReminder(Reminder.REQUEST_HEADER_COURSE_ENDDATE, db.getCourse(id), endDate);
                }

            }

            db.close();
            finish();
        }
    }

    public void onClickAddMentor(View view) {
        Intent intent = new Intent(getBaseContext(), AddCourseMentorActivity.class);
        intent.putExtra("action", "add");
        intent.putExtra("courseId", courseId);
        startActivityForResult(intent, CREATE_MENTOR_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CREATE_MENTOR_RESULT) {
            if (resultCode == RESULT_OK) {
                int position = data.getIntExtra("position", -1);

                CourseMentor courseMentor = new CourseMentor();
                courseMentor.setId(data.getLongExtra("cmId", 0));
                courseMentor.setCourseId(data.getLongExtra("courseId", courseId));
                courseMentor.setName(data.getStringExtra("etCMName"));
                courseMentor.setWorkPhone(data.getStringExtra("etWorkPhone"));
                courseMentor.setHomePhone(data.getStringExtra("etHomePhone"));
                courseMentor.setCellPhone(data.getStringExtra("etCellPhone"));
                courseMentor.setWorkEmail(data.getStringExtra("etEmailWork"));
                courseMentor.setHomeEmail(data.getStringExtra("etHomeEmail"));
                courseMentor.setOtherEmail(data.getStringExtra("etOtherEmail"));
                if (position > -1) {
                    courseMentors.set(position, courseMentor);
                } else {
                    courseMentors.add(courseMentor);
                }
            }
        }

    }

    private int dpToPixels(int dp) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

}
