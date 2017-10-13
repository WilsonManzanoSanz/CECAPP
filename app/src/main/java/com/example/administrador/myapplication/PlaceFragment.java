package com.example.administrador.myapplication;


import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.administrador.myapplication.shared.objects.InsertSQLRequest;

import java.io.File;

/**
 * Created by Administrador on 21/04/2017.
 */

public class PlaceFragment extends Fragment implements View.OnClickListener {

    static int CAMERA_REQUEST_CODE=101;
    private ImageView imageView;
    String lastFileName;
    Uri lastPhotoURI;

    private static final String AUTHORITY= BuildConfig.APPLICATION_ID+".provider";
    private String lastPhotoTakenPath;

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, Bundle savedInstanceState) {

        View result=inflater.inflate(R.layout.place_fragment_layout,container,false);
        ((ImageButton)result.
                findViewById(R.id.place_fragment_camera_button)).setOnClickListener(this);
        ((Button)result.
                findViewById(R.id.place_fragment_create_place)).setOnClickListener(this);
        return  result;
    }


    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.place_fragment_camera_button){
            takeCameraShot();
        }

        if(v.getId()==R.id.place_fragment_create_place){
            createPlace();
        }
    }

    private void createPlace() {
        try{
            if(imageView!=null){
                RatingBar ratingBar=(RatingBar)this.getActivity().findViewById(R.id.place_fragment_rating_bar);
                Place newPlace=new Place(
                        ((BitmapDrawable)this.imageView.getDrawable()).getBitmap(),
                        ((EditText)getActivity().
                                findViewById(R.id.edit_text_place_name)).getText().toString(),
                        ((EditText)getActivity().
                                findViewById(R.id.edit_text_place_description)).getText().toString(),
                        ratingBar.getRating());
                newPlace.setPlaceImagePath(lastPhotoTakenPath);


                /*
                "places(" +
                        "place_code integer primary key autoincrement," +
                        "place_name text," +
                        "place_description text," +
                        "place_rating text," +
                        "place_longitude text," +
                        "place_latitude text," +
                        "place_image_uri text" +*/

                InsertSQLRequest request=new InsertSQLRequest();
                request.userState="database/insert/place";
                request.tableName="places";
                ContentValues contentValues=new ContentValues();
                contentValues.put("place_name",newPlace.getPlaceName());
                contentValues.put("place_description",newPlace.getPlaceDescription());
                contentValues.put("place_rating",newPlace.getStarts()+"");
                contentValues.put("place_image_uri",newPlace.getPlaceImagePath());
                request.contentValues=contentValues;
                ((MainActivity)getActivity()).broadCastManager.sendBroadCastMessage("InsertSQLRequest",
                        ((MainActivity)getActivity()).getGson().toJson(request));



                ((MainActivity)getActivity()).addThisPlaceToTheList(newPlace);

                ratingBar.setRating(0);
                imageView.setImageBitmap(null);
                ((EditText)getActivity().
                        findViewById(R.id.edit_text_place_name)).setText("");
                ((EditText)getActivity().
                        findViewById(R.id.edit_text_place_description)).setText("");
                Toast.makeText(this.getContext(),"Nuevo lugar creado",Toast.LENGTH_SHORT).show();

            }

        }catch(Exception error){
            Log.e("PlaceFragment","Exception: "+error.getMessage());
        }
    }

    public void takeCameraShot(){
        try{
            lastFileName="place_image_"+java.util.Calendar.getInstance().getTime().getTime()+".jpg";
            File photoFile=new File(new File(this.getActivity().getFilesDir(),"photos"),lastFileName);
            if(photoFile.exists()){
                photoFile.delete();
            }else{
                photoFile.getParentFile().mkdir();
            }
            lastPhotoURI= FileProvider.getUriForFile(this.getActivity(),AUTHORITY,photoFile);
            Intent intentToCallTheCamera=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intentToCallTheCamera.putExtra(MediaStore.EXTRA_OUTPUT,lastPhotoURI);
            if(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP){
                intentToCallTheCamera.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            lastPhotoTakenPath=Uri.fromFile(photoFile).getPath();
            if(intentToCallTheCamera.resolveActivity(getActivity().getPackageManager())!=null){
                startActivityForResult(intentToCallTheCamera,CAMERA_REQUEST_CODE);
            }else{
                Toast.makeText(this.getContext(),"Please verify your camera application",Toast.LENGTH_SHORT).show();
            }


        }catch (Exception error){

        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if((requestCode==this.CAMERA_REQUEST_CODE)&&(resultCode==getActivity().RESULT_OK)){
            File photoFile=new File(new File(this.getActivity().getFilesDir(),"photos"),lastFileName);
            Bitmap image= BitmapFactory.decodeFile(photoFile.getAbsolutePath());

            imageView=(ImageView)getActivity().findViewById(R.id.imageView);
            imageView.setImageBitmap(image);


        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
