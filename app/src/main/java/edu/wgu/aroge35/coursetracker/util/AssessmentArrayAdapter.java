package edu.wgu.aroge35.coursetracker.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import edu.wgu.aroge35.coursetracker.R;
import edu.wgu.aroge35.coursetracker.model.Assessment;

/**
 *
 */
public class AssessmentArrayAdapter extends ArrayAdapter<Assessment> {
    private final Context context;
    private final List<Assessment> data;

    public AssessmentArrayAdapter(Context context, List<Assessment> objects) {
        super(context, R.layout.list_item_assessment, objects);
        this.context = context;
        data = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = vi.inflate(R.layout.list_item_assessment, null);

        Assessment assessment = data.get(position);

        TextView tv = (TextView) view.findViewById(R.id.tvLine1);
        String str = assessment.getName();
        tv.setText(str);

        tv = (TextView) view.findViewById(R.id.tvLine2);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("LLL d, yyyy", Locale.getDefault());
        String display =  assessment.getType().toString() + ", Due: " + simpleDateFormat.format(assessment.getDueDate());
        tv.setText(display);

        return view;
    }
}
