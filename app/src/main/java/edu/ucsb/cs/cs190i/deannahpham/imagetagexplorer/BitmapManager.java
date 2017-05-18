package edu.ucsb.cs.cs190i.deannahpham.imagetagexplorer;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by Deanna on 5/16/17.
 */

public class BitmapManager {
    public static ArrayList<Bitmap> bitmapList = new ArrayList<>();

    public static void addBitmap(Bitmap bitmap) {
        bitmapList.add(bitmap);
    }
}
