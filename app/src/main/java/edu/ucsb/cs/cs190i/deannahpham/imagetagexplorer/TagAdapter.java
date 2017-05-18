package edu.ucsb.cs.cs190i.deannahpham.imagetagexplorer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Deanna on 5/16/17.
 */

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ViewHolder> implements ImageTagDatabaseHelper.OnDatabaseChangeListener {
    private Context context;
    private ArrayList<String> tagsList;


    public TagAdapter(Context c, ArrayList<String> tags) {
        tagsList = tags;
        context = c;
    }


    @Override
    public TagAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View tag_view = inflater.inflate(R.layout.add_tag, parent, false);
        ViewHolder viewHolder = new ViewHolder(tag_view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(TagAdapter.ViewHolder holder, int position) {
        String tags = tagsList.get(position);
        TextView textView = holder.tag;
        textView.setText(tags);

    }

    @Override
    public int getItemCount() {
        return tagsList.size();
    }

    @Override
    public void OnDatabaseChange() {

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tag;

        public ViewHolder(View itemView) {
            super(itemView);
            tag = (TextView) itemView.findViewById(R.id.tags);
        }
    }
}
