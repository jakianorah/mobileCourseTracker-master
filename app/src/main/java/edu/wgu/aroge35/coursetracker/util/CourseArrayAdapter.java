package edu.wgu.aroge35.coursetracker.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import edu.wgu.aroge35.coursetracker.R;
import edu.wgu.aroge35.coursetracker.model.Course;

/**
 * Created by alissa on 12/2/15.
 */
public class CourseArrayAdapter extends ArrayAdapter<Course> {
    private final Context context;
    private final List<Course> data;

    public CourseArrayAdapter(Context context, List<Course> objects) {
        super(context, R.layout.list_item_course, objects);
        this.context = context;
        data = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = vi.inflate(R.layout.list_item_course, null);

        Course course = data.get(position);

        TextView tv = (TextView) view.findViewById(R.id.tvLine1);
        tv.setText(course.getTitle());

        tv = (TextView) view.findViewById(R.id.tvLine2);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("LLL d, yyyy", Locale.getDefault());
        String display = simpleDateFormat.format(course.getStartDate()) + " to " + simpleDateFormat.format(course.getEndDate());
        tv.setText(display);

        String[] statusImages = new String[]{
                "ic_status_in_progress",
                "ic_status_complete",
                "ic_status_dropped",
                "ic_status_to_take"
        };



        return view;
    }
}
