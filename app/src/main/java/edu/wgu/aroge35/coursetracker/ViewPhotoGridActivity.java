package edu.wgu.aroge35.coursetracker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import edu.wgu.aroge35.coursetracker.model.Photo;
import edu.wgu.aroge35.coursetracker.util.GridViewAdapter;

/**
 * Created by jakia on 12/4/15.
 */
public class ViewPhotoGridActivity extends AppCompatActivity {
    private static final int RESULT_TAKE_PICTURE = 300;
    private String key;
    private File dir;
    private File path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image_grid);

        Bundle bundle = getIntent().getExtras();
        key = bundle.getString("key");

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        dir = new File(Environment.getExternalStorageDirectory() + "/" + getPackageName() + "/" + key);

        super.onResume();

        GridView gridView = (GridView) findViewById(R.id.gvImageGrid);
        GridViewAdapter gridAdapter = new GridViewAdapter(this, R.layout.list_item_image, getData());
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    String fileName = "" + new Date().getTime() + ".jpg";
                    path = new File(dir, fileName);
                    path.getParentFile().mkdirs();

                    startCameraActivity(path);
                } else {
                    Photo item = (Photo) parent.getItemAtPosition(position);
                    //Create intent
                    Intent intent = new Intent(ViewPhotoGridActivity.this, ViewPhotoActivity.class);
                    intent.putExtra("title", item.getTitle());
//                    intent.putExtra("image", item.getImage());
                    intent.putExtra("filename", item.getFilename());

                    //Start details activity
                    startActivity(intent);
                }
            }
        });

    }

    private void startCameraActivity(File path) {
        // Creates a Uri from a file
        Uri outputFileUri = Uri.fromFile(path);

        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(intent, RESULT_TAKE_PICTURE);
    }

    private ArrayList<Photo> getData() {
        final ArrayList<Photo> imageItems = new ArrayList<>();

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_camera);
        imageItems.add(new Photo(bitmap, "Take Photo"));

        File[] images = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".jpg");
            }
        });
        if (images != null) {
            for (File image : images) {
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy\nh:mm:ss", Locale.getDefault());
                String filename = image.getAbsolutePath();
                bitmap = BitmapFactory.decodeFile(filename);
                imageItems.add(new Photo(bitmap, sdf.format(image.lastModified()), filename));
            }
        }

        return imageItems;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_TAKE_PICTURE:
                Toast.makeText(getBaseContext(), "User cancelled", Toast.LENGTH_SHORT).show();
                break;
            case -1:
                break;
        }
    }
}
