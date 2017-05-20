package edu.ucsb.cs.cs190i.deannahpham.imagetagexplorer;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Samuel on 5/2/2017.
 */

public class ImageTagDatabaseHelper extends SQLiteOpenHelper {
    private static final String CreateImageTable = "CREATE TABLE Image (Id integer PRIMARY KEY AUTOINCREMENT, Uri text NOT NULL UNIQUE);";
    private static final String CreateTagTable = "CREATE TABLE Tag (Id integer PRIMARY KEY AUTOINCREMENT, Text text NOT NULL UNIQUE);";
    private static final String CreateLinkTable =
            "CREATE TABLE Link (ImageId integer, TagId integer, PRIMARY KEY (ImageId, TagId), " +
                    "FOREIGN KEY (ImageId) REFERENCES Image (Id) ON DELETE CASCADE ON UPDATE NO ACTION, " +
                    "FOREIGN KEY (TagId) REFERENCES Tag (Id) ON DELETE CASCADE ON UPDATE NO ACTION);";
    private static final String DatabaseName = "ImageTagDatabase.db";
    private static ImageTagDatabaseHelper Instance;
    private List<OnDatabaseChangeListener> Listeners;

    private ImageTagDatabaseHelper(Context context) {
        super(context, DatabaseName, null, 1);
        Listeners = new ArrayList<>();
    }

    public static void Initialize(Context context) {
        Instance = new ImageTagDatabaseHelper(context);
    }

    public static ImageTagDatabaseHelper GetInstance() {
        return Instance;
    }

    public void Subscribe(OnDatabaseChangeListener listener) {
        Listeners.add(listener);
    }

    private boolean TryUpdate(Cursor cursor) {
        try {
            cursor.moveToFirst();
        } catch (SQLiteConstraintException exception) {
            return false;
        } finally {
            cursor.close();
        }
        NotifyListeners();
        return true;
    }

    private void NotifyListeners() {
        for (OnDatabaseChangeListener listener : Listeners) {
            listener.OnDatabaseChange();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CreateImageTable);
        db.execSQL(CreateTagTable);
        db.execSQL(CreateLinkTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public interface OnDatabaseChangeListener {
        void OnDatabaseChange();
    }

    //HELP FROM: http://www.androidhive.info/2011/11/android-sqlite-database-tutorial/
    //http://stackoverflow.com/questions/15010761/how-to-check-if-a-cursor-is-empty
    //http://stackoverflow.com/questions/11461520/cursor-getint-throws-cursorindexoutofboundsexception
    //http://stackoverflow.com/questions/8907729/how-to-delete-tables-and-database-using-sqiltehelper-in-android

    //check functions to check if image, tag, or link exists already

    public boolean checkImageExist(String fileName) {
        return getImageId(fileName) != -1;
    }

    public boolean checkTagExist(String tag) {
        return getTagId(tag) != -1;
    }

    public boolean checkLinkExist(int fileId, int tagId) {
        SQLiteDatabase db = getWritableDatabase();

        String selectQuery = "SELECT * FROM Link WHERE ImageId=" + String.valueOf(fileId) + " AND "
                + "TagId=" + String.valueOf(tagId);

        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor != null && cursor.getCount() > 0;
    }

    // adding an image to the database via filename

    public void addImage(String filename){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues(); // got this from this website above
        values.put("Uri", filename);

        db.insert("Image", null, values);
        db.close();
        NotifyListeners();
    }

    //add tag into the database

    public void addTag(String tag){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("Text", tag);

        db.insert("Tag", null, values);
        db.close();
        NotifyListeners();
    }

    // now add a link between the image and the tag, given the imageId and tagId, which we will get from the next functions

    public void addLink(int id, int tag_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("ImageId", id);
        values.put("TagId", tag_id);

        db.insert("Link", null, values);
        db.close();
        NotifyListeners();
    }

    // function to get the imageId so that we can link it (to insert into link db) with tagId.

    public int getImageId(String filename) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("Image", new String[] { "Id" }, "Uri" + "=?",
                new String[] { filename}, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            return cursor.getInt(0);
        }
        return -1;
    }

    public int getTagId(String tag) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("Tag", new String[] { "Id" }, "Text" + "=?",
                new String[] { tag }, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            return cursor.getInt(0);
        }
        return -1;
    }

    //used this function to get all the images in the db to populate

    public List<String> getAllImages() {
        List<String> imageList = new ArrayList<String>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + "Image";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String imageUri = cursor.getString(1);
                imageList.add(imageUri);
            } while (cursor.moveToNext());
        }

        return imageList;
    }

    // given a list of tags, get the images associated with the tag
    // must first query for the tagId's associated with the given tags,
    // from there I get the associated imageId's from the link table via tagId,
    // then get the imageUri from the Image table from the imageId ,
    // eventually returning a list of imageUri's associated with the string of tags.

    public List<String> getImagesWithTag(String[] tags) {
        if (tags == null || tags.length == 0) return new ArrayList<>();

        List<Integer> tagIds = new ArrayList<>();
        String tagIdQuery = "SELECT Id FROM Tag WHERE Text IN (";
        for (String tag : tags) {
            tagIdQuery += "\'" + tag + "\'" + ", ";
        }

        tagIdQuery = tagIdQuery.substring(0, tagIdQuery.length() - 2);
        tagIdQuery += ")";

        Log.d("LOOKAT_QUERY", "Query Tag Id: " + tagIdQuery);

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(tagIdQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int tagId = cursor.getInt(0);
                tagIds.add(tagId);
            } while (cursor.moveToNext());
        }

        if (tagIds.isEmpty()) return new ArrayList<>();

        List<Integer> imageIds = new ArrayList<>();
        String imageIdQuery = "SELECT ImageId FROM Link WHERE TagId IN (";
        for (int tagId : tagIds) {
            imageIdQuery += String.valueOf(tagId) + ", ";
        }

        imageIdQuery = imageIdQuery.substring(0, imageIdQuery.length() -2);
        imageIdQuery += ")";

        Log.d("LOOKAT_QUERY", "Query Image Id: " + imageIdQuery);

        cursor = db.rawQuery(tagIdQuery, null);
        if (cursor.moveToFirst()) {
            do {
                int imageId = cursor.getInt(0);
                imageIds.add(imageId);
            } while (cursor.moveToNext());
        }

        if (imageIds.isEmpty()) return new ArrayList<>();

        List<String> imageUris = new ArrayList<>();

        String imageUriQuery = "SELECT Uri FROM Image WHERE Id IN (";
        for (int imageId : imageIds) {
            imageUriQuery += String.valueOf(imageId) + ", ";
        }

        imageUriQuery = imageUriQuery.substring(0, imageUriQuery.length() - 2);
        imageUriQuery += ")";

        cursor = db.rawQuery(imageUriQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String imageUri = cursor.getString(0);
                // Adding contact to list
                imageUris.add(imageUri);
            } while (cursor.moveToNext());
        }

        db.close();

        return imageUris;
    }

//    public void clearDatabase() {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.execSQL("DROP TABLE IF EXISTS Image");
//        db.execSQL("DROP TABLE IF EXISTS Tag");
//        db.execSQL("DROP TABLE IF EXISTS Link");
//
//        db.close();
//
//        NotifyListeners();
//
//    }

}