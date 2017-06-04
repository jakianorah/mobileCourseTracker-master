package edu.wgu.aroge35.coursetracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import edu.wgu.aroge35.coursetracker.data.DataSource;
import edu.wgu.aroge35.coursetracker.model.Term;
import edu.wgu.aroge35.coursetracker.util.TermArrayAdapter;

public class ViewTermListActivity extends AppCompatActivity {
    private static List<Term> terms;
    private DataSource db = new DataSource(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_term_list);
    }

    @Override
    protected void onResume() {
        super.onResume();
        db.open();
        terms = db.getAllTerms();
        db.close();

        TermsFragment fragment = new TermsFragment();
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_term_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_terms_new) {
            Intent intent = new Intent(ViewTermListActivity.this, AddTermActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.menu_term_list_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Term term = terms.get(info.position);

        switch (item.getItemId()) {
            case R.id.menu_terms_edit:
                Intent intent = new Intent(this, AddTermActivity.class);
                intent.putExtra("id", term.getId());
                startActivity(intent);
                return true;
            case R.id.menu_terms_delete:
                deleteTerm(term);
            default:
                return false;
        }
    }

    private void deleteTerm(Term term) {
        db.open();
        DataSource.Result result = db.removeTerm(this, term.getId());
        db.close();

        switch (result) {
            case RESULT_OK:
                Toast.makeText(getBaseContext(), "Term has been deleted", Toast.LENGTH_SHORT).show();
                onResume();
                break;
            case ERROR_TERM_NOT_EMPTY:
                Toast.makeText(getBaseContext(), "Term not empty\nDelete all courses and try again.", Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(getBaseContext(), "An error occurred while deleting term", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public static class TermsFragment extends ListFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            registerForContextMenu(getListView());
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);

            TermArrayAdapter adapter = new TermArrayAdapter(context, terms);
            setListAdapter(adapter);
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            super.onListItemClick(l, v, position, id);

            Term term = terms.get(position);
            Intent intent = new Intent(getContext(), ViewTermDetailsActivity.class);
            intent.putExtra("termId", term.getId());
            startActivity(intent);
        }
    }
}
