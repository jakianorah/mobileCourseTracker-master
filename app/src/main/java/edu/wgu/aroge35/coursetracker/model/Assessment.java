package edu.wgu.aroge35.coursetracker.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by alissa on 11/22/15.
 */
public class Assessment {

    private long id;
    private long courseID;
    private String name;
    private Type type;
    private Date dueDate;
    private boolean dueDateReminder;

    public Assessment() {
    }

    public Assessment(long courseID, String name, Type type, Date dueDate, boolean dueDateReminder) {
        this.courseID = courseID;
        this.name = name;
        this.type = type;
        this.dueDate = dueDate;
        this.dueDateReminder = dueDateReminder;
    }

    public Assessment(long id, long courseID, String name, Type type, Date dueDate, boolean dueDateReminder) {
        this.id = id;
        this.courseID = courseID;
        this.name = name;
        this.type = type;
        this.dueDate = dueDate;
        this.dueDateReminder = dueDateReminder;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public long getCourseID() {
        return courseID;
    }

    public void setCourseID(long courseID) {
        this.courseID = courseID;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isReminderSet() {
        return dueDateReminder;
    }

    public void setDueDateReminder(boolean dueDateReminder) {
        this.dueDateReminder = dueDateReminder;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        return name + "(" + type.toString() + ")\nDue " + sdf.format(dueDate);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public enum Type {
        OBJECTIVE("Objective"),
        PERFORMANCE("Performance");

        private final String string;

        Type(String s) {
            string = s;
        }

        public String toString() {
            return this.string;
        }
    }
}
