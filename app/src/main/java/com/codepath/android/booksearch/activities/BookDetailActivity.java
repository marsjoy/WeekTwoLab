package com.codepath.android.booksearch.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.codepath.android.booksearch.R;
import com.codepath.android.booksearch.models.Book;

import org.parceler.Parcels;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BookDetailActivity extends AppCompatActivity {
    private ImageView ivBookCover;
    private TextView tvTitle;
    private TextView tvAuthor;
    private TextView tvPublishDate;
    private TextView tvDescription;
    private ShareActionProvider miShareAction;
    private Intent shareIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Fetch views
        ivBookCover = (ImageView) findViewById(R.id.ivBookCover);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvAuthor = (TextView) findViewById(R.id.tvAuthor);
        tvPublishDate = (TextView) findViewById(R.id.tvPublishDate);
        tvDescription = (TextView) findViewById(R.id.tvDescription);

        // Extract book object from intent extras
        Book book = Parcels.unwrap(getIntent().getParcelableExtra("book"));

        // Use book object to populate data into views
       loadBookIntoView(book);
    }

    private void loadBookIntoView(Book book) {
        // Set custom toolbar title
        getSupportActionBar().setTitle(book.getTitle());

        // Use book object to populate data into views
        Glide.with(BookDetailActivity.this)
                .load(Uri.parse(book.getCoverUrl()))
                .listener(new RequestListener<Uri, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        prepareShareIntent(
                                ((GlideBitmapDrawable) resource).getBitmap(),
                                book.getTitle(),
                                book.getAuthor());
                        attachShareIntentAction();
                        return false;
                    }
                })
                .into(ivBookCover);

        tvTitle.setText(book.getTitle());
        tvPublishDate.setText(book.getPublishDate());
        tvAuthor.setText(getString(R.string.author, book.getAuthor()));
        tvDescription.setText(book.getDescription());
    }

    // Gets the image URI and setup the associated share intent to hook into the provider
    public void prepareShareIntent(Bitmap drawableImage, String title, String author) {
        // Fetch Bitmap Uri locally
        Uri bmpUri = getLocalBitmapUriFromDrawable(drawableImage);// see previous remote images section and notes for API > 23

        // Construct share intent as described above based on bitmap
        shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_TEXT, title);
        shareIntent.putExtra(Intent.EXTRA_TEXT, author);
    }

    // Attaches the share intent to the share menu item provider
    public void attachShareIntentAction() {
        if (miShareAction != null && shareIntent != null)
            miShareAction.setShareIntent(shareIntent);
    }

    // Returns the URI path to the Bitmap displayed in cover imageview
    public Uri getLocalBitmapUriFromDrawable(Bitmap drawableImage) {
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file =  new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            drawableImage.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_book_detail, menu);

        // locate share action menu item
        MenuItem item = menu.findItem(R.id.action_share);

        // fetch the reference to the action provider
        miShareAction = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        attachShareIntentAction();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Launch share menu
        startActivity(Intent.createChooser(shareIntent, "Share Image"));

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
