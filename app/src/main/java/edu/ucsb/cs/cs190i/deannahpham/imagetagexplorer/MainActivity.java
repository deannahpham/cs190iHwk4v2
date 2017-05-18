package edu.ucsb.cs.cs190i.deannahpham.imagetagexplorer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ImageTagDatabaseHelper.OnDatabaseChangeListener {

    private static final String IMAGE_PATH = "/deanna/images/";

    public FloatingActionButton camera;
    public FloatingActionButton gallery;
    public AutoCompleteTextView tag;
    public String search_for_tag;

    public final String APP_TAG = "MyCustomApp";
    public String photoFileName;
    private static final int PICK_IMAGE_REQUEST = 9876;
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;

    private GridLayoutManager manager;
    final ImageAdapter image_adapter = new ImageAdapter(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageTagDatabaseHelper.Initialize(this);

        RecyclerView rv_image = (RecyclerView)findViewById(R.id.rv_image);
        rv_image.setAdapter(image_adapter);

        manager = new GridLayoutManager(this, 2);
        rv_image.setLayoutManager(manager);


        tag = (AutoCompleteTextView) findViewById(R.id.tag);
        camera = (FloatingActionButton) findViewById(R.id.camera_fab);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCameraClick(v);
            }
        });
        gallery = (FloatingActionButton) findViewById(R.id.gallery_fab);
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGalleryClick(v);
            }
        });

        search_for_tag = tag.getText().toString();
    }


    public void onCameraClick(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //photoFileName="image"+System.currentTimeMillis()+".jpg";
        //intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri(photoFileName)); // set the image file name
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    public void onGalleryClick(View view) {
        Toast.makeText(this, "In Gallery", Toast.LENGTH_SHORT).show();
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        //photoFileName="image"+System.currentTimeMillis()+".jpg";
        //galleryIntent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri(photoFileName));
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
                if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                    if (resultCode == RESULT_OK) {
                        Toast.makeText(this, "Picture was taken!", Toast.LENGTH_SHORT).show();
//                        File mediaStorageDir = new File(
//                                getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
//
//                        // Return the file target for the photo based on filename
//                        File file = new File(mediaStorageDir.getPath() + File.separator + photoFileName);
                        Uri takenPhoto = data.getData();
                        TagEntryFragment frag = TagEntryFragment.newInstance(takenPhoto);
                        frag.show(getSupportFragmentManager().beginTransaction(), "tag");
                        InputStream inputStream;
                        try {
                            //address of image on SD card
                            inputStream = getContentResolver().openInputStream(takenPhoto);
                            Bitmap image = BitmapFactory.decodeStream(inputStream);
                            //image_adapter.addImage(image);

                        } catch (IOException ex) {
                            System.out.println(ex);
                            Toast.makeText(this, "Unable to open image", Toast.LENGTH_LONG).show();
                        }
                        //get path/uri and write to databse right here

                    } else { // Result was a failure
                        Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case PICK_IMAGE_REQUEST:
                if (resultCode == RESULT_OK) {
                    // if we are here, everything processed okay

                    if (requestCode == PICK_IMAGE_REQUEST) {
                        // if we are here, we are hearing back from image gallery
                        Uri takenPhoto = data.getData();
                        TagEntryFragment frag = TagEntryFragment.newInstance(takenPhoto);
                        frag.show(getSupportFragmentManager().beginTransaction(), "tag");

                        InputStream inputStream;
                        try {
                            //address of image on SD card
                            inputStream = getContentResolver().openInputStream(takenPhoto);
                            Bitmap image = BitmapFactory.decodeStream(inputStream);
                            //image_adapter.addImage(image);

                        } catch (IOException ex) {
                            System.out.println(ex);
                            Toast.makeText(this, "Unable to open image", Toast.LENGTH_LONG).show();
                        }
                    }
                }
                break;

        }
    }

//    public Uri getPhotoFileUri(String fileName) {
//
//        // Only continue if the SD Card is mounted
//        if (isExternalStorageAvailable()) {
//            // Get safe storage directory for photos
//            // Use `getExternalFilesDir` on Context to access package-specific directories.
//            // This way, we don't need to request external read/write runtime permissions.
//            File mediaStorageDir = new File(
//                    getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
//
//            // Create the storage directory if it does not exist
//            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
//                Log.d(APP_TAG, "failed to create directory");
//            }
//            File file = new File(mediaStorageDir.getPath() + File.separator + fileName);
//            return FileProvider.getUriForFile(MainActivity.this, "com.codepath.fileprovider", file);
//        }
//        return null;
//    }
//
//    // Returns true if external storage for photos is available
//    private boolean isExternalStorageAvailable() {
//        String state = Environment.getExternalStorageState();
//        return state.equals(Environment.MEDIA_MOUNTED);
//    }


    @Override
    protected void onResume() {
        super.onResume();
        ImageTagDatabaseHelper.GetInstance().Subscribe(this);
        getBitmapFromDb();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImageTagDatabaseHelper.GetInstance().close();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        // Show your dialog here (this is called right after onActivityResult)
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater(); // reads XML
        inflater.inflate(R.menu.actionbar, menu); // to create
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.populate:
                Toast.makeText(this, "Populate selected", Toast.LENGTH_SHORT).show();



                TaggedImageRetriever.getNumImages(new TaggedImageRetriever.ImageNumResultListener() {
                    @Override
                    public void onImageNum(int num) {
                        for (int i = 0; i < num; i++) {
                            //Toast.makeText(MainActivity.this, "num in databse: " + num, Toast.LENGTH_SHORT).show();

                            final int I_CLOSURE = i;
                            // this is referred to as an inner class closure. See, e.g. discussion at
                            // http://stackoverflow.com/questions/2804923/how-does-java-implement-inner-class-closures

                            TaggedImageRetriever.getTaggedImageByIndex(i, new TaggedImageRetriever.TaggedImageResultListener() {
                                @Override
                                public void onTaggedImage(TaggedImageRetriever.TaggedImage image) {
                                    if (image != null) {
                                        String filePath = getFilesDir() + IMAGE_PATH + "Test"+ I_CLOSURE + ".jpg";
                                        OutputStream stream = null;
                                        try {
                                            File file = new File(filePath);
                                            if (!file.exists()) {
                                                file.getParentFile().mkdirs();
                                            }

                                            stream = new FileOutputStream(new File(filePath));
                                            image.image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                                            image.image.recycle();
                                            stream.flush();
                                            stream.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                        if (!ImageTagDatabaseHelper.GetInstance().checkImageExist(filePath)) {
                                            ImageTagDatabaseHelper.GetInstance().addImage(filePath);
                                        }

                                        int imageId = ImageTagDatabaseHelper.GetInstance().getImageId(filePath);

                                        for(String tag : image.tags) {
                                            if (!ImageTagDatabaseHelper.GetInstance().checkTagExist(tag)) {
                                                ImageTagDatabaseHelper.GetInstance().addTag(tag);
                                            }

                                            int tagId = ImageTagDatabaseHelper.GetInstance().getTagId(tag);

                                            if (!ImageTagDatabaseHelper.GetInstance().checkLinkExist(imageId, tagId)) {
                                                ImageTagDatabaseHelper.GetInstance().addLink(imageId, tagId);
                                            }
//
//                                            Log.d("LOOKHERE", String.format("Inserted link with \n\tImage: (%s, %s)\n\tTag: (%s, %s)\n",
//                                                    imageId, Uri.parse(fileName).getLastPathSegment(), tagId, tag));
//
//                                            String[] tagQuery = new String[] { tag };
//                                            List<String> imageUris = ImageTagDatabaseHelper.GetInstance().getImagesWithTag(tagQuery);
//                                            Log.d("LOOKHERE", "Got " + imageUris.size() + " images");
                                        }



                                    }
                                }
                            });
                        }
                    }
                });

                break;
            case R.id.clear:
                Toast.makeText(this, "Clear selected", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void OnDatabaseChange() {
        Toast.makeText(this, "db", Toast.LENGTH_LONG).show();
        getBitmapFromDb();
    }

    public void getBitmapFromDb() {
        image_adapter.setImageUris(ImageTagDatabaseHelper.GetInstance().getAllImages());
    }
}

//HELP FROM: http://www.cs.ucsb.edu/~holl/CS190I/handouts/slides_camera-17.pdf
// https://github.com/codepath/android_guides/wiki/Accessing-the-Camera-and-Stored-Media
//http://stackoverflow.com/questions/5030565/multiple-onactivityresult-for-1-activity
//https://guides.codepath.com/android/Using-DialogFragment
//https://developer.android.com/reference/android/support/v4/content/FileProvider.html
//https://developer.android.com/reference/android/app/DialogFragment.html
//http://stackoverflow.com/questions/24682217/get-bitmap-from-imageview-loaded-with-picasso
//https://guides.codepath.com/android/using-the-recyclerview
//http://www.androidhive.info/2011/11/android-sqlite-database-tutorial/
