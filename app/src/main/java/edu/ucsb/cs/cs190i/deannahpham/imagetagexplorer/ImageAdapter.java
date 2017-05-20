package edu.ucsb.cs.cs190i.deannahpham.imagetagexplorer;

import android.app.FragmentManager;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.Fragment;
import android.app.FragmentTransaction;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static edu.ucsb.cs.cs190i.deannahpham.imagetagexplorer.R.id.tags;

/**
 * Created by Deanna on 5/16/17.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    public final Context context;
    private List<String> imageUriList;
    private static final String IMAGE_PATH = "/deanna/images/";
    public int counter = 100;


    public ImageAdapter(Context context){
        this.context = context;
    }

    public void setImageUris(List<String> imageUriList) {
        this.imageUriList = imageUriList;
        Log.d("LOOKHERE", "Setting new list of imageUris: size=" + imageUriList.size());
        notifyDataSetChanged();
    }

    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        final View image_view = inflater.inflate(R.layout.grid_images, parent, false);
        ImageAdapter.ViewHolder viewHolder = new ImageAdapter.ViewHolder(image_view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ImageAdapter.ViewHolder holder, int position) {
        final Uri uri = Uri.parse(imageUriList.get(position));
        Picasso.with(holder.itemView.getContext()).load(new File(uri.getPath())).resize(500, 500).centerCrop().into(holder.image);

        if ("content".equals(uri.getScheme())) {
            Picasso.with(context).load(uri).resize(500, 500).centerCrop().into(holder.image);
        }

        final ImageView image = (ImageView)holder.itemView.findViewById(R.id.imageView);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter ++;
                Toast.makeText(image.getContext(), "Clicked on an image!" , Toast.LENGTH_SHORT).show();
                TagEntryFragment frag = TagEntryFragment.newInstance(uri.toString());
                frag.show(((MainActivity) v.getContext()).getSupportFragmentManager().beginTransaction(), "tag");

            }
        });
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
