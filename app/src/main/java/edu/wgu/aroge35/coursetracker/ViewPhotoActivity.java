package edu.wgu.aroge35.coursetracker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

/**
 * Created by alissa on 12/7/15.
 */
public class ViewPhotoActivity extends AppCompatActivity {
    private String filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        Bundle bundle = getIntent().getExtras();
        String title = bundle.getString("title", "");
//        image = bundle.getParcelable("image");
        filename = bundle.getString("filename", "");

        if (!filename.equals("")) {
            setTitle(title);

            TextView textView = (TextView) findViewById(R.id.tvViewImage);
            textView.setVisibility(View.INVISIBLE);

            Bitmap image = BitmapFactory.decodeFile(filename);

            ImageView imageView = (ImageView) findViewById(R.id.ivImage);
            imageView.setImageBitmap(image);

        } else {
            ImageView imageView = (ImageView) findViewById(R.id.ivImage);
            imageView.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photo_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_photo_delete:
                deletePhoto();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deletePhoto() {
        File file = new File(filename);
        if (file.isFile()) {
            if (file.delete()){
                Toast.makeText(this, "Photo has been deleted", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, "Error deleting photo", Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            Toast.makeText(this, "Error deleting photo", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
