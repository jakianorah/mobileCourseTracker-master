package edu.wgu.aroge35.coursetracker.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.Date;

import edu.wgu.aroge35.coursetracker.data.DataSource;
import edu.wgu.aroge35.coursetracker.model.Assessment;
import edu.wgu.aroge35.coursetracker.model.Course;
import edu.wgu.aroge35.coursetracker.model.Term;

/**
 * Created by alissa on 12/6/15.
 */
public class Reminder {
    public static final int REQUEST_HEADER_ASSESSMENT_DUEDATE = 10;
    public static final int REQUEST_HEADER_COURSE_STARTDATE = 20;
    public static final int REQUEST_HEADER_COURSE_ENDDATE = 25;
    public static final int REQUEST_HEADER_TERM_STARTDATE = 30;
    public static final int REQUEST_HEADER_TERM_ENDDATE = 35;

    private DataSource dataSource;

    private Context context;

    public Reminder(Context context) {
        this.context = context;
        dataSource = new DataSource(context);
    }

    private int getRequestCode(int header, long id) {
        return Integer.parseInt("" + header + id);
    }

    public void startReminder(int header, Object object, Date alarmDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(alarmDate);
        int requestCode = 0;
        String title = "Generic title";
        String text = "Generic text";
        Intent intent = new Intent(context, NotifyService.class);

        switch (header) {
            case REQUEST_HEADER_ASSESSMENT_DUEDATE: {
                Assessment assessment = (Assessment) object;
                requestCode = getRequestCode(REQUEST_HEADER_ASSESSMENT_DUEDATE, assessment.getId());
                title = assessment.getType().toString() + " Assessment Due";
                dataSource.open();
                text = dataSource.getCourse(assessment.getCourseID()).getTitle();
                dataSource.close();
                break;
            }
            case REQUEST_HEADER_COURSE_STARTDATE: {
                Course course = (Course) object;
                requestCode = getRequestCode(REQUEST_HEADER_COURSE_STARTDATE, course.getId());
                title = "Course due to start";
                text = course.getTitle();
                break;
            }
            case REQUEST_HEADER_COURSE_ENDDATE: {
                Course course = (Course) object;
                requestCode = getRequestCode(REQUEST_HEADER_COURSE_ENDDATE, course.getId());
                title = "Course due to end";
                text = course.getTitle();
                break;
            }
            case REQUEST_HEADER_TERM_STARTDATE: {
                Term term = (Term) object;
                requestCode = getRequestCode(REQUEST_HEADER_TERM_STARTDATE, term.getId());
                title = "Term due to start";
                text = term.getName();
                break;
            }
            case REQUEST_HEADER_TERM_ENDDATE: {
                Term term = (Term) object;
                requestCode = getRequestCode(REQUEST_HEADER_TERM_ENDDATE, term.getId());
                title = "Term due to end";
                text = term.getName();
                break;
            }
        }

        intent.putExtra("title", title);
        intent.putExtra("text", text);

        PendingIntent pendingIntent = PendingIntent.getService(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), 1000, pendingIntent);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public void killReminder(int header, Object object) {
        Intent intent = new Intent(context, NotifyService.class);
        int requestCode = 0;

        switch (header) {
            case REQUEST_HEADER_ASSESSMENT_DUEDATE: {
                Assessment assessment = (Assessment) object;
                long id = assessment.getId();
                requestCode = getRequestCode(header, id);
                break;
            }
            case REQUEST_HEADER_COURSE_STARTDATE: {
                Course course = (Course) object;
                long id = course.getId();
                requestCode = getRequestCode(header, id);
                break;
            }
            case REQUEST_HEADER_COURSE_ENDDATE: {
                Course course = (Course) object;
                long id = course.getId();
                requestCode = getRequestCode(header, id);
                break;
            }
            case REQUEST_HEADER_TERM_STARTDATE: {
                Term term = (Term) object;
                long id = term.getId();
                requestCode = getRequestCode(header, id);
                break;
            }
            case REQUEST_HEADER_TERM_ENDDATE: {
                Term term = (Term) object;
                long id = term.getId();
                requestCode = getRequestCode(header, id);
                break;
            }
        }

        PendingIntent pendingIntent = PendingIntent.getService(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}

