package edu.ucsb.cs.cs190i.deannahpham.imagetagexplorer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Deanna on 5/15/17.
 */

public class TagEntryFragment extends android.support.v4.app.DialogFragment {
    ArrayList<String> tagsList;

    private AutoCompleteTextView tag_text;
    private ImageView tag_image;
    private Button add_tag;
    private String add_tag_string;

    public static TagEntryFragment newInstance(String filePath) {
        TagEntryFragment frag = new TagEntryFragment();
        Bundle args = new Bundle();
        args.putString("title", filePath);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tag, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tag_text = (AutoCompleteTextView) view.findViewById(R.id.tag_text);
        tag_image = (ImageView) view.findViewById(R.id.tag_image);
        add_tag = (Button)view.findViewById(R.id.add_tag_button);
        //add_image = (Button)view.findViewById(R.id.add_image_button);


        final Uri uri = Uri.parse((String)getArguments().get("title"));
        Picasso.with(getActivity()).load(uri).resize(500,500).centerCrop().into(tag_image);
        tag_text.requestFocus();

        //tagList recycler stuff
        tagsList = new ArrayList<>();
        RecyclerView rv_tag = (RecyclerView) view.findViewById(R.id.rv_tag);
        final TagAdapter tag_adapter = new TagAdapter(this.getActivity(), tagsList);
        rv_tag.setAdapter(tag_adapter);
        rv_tag.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        add_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_tag_string = tag_text.getText().toString();
                tagsList.add(add_tag_string);
                tag_adapter.notifyDataSetChanged();
                tag_text.setText("");

                String filePath = getArguments().getString("title");

                if (!ImageTagDatabaseHelper.GetInstance().checkImageExist(filePath)) {
                    ImageTagDatabaseHelper.GetInstance().addImage(filePath);
                }

                int imageId = ImageTagDatabaseHelper.GetInstance().getImageId(filePath);

                for(String tag : tagsList) {
                    if (!ImageTagDatabaseHelper.GetInstance().checkTagExist(tag)) {
                        ImageTagDatabaseHelper.GetInstance().addTag(tag);
                    }

                    int tagId = ImageTagDatabaseHelper.GetInstance().getTagId(tag);

                    if (!ImageTagDatabaseHelper.GetInstance().checkLinkExist(imageId, tagId)) {
                        ImageTagDatabaseHelper.GetInstance().addLink(imageId, tagId);
                    }

                    // For debugging purposes:
                    Log.d("LOOKHERE", String.format("Inserted link with \n\tImage: (%s, %s)\n\tTag: (%s, %s)\n",
                            imageId, Uri.parse(filePath).getLastPathSegment(), tagId, tag));
                    String[] tagQuery = new String[] { tag };
                    List<String> imageUris = ImageTagDatabaseHelper.GetInstance().getImagesWithTag(tagQuery);
                    Log.d("LOOKHERE", "Got tag" + tag);
                    Log.d("LOOKHERE", "Got " + imageUris.size() + " images");

                }
                getDialog().dismiss();
            }
        });
    }
}
