package edu.ucsb.cs.cs190i.deannahpham.imagetagexplorer;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Deanna on 5/15/17.
 */

public class TagEntryFragment extends android.support.v4.app.DialogFragment {
    ArrayList<String> tagsList;

    private AutoCompleteTextView tag_text;
    private ImageView tag_image;
    private Button add_tag;
    private Button add_image;
    private String add_tag_string;

    public static TagEntryFragment newInstance(Uri uri) {
        TagEntryFragment frag = new TagEntryFragment();
        Bundle args = new Bundle();
        args.putString("title", uri.toString());
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

        String title = getArguments().getString("title", "Enter Name");
        Uri uri = Uri.parse((String)getArguments().get("title"));
        Picasso.with(getActivity()).load(uri).resize(500,500).centerCrop().into(tag_image);
        getDialog().setTitle(title);
        tag_text.requestFocus();

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
            }
        });

//        add_image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getContext(), "ADD IMAGE AND TAG TO DATABASE!", Toast.LENGTH_SHORT).show();
//
//            }
//        });


    }
}
