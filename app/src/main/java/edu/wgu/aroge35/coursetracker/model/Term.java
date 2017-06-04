package edu.wgu.aroge35.coursetracker.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by alissa on 10/30/15.
 */
public class Term {

    private long id;
    private String name;
    private Date startDate; // Term start date
    private boolean startDateReminder;
    private Date endDate; // Term end date
    private boolean endDateReminder;

    public Term() {
    }

    public Term(String name, Date startDate, boolean startDateReminder, Date endDate, boolean endDateReminder) {
        this.name = name;
        this.startDate = startDate;
        this.startDateReminder = startDateReminder;
        this.endDate = endDate;
        this.endDateReminder = endDateReminder;
    }

    public Term(long id, String name, Date startDate, boolean startDateReminder, Date endDate, boolean endDateReminder) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.startDateReminder = startDateReminder;
        this.endDate = endDate;
        this.endDateReminder = endDateReminder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());

        return name + "\n(" + simpleDateFormat.format(startDate) + " to " + simpleDateFormat.format(endDate) + ")";
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
}
