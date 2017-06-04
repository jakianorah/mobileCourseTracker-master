package edu.wgu.aroge35.coursetracker.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import edu.wgu.aroge35.coursetracker.R;
import edu.wgu.aroge35.coursetracker.model.CourseMentor;

/**
 *
 */
public class CourseMentorArrayAdapter extends ArrayAdapter<CourseMentor> {

    private final Context context;
    private final List<CourseMentor> data;

    public CourseMentorArrayAdapter(Context context, List<CourseMentor> objects) {
        super(context, R.layout.list_item_mentor, objects);
        this.context = context;
        data = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = vi.inflate(R.layout.list_item_mentor, null);

        CourseMentor courseMentor = data.get(position);

        TextView tv = (TextView) view.findViewById(R.id.tvLine1);
        tv.setText(courseMentor.getName());

        return view;
    }
}
