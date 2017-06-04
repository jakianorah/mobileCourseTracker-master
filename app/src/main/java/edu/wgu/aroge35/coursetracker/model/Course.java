package edu.wgu.aroge35.coursetracker.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by alissa on 10/30/15.
 */
public class Course {

    private long id;
    private long termId;
    private String title;
    private Date startDate;
    private boolean startDateReminder;
    private Date endDate;
    private boolean endDateReminder;
    private Status status;
    public Course() {
    }

    public Course(long termId, String title, Date startDate, boolean startDateReminder, Date endDate, boolean endDateReminder, Status status) {
        this.termId = termId;
        this.title = title;
        this.startDate = startDate;
        this.startDateReminder = startDateReminder;
        this.endDate = endDate;
        this.endDateReminder = endDateReminder;
        this.status = status;
    }

    public Course(long id, long termId, String title, Date startDate, boolean startDateReminder, Date endDate, boolean endDateReminder, Status status) {
        this.id = id;
        this.termId = termId;
        this.title = title;
        this.startDate = startDate;
        this.startDateReminder = startDateReminder;
        this.endDate = endDate;
        this.endDateReminder = endDateReminder;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTermId() {
        return termId;
    }

    public void setTermId(long termId) {
        this.termId = termId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isStartDateReminderSet() {
        return startDateReminder;
    }

    public void setStartDateReminder(boolean startDateReminder) {
        this.startDateReminder = startDateReminder;
    }

    public boolean isEndDateReminderSet() {
        return endDateReminder;
    }

    public void setEndDateReminder(boolean endDateReminder) {
        this.endDateReminder = endDateReminder;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Course) {
            Course obj = (Course) o;
            return obj.title.equals(this.title);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());

        return title + "\nStart: " + simpleDateFormat.format(startDate) + " End: " + simpleDateFormat.format(endDate);
    }

    public enum Status {
        IN_PROGRESS("In progress"),
        COMPLETED("Completed"),
        DROPPED("Dropped"),
        PLAN_TO_TAKE("Plan to take");

        private final String string;

        Status(String s) {
            string = s;
        }

        public String toString() {
            return string;
        }
    }
}
