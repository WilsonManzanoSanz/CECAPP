package com.example.administrador.myapplication;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.Vector;

/**
 * Created by Administrador on 22/04/2017.
 */

public class CustomListAdapter extends BaseAdapter {

    Activity activity;
    Vector<Place> placesVector;

    public CustomListAdapter(Activity activity,Vector<Place> placesVector){

        this.activity=activity;
        this.placesVector=placesVector;
    }

    @Override
    public int getCount() {
        return this.placesVector.size();
    }

    @Override
    public Object getItem(int position) {
        return placesVector.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater=activity.getLayoutInflater();
        Place currentPlace=this.placesVector.get(position);
        View currentListItem=inflater.inflate(R.layout.custom_list_item,null,true);
        if(position%2==0) {
            currentListItem.setBackground(currentListItem.getResources().getDrawable(R.color.colorPrimaryDark));
        }
        ((ImageView)currentListItem.
                findViewById(R.id.custom_item_image_view)).
                setImageBitmap(currentPlace.getPlaceImage());
        ((TextView)currentListItem.
                findViewById(R.id.custom_item_place_name)).
                setText(currentPlace.getPlaceName());
        ((TextView)currentListItem.
                findViewById(R.id.custom_item_place_description)).
                setText(currentPlace.getPlaceDescription());
        ((RatingBar)currentListItem.
                findViewById(R.id.custom_item_rating_bar)).
                setRating(currentPlace.getStarts());

        return currentListItem;
    }
}
