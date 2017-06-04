package edu.wgu.aroge35.coursetracker.data;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.wgu.aroge35.coursetracker.model.Assessment;
import edu.wgu.aroge35.coursetracker.model.Course;
import edu.wgu.aroge35.coursetracker.model.CourseMentor;
import edu.wgu.aroge35.coursetracker.model.Term;
import edu.wgu.aroge35.coursetracker.util.Reminder;

/**
 *
 */
public class DataSource extends Activity {
    private static final String[] termsAllColumns = {
            DBHelper.COLUMN_ID,
            DBHelper.COLUMN_NAME,
            DBHelper.COLUMN_STARTDATE,
            DBHelper.COLUMN_STARTDATE_REMINDER,
            DBHelper.COLUMN_ENDDATE,
            DBHelper.COLUMN_ENDDATE_REMINDER
    };
    private static final String[] coursesAllColumns = {
            DBHelper.COLUMN_ID,
            DBHelper.COURSE_COLUMN_TERMID,
            DBHelper.COLUMN_NAME,
            DBHelper.COLUMN_STARTDATE,
            DBHelper.COLUMN_STARTDATE_REMINDER,
            DBHelper.COLUMN_ENDDATE,
            DBHelper.COLUMN_ENDDATE_REMINDER,
            DBHelper.COURSE_COLUMN_STATUS
    };
    private static final String[] assessmentsAllColumns = {
            DBHelper.COLUMN_ID,
            DBHelper.COLUMN_COURSEID,
            DBHelper.COLUMN_NAME,
            DBHelper.ASSESSMENT_COLUMN_TYPE,
            DBHelper.ASSESSMENT_COLUMN_DUEDATE,
            DBHelper.ASSESSMENT_COLUMN_DUEDATE_REMINDER
    };
    private static final String[] cmAllColumns = {
            DBHelper.COLUMN_ID,
            DBHelper.COLUMN_COURSEID,
            DBHelper.COLUMN_NAME,
            DBHelper.CM_COLUMN_EMAIL_WORK,
            DBHelper.CM_COLUMN_EMAIL_HOME,
            DBHelper.CM_COLUMN_EMAIL_OTHER,
            DBHelper.CM_COLUMN_PHONE_WORK,
            DBHelper.CM_COLUMN_PHONE_HOME,
            DBHelper.CM_COLUMN_PHONE_CELL
    };
    private SQLiteOpenHelper dbhelper;
    private SQLiteDatabase database;

    public DataSource(Context context) {
        dbhelper = new DBHelper(context);
    }

    public void open() {
        database = dbhelper.getWritableDatabase();
    }

    public void close() {
        dbhelper.close();
    }

    // Create term
    public Term createTerm(Term term) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_NAME, term.getName());
        values.put(DBHelper.COLUMN_STARTDATE, term.getStartDate().getTime());
        values.put(DBHelper.COLUMN_STARTDATE_REMINDER, term.isStartDateReminderSet());
        values.put(DBHelper.COLUMN_ENDDATE, term.getEndDate().getTime());
        values.put(DBHelper.COLUMN_ENDDATE_REMINDER, term.isEndDateReminderSet());
        long insertId = database.insert(DBHelper.TERM_TABLENAME, null, values);
        term.setId(insertId);
        return term;
    }

    // Edit term
    public boolean updateTerm(Term term) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_NAME, term.getName());
        values.put(DBHelper.COLUMN_STARTDATE, term.getStartDate().getTime());
        values.put(DBHelper.COLUMN_STARTDATE_REMINDER, term.isStartDateReminderSet());
        values.put(DBHelper.COLUMN_ENDDATE, term.getEndDate().getTime());
        values.put(DBHelper.COLUMN_ENDDATE_REMINDER, term.isEndDateReminderSet());
        String selection = DBHelper.COLUMN_ID + "=" + term.getId();
        int rows = database.update(DBHelper.TERM_TABLENAME, values, selection, null);
        return rows > 0;
    }

    // Retrieve all terms
    public List<Term> getAllTerms() {
        List<Term> returnList = new ArrayList<>();
        Cursor cursor = database.query(DBHelper.TERM_TABLENAME, termsAllColumns, null, null, null, null, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Term term = new Term();
                term.setId(cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMN_ID)));
                term.setName(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAME)));
                term.setStartDate(new Date(cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMN_STARTDATE))));
                term.setStartDateReminder(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_STARTDATE_REMINDER)) == 1);
                term.setEndDate(new Date(cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMN_ENDDATE))));
                term.setEndDateReminder(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_ENDDATE_REMINDER)) == 1);
                returnList.add(term);
            }
        }
        cursor.close();
        return returnList;
    }

    // Retrieve one term
    public Term getTerm(long termId) {
        Term term = new Term();
        String selection = DBHelper.COLUMN_ID + "=" + termId;
        Cursor cursor = database.query(DBHelper.TERM_TABLENAME, termsAllColumns, selection, null, null, null, null);
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            term.setId(cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMN_ID)));
            term.setName(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAME)));
            term.setStartDate(new Date(cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMN_STARTDATE))));
            term.setStartDateReminder(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_STARTDATE_REMINDER)) == 1);
            term.setEndDate(new Date(cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMN_ENDDATE))));
            term.setEndDateReminder(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_ENDDATE_REMINDER)) == 1);
            return term;
        }
        cursor.close();
        return null;
    }

    // Delete term if no courses
    public Result removeTerm(Context context, long termId) {
        String where = DBHelper.COURSE_COLUMN_TERMID + "=" + termId;
        Cursor cursor = database.query(DBHelper.COURSE_TABLENAME, new String[]{DBHelper.COLUMN_ID}, where, null, null, null, null);
        if (cursor.getCount() > 0) {
            return Result.ERROR_TERM_NOT_EMPTY;
        } else {
            // No courses for this term, so continue to delete
            // Remove any reminders
            if (getTerm(termId).isStartDateReminderSet()) {
                Reminder reminder = new Reminder(context);
                reminder.killReminder(Reminder.REQUEST_HEADER_TERM_STARTDATE, getTerm(termId));
            }

            if (getTerm(termId).isEndDateReminderSet()) {
                Reminder reminder = new Reminder(context);
                reminder.killReminder(Reminder.REQUEST_HEADER_TERM_ENDDATE, getTerm(termId));
            }

            where = DBHelper.COLUMN_ID + "=" + termId;
            int result = database.delete(DBHelper.TERM_TABLENAME, where, null);
            cursor.close();

            if (result > 0) {
                return Result.RESULT_OK;
            } else {
                return Result.ERROR_UNKNOWN_ERROR;
            }
        }
    }

    // Add course to term
    public Course addCourse(Course course) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COURSE_COLUMN_TERMID, course.getTermId());
        values.put(DBHelper.COLUMN_NAME, course.getTitle());
        values.put(DBHelper.COLUMN_STARTDATE, course.getStartDate().getTime());
        values.put(DBHelper.COLUMN_STARTDATE_REMINDER, course.isStartDateReminderSet());
        values.put(DBHelper.COLUMN_ENDDATE, course.getEndDate().getTime());
        values.put(DBHelper.COLUMN_ENDDATE_REMINDER, course.isEndDateReminderSet());
        values.put(DBHelper.COURSE_COLUMN_STATUS, course.getStatus().ordinal());
        long insertId = database.insert(DBHelper.COURSE_TABLENAME, null, values);
        course.setId(insertId);
        return course;
    }

    // Edit course
    public boolean updateCourse(Course course) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COURSE_COLUMN_TERMID, course.getTermId());
        values.put(DBHelper.COLUMN_NAME, course.getTitle());
        values.put(DBHelper.COLUMN_STARTDATE, course.getStartDate().getTime());
        values.put(DBHelper.COLUMN_STARTDATE_REMINDER, course.isStartDateReminderSet());
        values.put(DBHelper.COLUMN_ENDDATE, course.getEndDate().getTime());
        values.put(DBHelper.COLUMN_ENDDATE_REMINDER, course.isEndDateReminderSet());
        values.put(DBHelper.COURSE_COLUMN_STATUS, course.getStatus().ordinal());
        String selection = DBHelper.COLUMN_ID + "=" + course.getId();
        int rows = database.update(DBHelper.COURSE_TABLENAME, values, selection, null);
        return rows > 0;
    }

    // Retrieve all courses for term
    public List<Course> getAllCoursesForTerm(long termId) {
        List<Course> returnList = new ArrayList<>();
        Cursor cursor = database.query(DBHelper.COURSE_TABLENAME, coursesAllColumns, DBHelper.COURSE_COLUMN_TERMID + "=" + termId, null, null, null, DBHelper.COLUMN_ID + " ASC");
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Course course = new Course();
                course.setId(cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMN_ID)));
                course.setTermId(cursor.getLong(cursor.getColumnIndex(DBHelper.COURSE_COLUMN_TERMID)));
                course.setTitle(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAME)));
                course.setStartDate(new Date(cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMN_STARTDATE))));
                course.setStartDateReminder(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_STARTDATE_REMINDER)) == 1);
                course.setEndDate(new Date(cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMN_ENDDATE))));
                course.setEndDateReminder(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_ENDDATE_REMINDER)) == 1);
                course.setStatus(Course.Status.values()[cursor.getInt(cursor.getColumnIndex(DBHelper.COURSE_COLUMN_STATUS))]);
                returnList.add(course);
            }
        }
        cursor.close();
        return returnList;
    }

    // Retrieve one course for details
    public Course getCourse(long courseId) {
        Course course = new Course();
        String selection = DBHelper.COLUMN_ID + "=" + courseId;
        Cursor cursor = database.query(DBHelper.COURSE_TABLENAME, coursesAllColumns, selection, null, null, null, null);
        if (cursor.getCount() == 1) {
            cursor.moveToNext();
            course.setId(cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMN_ID)));
            course.setTermId(cursor.getLong(cursor.getColumnIndex(DBHelper.COURSE_COLUMN_TERMID)));
            course.setTitle(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAME)));
            course.setStartDate(new Date(cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMN_STARTDATE))));
            course.setStartDateReminder(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_STARTDATE_REMINDER)) == 1);
            course.setEndDate(new Date(cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMN_ENDDATE))));
            course.setEndDateReminder(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_ENDDATE_REMINDER)) == 1);
            course.setStatus(Course.Status.values()[cursor.getInt(cursor.getColumnIndex(DBHelper.COURSE_COLUMN_STATUS))]);
            cursor.close();
            return course;
        }
        cursor.close();
        return null;
    }

    // Delete course, including assessments and course mentors
    public Result removeCourse(Context context, long courseId) {
        List<Assessment> assessments = getAllAssessmentsForCourse(courseId);
        for (Assessment a : assessments) {
            removeAssessment(context, a.getId());
        }

        String where = DBHelper.COLUMN_COURSEID + "=" + courseId;
        database.delete(DBHelper.COURSEMENTOR_TABLENAME, where, null);

        if (getCourse(courseId).isStartDateReminderSet()) {
            Reminder reminder = new Reminder(context);
            reminder.killReminder(Reminder.REQUEST_HEADER_COURSE_STARTDATE, getCourse(courseId));
        }

        if (getCourse(courseId).isEndDateReminderSet()) {
            Reminder reminder = new Reminder(context);
            reminder.killReminder(Reminder.REQUEST_HEADER_COURSE_ENDDATE, getCourse(courseId));
        }

        where = DBHelper.COLUMN_ID + "=" + courseId;
        int result = database.delete(DBHelper.COURSE_TABLENAME, where, null);
        if (result == 1) {
            return Result.RESULT_OK;
        } else {
            return Result.ERROR_UNKNOWN_ERROR;
        }

    }

    // Add assessments to course
    public Assessment addAssessment(Assessment assessment) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_COURSEID, assessment.getCourseID());
        values.put(DBHelper.COLUMN_NAME, assessment.getName());
        values.put(DBHelper.ASSESSMENT_COLUMN_TYPE, assessment.getType().ordinal());
        values.put(DBHelper.ASSESSMENT_COLUMN_DUEDATE, assessment.getDueDate().getTime());
        values.put(DBHelper.ASSESSMENT_COLUMN_DUEDATE_REMINDER, assessment.isReminderSet());
        long insertId = database.insert(DBHelper.ASSESSMENT_TABLENAME, null, values);
        assessment.setId(insertId);
        return assessment;
    }

    // Edit assessment
    public boolean updateAssessment(Assessment assessment) {
        ContentValues values = new ContentValues();
        String selection = DBHelper.COLUMN_ID + "=" + assessment.getId();
        values.put(DBHelper.COLUMN_COURSEID, assessment.getCourseID());
        values.put(DBHelper.COLUMN_NAME, assessment.getName());
        values.put(DBHelper.ASSESSMENT_COLUMN_TYPE, assessment.getType().ordinal());
        values.put(DBHelper.ASSESSMENT_COLUMN_DUEDATE, assessment.getDueDate().getTime());
        values.put(DBHelper.ASSESSMENT_COLUMN_DUEDATE_REMINDER, assessment.isReminderSet());
        int rows = database.update(DBHelper.ASSESSMENT_TABLENAME, values, selection, null);
        return rows > 0;
    }

    // Retrieve all assessments for course
    public List<Assessment> getAllAssessmentsForCourse(long courseId) {
        List<Assessment> returnList = new ArrayList<>();
        Cursor cursor = database.query(DBHelper.ASSESSMENT_TABLENAME, assessmentsAllColumns, DBHelper.COLUMN_COURSEID + "=" + courseId, null, null, null, DBHelper.COLUMN_ID + " ASC");
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Assessment assessment = new Assessment();
                assessment.setId(cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMN_ID)));
                assessment.setCourseID(cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMN_COURSEID)));
                assessment.setName(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAME)));
                assessment.setType(Assessment.Type.values()[cursor.getInt(cursor.getColumnIndex(DBHelper.ASSESSMENT_COLUMN_TYPE))]);
                assessment.setDueDate(new Date(cursor.getLong(cursor.getColumnIndex(DBHelper.ASSESSMENT_COLUMN_DUEDATE))));
                assessment.setDueDateReminder((cursor.getInt(cursor.getColumnIndex(DBHelper.ASSESSMENT_COLUMN_DUEDATE_REMINDER)) == 1));
                returnList.add(assessment);
            }
        }
        cursor.close();
        return returnList;
    }

    // Retrieve one assessment for detail view
    public Assessment getAssessment(long assessmentId) {
        Assessment assessment = new Assessment();
        String selection = DBHelper.COLUMN_ID + "=" + assessmentId;
        Cursor cursor = database.query(DBHelper.ASSESSMENT_TABLENAME, assessmentsAllColumns, selection, null, null, null, null);
        if (cursor.getCount() == 1) {
            cursor.moveToNext();
            assessment.setId(cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMN_ID)));
            assessment.setCourseID(cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMN_COURSEID)));
            assessment.setName(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAME)));
            assessment.setType(Assessment.Type.values()[cursor.getInt(cursor.getColumnIndex(DBHelper.ASSESSMENT_COLUMN_TYPE))]);
            assessment.setDueDate(new Date(cursor.getLong(cursor.getColumnIndex(DBHelper.ASSESSMENT_COLUMN_DUEDATE))));
            assessment.setDueDateReminder((cursor.getInt(cursor.getColumnIndex(DBHelper.ASSESSMENT_COLUMN_DUEDATE_REMINDER)) == 1));
            cursor.close();
            return assessment;
        }
        cursor.close();
        return null;
    }

    // Delete assessment
    public Result removeAssessment(Context context, long assessmentId) {
        // Remove the reminder for this assessment, if it exists
        if (getAssessment(assessmentId).isReminderSet()) {
            Reminder reminder = new Reminder(context);
            reminder.killReminder(Reminder.REQUEST_HEADER_ASSESSMENT_DUEDATE, getAssessment(assessmentId));
        }

        String where = DBHelper.COLUMN_ID + "=" + assessmentId;
        int result = database.delete(DBHelper.ASSESSMENT_TABLENAME, where, null);
        if (result == 1) {
            return Result.RESULT_OK;
        } else {
            return Result.ERROR_UNKNOWN_ERROR;
        }
    }

    // Add course mentor to course
    public CourseMentor addCourseMentor(CourseMentor courseMentor) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_COURSEID, courseMentor.getCourseId());
        values.put(DBHelper.COLUMN_NAME, courseMentor.getName());
        values.put(DBHelper.CM_COLUMN_PHONE_WORK, courseMentor.getWorkPhone());
        values.put(DBHelper.CM_COLUMN_PHONE_HOME, courseMentor.getHomePhone());
        values.put(DBHelper.CM_COLUMN_PHONE_CELL, courseMentor.getCellPhone());
        values.put(DBHelper.CM_COLUMN_EMAIL_WORK, courseMentor.getWorkEmail());
        values.put(DBHelper.CM_COLUMN_EMAIL_HOME, courseMentor.getHomeEmail());
        values.put(DBHelper.CM_COLUMN_EMAIL_OTHER, courseMentor.getOtherEmail());
        long insertId = database.insert(DBHelper.COURSEMENTOR_TABLENAME, null, values);
        courseMentor.setId(insertId);
        return courseMentor;
    }

    // Edit course mentor
    public boolean updateCourseMentor(CourseMentor courseMentor) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_COURSEID, courseMentor.getCourseId());
        values.put(DBHelper.COLUMN_NAME, courseMentor.getName());
        values.put(DBHelper.CM_COLUMN_PHONE_WORK, courseMentor.getWorkPhone());
        values.put(DBHelper.CM_COLUMN_PHONE_HOME, courseMentor.getHomePhone());
        values.put(DBHelper.CM_COLUMN_PHONE_CELL, courseMentor.getCellPhone());
        values.put(DBHelper.CM_COLUMN_EMAIL_WORK, courseMentor.getWorkEmail());
        values.put(DBHelper.CM_COLUMN_EMAIL_HOME, courseMentor.getHomeEmail());
        values.put(DBHelper.CM_COLUMN_EMAIL_OTHER, courseMentor.getOtherEmail());
        String selection = DBHelper.COLUMN_ID + "=" + courseMentor.getId();
        int rows = database.update(DBHelper.COURSEMENTOR_TABLENAME, values, selection, null);
        return rows > 0;

    }

    // Retrieve all course mentors for course
    public List<CourseMentor> getAllCourseMentorsForCourse(long courseId) {
        List<CourseMentor> returnList = new ArrayList<>();
        Cursor cursor = database.query(DBHelper.COURSEMENTOR_TABLENAME, cmAllColumns, DBHelper.COLUMN_COURSEID + "=" + courseId, null, null, null, DBHelper.COLUMN_ID + " ASC");
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                CourseMentor courseMentor = new CourseMentor();
                courseMentor.setId(cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMN_ID)));
                courseMentor.setCourseId(cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMN_COURSEID)));
                courseMentor.setName(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAME)));
                courseMentor.setWorkPhone(cursor.getString(cursor.getColumnIndex(DBHelper.CM_COLUMN_PHONE_WORK)));
                courseMentor.setHomePhone(cursor.getString(cursor.getColumnIndex(DBHelper.CM_COLUMN_PHONE_HOME)));
                courseMentor.setCellPhone(cursor.getString(cursor.getColumnIndex(DBHelper.CM_COLUMN_PHONE_CELL)));
                courseMentor.setWorkEmail(cursor.getString(cursor.getColumnIndex(DBHelper.CM_COLUMN_EMAIL_WORK)));
                courseMentor.setHomeEmail(cursor.getString(cursor.getColumnIndex(DBHelper.CM_COLUMN_EMAIL_HOME)));
                courseMentor.setOtherEmail(cursor.getString(cursor.getColumnIndex(DBHelper.CM_COLUMN_EMAIL_OTHER)));
                returnList.add(courseMentor);
            }
        }
        cursor.close();
        return returnList;
    }

    // Retrieve one course mentor for detail view
    public CourseMentor getCourseMentor(long cmId) {
        CourseMentor courseMentor = new CourseMentor();
        String selection = DBHelper.COLUMN_ID + "=" + cmId;
        Cursor cursor = database.query(DBHelper.COURSEMENTOR_TABLENAME, cmAllColumns, selection, null, null, null, null);
        if (cursor.getCount() == 1) {
            cursor.moveToNext();
            courseMentor.setId(cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMN_ID)));
            courseMentor.setCourseId(cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMN_COURSEID)));
            courseMentor.setName(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAME)));
            courseMentor.setWorkPhone(cursor.getString(cursor.getColumnIndex(DBHelper.CM_COLUMN_PHONE_WORK)));
            courseMentor.setHomePhone(cursor.getString(cursor.getColumnIndex(DBHelper.CM_COLUMN_PHONE_HOME)));
            courseMentor.setCellPhone(cursor.getString(cursor.getColumnIndex(DBHelper.CM_COLUMN_PHONE_CELL)));
            courseMentor.setWorkEmail(cursor.getString(cursor.getColumnIndex(DBHelper.CM_COLUMN_EMAIL_WORK)));
            courseMentor.setHomeEmail(cursor.getString(cursor.getColumnIndex(DBHelper.CM_COLUMN_EMAIL_HOME)));
            courseMentor.setOtherEmail(cursor.getString(cursor.getColumnIndex(DBHelper.CM_COLUMN_EMAIL_OTHER)));
            cursor.close();
            return courseMentor;
        }
        cursor.close();
        return null;
    }

    // Delete course mentor
    public Result removeCourseMentor(long cmId) {
        String where = DBHelper.COLUMN_ID + "=" + cmId;
        int result = database.delete(DBHelper.COURSEMENTOR_TABLENAME, where, null);
        if (result == 1) {
            return Result.RESULT_OK;
        } else {
            return Result.ERROR_UNKNOWN_ERROR;
        }
    }

    public enum Result {
        RESULT_OK,
        ERROR_TERM_NOT_EMPTY,
        ERROR_UNKNOWN_ERROR
    }
}
