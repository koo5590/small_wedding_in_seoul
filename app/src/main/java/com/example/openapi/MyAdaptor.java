package com.example.openapi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/****** Adaptor for ListView in DetailActivity *********/
public class MyAdaptor extends BaseAdapter{

    private ArrayList<ListItem> itemList;
    private LayoutInflater layoutInflater;

    //Constructor
    public MyAdaptor(ArrayList<ListItem> itemList, Context context){
        this.itemList = itemList;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //set itemList and notify that the data has changed
    private void setItemList(ArrayList<ListItem> itemList){
        this.itemList = itemList;
        this.notifyDataSetChanged();
    }

    //return # of items in itemList
    @Override
    public int getCount(){
        return itemList.size();
    }

    //return item at *position* in itemList
    @Override
    public Object getItem(int position){
        if(0<=position && position<itemList.size()) return itemList.get(position);
        else return null;
    }

    //return the index of the item in itemList
    @Override
    public long getItemId(int position){
        if(0<=position && position<itemList.size()) return position;
        else return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder viewHolder = new ViewHolder();

        //inflating done only once
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.list_item, parent, false);

            viewHolder.textTitle = (TextView)convertView.findViewById(R.id.textName);
            viewHolder.textCont = (TextView)convertView.findViewById(R.id.textContent);

            convertView.setTag(viewHolder);
        }

        else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        //List each item on listView
        ListItem item = itemList.get(position);

        viewHolder.textTitle.setText(item.title);
        viewHolder.textCont.setText(item.content);

        return convertView;
    }
}
