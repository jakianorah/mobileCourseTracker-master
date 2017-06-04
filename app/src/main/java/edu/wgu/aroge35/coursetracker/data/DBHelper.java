package edu.wgu.aroge35.coursetracker.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import edu.wgu.aroge35.coursetracker.MainActivity;

/**
 * SQLiteOpenHelper
 */
public class DBHelper extends SQLiteOpenHelper {
    // Common column names
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_COURSEID = "CourseID";
    public static final String COLUMN_NAME = "Name";
    public static final String COLUMN_STARTDATE = "StartDate";
    public static final String COLUMN_STARTDATE_REMINDER = "StartDateReminder";
    public static final String COLUMN_ENDDATE = "EndDate";
    public static final String COLUMN_ENDDATE_REMINDER = "EndDateReminder";

    // Table - Term
    public static final String TERM_TABLENAME = "terms";
    private static final String TERM_TABLE_CREATE =
            "CREATE TABLE " + TERM_TABLENAME + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_STARTDATE + " INTEGER, " +
                    COLUMN_STARTDATE_REMINDER + " INTEGER, " +
                    COLUMN_ENDDATE + " INTEGER, " +
                    COLUMN_ENDDATE_REMINDER + " INTEGER" +
                    ")";

    // Table - Course
    public static final String COURSE_TABLENAME = "courses";
    public static final String COURSE_COLUMN_TERMID = "TermID";
    public static final String COURSE_COLUMN_STATUS = "Status";
    private static final String COURSE_TABLE_CREATE =
            "CREATE TABLE " + COURSE_TABLENAME + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COURSE_COLUMN_TERMID + " INTEGER NOT NULL, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_STARTDATE + " INTEGER, " +
                    COLUMN_STARTDATE_REMINDER + " INTEGER, " +
                    COLUMN_ENDDATE + " INTEGER, " +
                    COLUMN_ENDDATE_REMINDER + " INTEGER, " +
                    COURSE_COLUMN_STATUS + " INTEGER, " +
                    "FOREIGN KEY(" + COURSE_COLUMN_TERMID + ") " +
                    "REFERENCES " + TERM_TABLENAME + "(" + COLUMN_ID + ")" +
                    ")";

    // Table - Assessment
    public static final String ASSESSMENT_TABLENAME = "assessments";
    public static final String ASSESSMENT_COLUMN_TYPE = "Type";
    public static final String ASSESSMENT_COLUMN_DUEDATE = "DueDate";
    public static final String ASSESSMENT_COLUMN_DUEDATE_REMINDER = "DueDateReminder";
    private static final String ASSESSMENT_TABLE_CREATE =
            "CREATE TABLE " + ASSESSMENT_TABLENAME + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_COURSEID + " INTEGER NOT NULL, " +
                    COLUMN_NAME + " TEXT, " +
                    ASSESSMENT_COLUMN_TYPE + " INTEGER NOT NULL, " +
                    ASSESSMENT_COLUMN_DUEDATE + " INTEGER, " +
                    ASSESSMENT_COLUMN_DUEDATE_REMINDER + " INTEGER, " +
                    "FOREIGN KEY(" + COLUMN_COURSEID + ") " +
                    "REFERENCES " + COURSE_TABLENAME + "(" + COLUMN_ID + ")" +
                    ")";

    // Table - CourseMentor
    public static final String COURSEMENTOR_TABLENAME = "coursementors";
    public static final String CM_COLUMN_EMAIL_WORK = "EmailWork";
    public static final String CM_COLUMN_EMAIL_HOME = "EmailHome";
    public static final String CM_COLUMN_EMAIL_OTHER = "EmailOther";
    public static final String CM_COLUMN_PHONE_WORK = "PhoneWork";
    public static final String CM_COLUMN_PHONE_HOME = "PhoneHome";
    public static final String CM_COLUMN_PHONE_CELL = "PhoneCell";
    private static final String COURSEMENTOR_TABLE_CREATE =
            "CREATE TABLE " + COURSEMENTOR_TABLENAME + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_COURSEID + " INTEGER NOT NULL, " +
                    COLUMN_NAME + " TEXT, " +
                    CM_COLUMN_PHONE_WORK + " TEXT, " +
                    CM_COLUMN_PHONE_HOME + " TEXT, " +
                    CM_COLUMN_PHONE_CELL + " TEXT, " +
                    CM_COLUMN_EMAIL_WORK + " TEXT, " +
                    CM_COLUMN_EMAIL_HOME + " TEXT, " +
                    CM_COLUMN_EMAIL_OTHER + " TEXT, " +
                    "FOREIGN KEY(" + COLUMN_COURSEID + ") " +
                    "REFERENCES " + COURSE_TABLENAME + "(" + COLUMN_ID + ")" +
                    ")";

    private static final String DB_NAME = "course_tracker.db";
    private static final int DB_VERSION = 7;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TERM_TABLE_CREATE);
        db.execSQL(COURSE_TABLE_CREATE);
        db.execSQL(ASSESSMENT_TABLE_CREATE);
        db.execSQL(COURSEMENTOR_TABLE_CREATE);
        Log.i(MainActivity.LOGTAG, "Tables have been created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + COURSEMENTOR_TABLENAME);
        db.execSQL("DROP TABLE IF EXISTS " + ASSESSMENT_TABLENAME);
        db.execSQL("DROP TABLE IF EXISTS " + COURSE_TABLENAME);
        db.execSQL("DROP TABLE IF EXISTS " + TERM_TABLENAME);
        onCreate(db);
        Log.i(MainActivity.LOGTAG, "Database has been upgraded from v" + oldVersion + " to v" + newVersion);
    }
}
