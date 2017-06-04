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
import edu.wgu.aroge35.coursetracker.model.Term;

/**
 * Created by alissa on 12/2/15.
 */
public class TermArrayAdapter extends ArrayAdapter<Term> {
    private final Context context;
    private final List<Term> data;

    public TermArrayAdapter(Context context, List<Term> objects) {
        super(context, R.layout.list_item_term, objects);
        this.context = context;
        data = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = vi.inflate(R.layout.list_item_term, null);

        Term term = data.get(position);

        TextView tv = (TextView) view.findViewById(R.id.tvLine1);
        tv.setText(term.getName());

        tv = (TextView) view.findViewById(R.id.tvLine2);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("LLL d, yyyy", Locale.getDefault());
        String display = simpleDateFormat.format(term.getStartDate()) + " to " + simpleDateFormat.format(term.getEndDate());
        tv.setText(display);

        return view;
    }
}
