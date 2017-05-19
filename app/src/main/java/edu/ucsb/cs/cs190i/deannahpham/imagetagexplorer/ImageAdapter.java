package edu.ucsb.cs.cs190i.deannahpham.imagetagexplorer;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static edu.ucsb.cs.cs190i.deannahpham.imagetagexplorer.R.id.tags;

/**
 * Created by Deanna on 5/16/17.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private final Context context;
    private List<String> imageUriList;

    public ImageAdapter(Context context){
        this.context = context;
        //ImageTagDatabaseHelper.GetInstance().Subscribe(this);
    }

    public void setImageUris(List<String> imageUriList) {
        this.imageUriList = imageUriList;

        Log.d("LOOKHERE", "Setting new list of imageUris: size=" + imageUriList.size());

        notifyDataSetChanged();
    }

    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View image_view = inflater.inflate(R.layout.grid_images, parent, false);
        ImageAdapter.ViewHolder viewHolder = new ImageAdapter.ViewHolder(image_view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ImageAdapter.ViewHolder holder, int position) {
        Uri uri = Uri.parse(imageUriList.get(position));
        Picasso.with(holder.itemView.getContext()).load(new File(uri.getPath())).resize(240, 120).into(holder.image);


//        if ("content".equals(uri.getScheme())) {
//            try {
//                InputStream inputStream = holder.itemView.getContext().getContentResolver().openInputStream(uri);
//                holder.image.setImageBitmap(BitmapFactory.decodeStream(inputStream));
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//        } else {
//            Picasso.with(holder.itemView.getContext()).load(new File(uri.getPath())).resize(240, 120).into(holder.image);
//        }

        //Picasso.with(context).load(imageUri).resize(240, 120).into(holder.image);
        //Picasso.with(holder.itemView.getContext()).load(Uri.parse(imageUri)).resize(240, 120).into(holder.image);
//        Bitmap bitmap = BitmapFactory.decodeFile(imageUri);
//        holder.image.setImageBitmap(bitmap);

    }

    @Override
    public int getItemCount() {
        return imageUriList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }
}
