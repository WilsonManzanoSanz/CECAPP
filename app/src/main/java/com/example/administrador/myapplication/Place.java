package com.example.administrador.myapplication;

import android.graphics.Bitmap;

/**
 * Created by Administrador on 22/04/2017.
 */

public class Place {

    public Place(){

    }

    public Place(Bitmap image,String name,String description,float starts){
        this.setPlaceImage(image);
        this.setPlaceName(name);
        this.setPlaceDescription(description);
        this.setStarts(starts);


    }

    public Bitmap getPlaceImage() {
        return placeImage;
    }

    public void setPlaceImage(Bitmap placeImage) {
        this.placeImage = placeImage;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceDescription() {
        return placeDescription;
    }

    public void setPlaceDescription(String placeDescription) {
        this.placeDescription = placeDescription;
    }

    public float getStarts() {
        return starts;
    }

    public void setStarts(float starts) {
        this.starts = starts;
    }

    /*
    These are the variables of the class
     */
    private Bitmap placeImage;
    private String placeName;
    private String placeDescription;
    private float starts;

    public String getPlaceImagePath() {
        return placeImagePath;
    }

    public void setPlaceImagePath(String placeImagePath) {
        this.placeImagePath = placeImagePath;
    }

    private String placeImagePath;

}
