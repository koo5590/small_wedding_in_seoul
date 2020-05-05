package com.example.openapi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/****** Adaptor for review list in DetailActivity *********/
public class ReviewAdaptor extends BaseAdapter{

    private ArrayList<ReviewItem> reviewList;
    private LayoutInflater layoutInflater;

    //Constructor
    public ReviewAdaptor(ArrayList<ReviewItem> reviewList, Context context){
        this.reviewList = reviewList;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //set reviewList and notify that the data has changed
    private void setReviewList(ArrayList<ReviewItem> reviewList){
        this.reviewList = reviewList;
        this.notifyDataSetChanged();
    }

    //return # of items in reviewList
    @Override
    public int getCount(){
        return reviewList.size();
    }

    //return item at *position* in reviewList
    @Override
    public Object getItem(int position){
        if(0<=position && position<reviewList.size()) return reviewList.get(position);
        else return null;
    }

    //return the index of the item in reviewList
    @Override
    public long getItemId(int position){
        if(0<=position && position<reviewList.size()) return position;
        else return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder viewHolder = new ViewHolder();

        //inflating done only once
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.review_list, parent, false);

            viewHolder.textTitle = (TextView)convertView.findViewById(R.id.reviewTitle);
            viewHolder.textCont = (TextView)convertView.findViewById(R.id.reviewScore);

            convertView.setTag(viewHolder);
        }

        else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        //List each item on reviewList
        ReviewItem item = reviewList.get(position);

        viewHolder.textTitle.setText(item.content);
        viewHolder.textCont.setText(item.date);

        return convertView;
    }
}
