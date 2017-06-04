package edu.wgu.aroge35.coursetracker.util;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import edu.wgu.aroge35.coursetracker.MainActivity;
import edu.wgu.aroge35.coursetracker.R;

/**
 *
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private String mActivity;
    private String mDate;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int year;
        int month;
        int day;

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mActivity = bundle.getString("activity");
            mDate = bundle.getString("curr_date");
        }

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        try {
            calendar.setTime(sdf.parse(mDate));
        } catch (ParseException e) {
            // Log the exception, but continue wih current date
            Log.d(MainActivity.LOGTAG, e.getLocalizedMessage());
        }
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);


        //Create a new DatePickerDialog instance and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        TextView tv = null;

        // Get the TextView to change based on the data stored in the Bundle
        // Using if else because can't switch on String in older Android
        // and I don't feel like faffing with constants
        switch (mActivity) {
            case "term_start_date":
                tv = (TextView) getActivity().findViewById(R.id.etStartDate);
                break;
            case "term_end_date":
                tv = (TextView) getActivity().findViewById(R.id.etEndDate);
                break;
            case "course_start_date":
                tv = (TextView) getActivity().findViewById(R.id.etCourseStartDate);
                break;
            case "course_end_date":
                tv = (TextView) getActivity().findViewById(R.id.etCourseEndDate);
                break;
            case "assessment_due_date":
                tv = (TextView) getActivity().findViewById(R.id.etAddAssessmentDueDate);
                break;
        }

        String stringOfDate = (month + 1) + "/" + day + "/" + year;
        // If the Bundle contained an unknown activity string, it'll still be null
        // so let's do nothing for now
        if (tv != null) {
            tv.setText(stringOfDate);
        }
    }
}
