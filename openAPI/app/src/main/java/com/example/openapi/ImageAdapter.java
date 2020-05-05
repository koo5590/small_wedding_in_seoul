package com.example.openapi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<WeddingObj> weddingList;

    public ImageAdapter(Context context, ArrayList<WeddingObj> weddingList) {
        this.context = context;
        this.weddingList = weddingList;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (convertView == null) {

            gridView = new View(context);

            // get layout from mobile.xml
            gridView = inflater.inflate(R.layout.mobile, null);


        }
        else gridView = (View) convertView;

        // set value into textview
        TextView textView = (TextView) gridView.findViewById(R.id.grid_item_label);
        textView.setText(weddingList.get(position).getName());

        // set image based on selected text
        ImageView imageView = (ImageView) gridView
                .findViewById(R.id.grid_item_image);


        imageView.setImageResource(R.drawable.wedding);

        return gridView;
    }

    @Override
    public int getCount() {
        return weddingList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}