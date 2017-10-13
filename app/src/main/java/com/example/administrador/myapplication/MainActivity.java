package com.example.administrador.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrador.myapplication.shared.objects.DownloadRequest;
import com.example.administrador.myapplication.shared.objects.GPSPosition;
import com.example.administrador.myapplication.shared.objects.InsertSQLRequest;
import com.example.administrador.myapplication.shared.objects.LoginRequest;
import com.example.administrador.myapplication.shared.objects.QuerySQLRequest;
import com.example.administrador.myapplication.shared.objects.WebServiceCallRequest;
import com.google.gson.Gson;

import java.io.File;
import java.util.Date;
import java.util.Vector;

import broadcasting.BroadCastManager;
import broadcasting.BroadCastManagerCallerInterface;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        BroadCastManagerCallerInterface {


    BroadCastManager broadCastManager;
    public String incommingBroadCastString="com.example.administrador.myapplication.in";
    public String outgoingBroadCastString="co.edu.uninorte.cec.mobile.cecservice.core.in";

    /*
    this vector holds the places list for all the fragments
     */
    public Vector<Place> vectorOfPlaces;
    private Gson gsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabs_main_layout);
        FragmentTabHost tabHost= (FragmentTabHost) findViewById(R.id.tabs_main_layout);
        tabHost.setup(this,getSupportFragmentManager(),android.R.id.tabcontent);
        tabHost.addTab(tabHost.newTabSpec("tab1")
                .setIndicator("",getResources().getDrawable(android.R.drawable.ic_menu_edit))
                ,PlaceFragment.class,
                null);
        tabHost.addTab(tabHost.newTabSpec("tab2")
                        .setIndicator("",getResources().getDrawable(android.R.drawable.ic_dialog_map))
                ,PlacesListFragment.class,
                null);
        tabHost.addTab(tabHost.newTabSpec("tab3")
                        .setIndicator("",getResources().getDrawable(android.R.drawable.ic_dialog_info))
                ,IntentsFragment.class,
                null);
        initializeBroadCast();
        userLoginVerify();

    }

    public void initializeBroadCast(){
        this.broadCastManager=new
                BroadCastManager(this,incommingBroadCastString,
                outgoingBroadCastString,this);
        broadCastManager.registerBroadCastReceiver();
    }

    @Override
    public void onClick(View v) {



    }

    public Vector<Place> getVectorOfPlaces() {
        if(vectorOfPlaces==null){
            this.vectorOfPlaces=new Vector<Place>();
        }
        return vectorOfPlaces;
    }

    public void addThisPlaceToTheList(Place currentPlace){
        this.getVectorOfPlaces().add(currentPlace);
    }

    @Override
    public void intentHasBeenReceivedThroughTheBroadCast(Intent intent) {
        String messageType=intent.getExtras().getString("MessageType");
        String payload=intent.getExtras().getString("MessagePayload");
        if(messageType.equals("GPSPosition")){
            GPSPosition gpsPosition=this.getGson().fromJson(payload,GPSPosition.class);
            ((TextView)findViewById(R.id.intents_fragment_longitude_text_view)).setText("Longitud: "+gpsPosition.longitude+"");
            ((TextView)findViewById(R.id.intents_fragment_latitude_text_view)).setText("Latitude: "+gpsPosition.latitude+"");
            ((TextView)findViewById(R.id.intents_fragment_date_text_view)).setText("Date: "+gpsPosition.dateTime+"");
        }

        if(messageType.equals("WebServiceCallRequest")){
            WebServiceCallRequest response=this.getGson().fromJson(payload,WebServiceCallRequest.class);
            if(response!=null){
                if(response.userState.equals("user/login")) {
                    if (response.successfully) {
                        LoginRequest loginResponse =
                                gsonObject.fromJson(response.result, LoginRequest.class);
                        if(loginResponse.successfully){
                            Toast.makeText(
                                    this, "Login exitoso!", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(
                                    this, "Autenticación fallida", Toast.LENGTH_LONG).show();
                            finish();
                        }

                    } else {
                        Toast.makeText(
                                this, "IO Error: " + response.result + " - response code: " + response.responseCode, Toast.LENGTH_LONG).show();
                    }
                }
            }
        }

        if(messageType.equals("DownloadRequest")){
            DownloadRequest response=this.getGson().fromJson(payload,DownloadRequest.class);
            if(response!=null){
                if(response.successfully){
                    try {
                        String filePath = Environment.getExternalStorageDirectory() + response.downloadedFilePath;
                        //String filePath = Environment.getExternalStorageDirectory() + response.downloadedFilePath;
                        File file = new File(filePath);
                        Intent intentToBeCalled = new Intent(Intent.ACTION_VIEW);
                        intentToBeCalled.setDataAndType(Uri.fromFile(file), "image/*");
                        startActivity(intentToBeCalled);
                    }catch (Exception error){
                        Log.e("MainActivity","intentHasBeenReceivedThroughTheBroadCast: "+error.toString());
                    }
                }
            }
        }

        if(messageType.equals("QuerySQLRequest")){
            QuerySQLRequest response=getGson().fromJson(payload,QuerySQLRequest.class);
            if(response!=null){
                if(response.userState.equals("place/get/all")){
                    if(response.successfullY){
                        Vector<Object[]> result = response.result;
                        if (result.size() > 1) {
                            for(int index=1;index<result.size();index++){
                                Place newPlace=new Place();
                                newPlace.setPlaceName((String)result.get(index)[0]);
                                newPlace.setPlaceDescription((String)result.get(index)[1]);
                                newPlace.setStarts(Float.parseFloat((String)result.get(index)[2]));
                                newPlace.setPlaceImagePath((String)result.get(index)[3]);
                                File photoFile=new File(newPlace.getPlaceImagePath());
                                Bitmap image= BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                                newPlace.setPlaceImage(image);
                                vectorOfPlaces.add(newPlace);
                            }
                        }
                        android.support.v4.app.Fragment fragment=getSupportFragmentManager().findFragmentByTag("tab2");
                        ((PlacesListFragment)fragment).updateList();
                    }
                }

                if(response.userState.equals("database/user/login")) {
                    if (response.successfullY) {
                        Vector<Object[]> result = response.result;
                        if (result.size() > 1) {
                            Toast.makeText(
                                    this, "Login exitoso!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(
                                    this, "Autenticación fallida", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                }
            }
        }

        if(messageType.equals("InsertSQLRequest")) {
            InsertSQLRequest response = getGson().fromJson(payload, InsertSQLRequest.class);
            if (response!=null){
                if(response.successfully){
                    Toast.makeText(
                            this, "Lugar insertado exitosamente", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(
                            this, response.errorMessage, Toast.LENGTH_LONG).show();
                }

            }
        }

    }

    public void sendThisMessageThroughBroadCast(String messageType,String messagePayload){
        this.broadCastManager.sendBroadCastMessage(messageType,messagePayload);
    }

    public void userLoginVerify(){
        String user=  this.getIntent().getExtras().get("user")+"";
        String password=  this.getIntent().getExtras().get("password")+"";

        QuerySQLRequest querySQLRequest=new QuerySQLRequest();
        querySQLRequest.queryString=" select user_name,user_password " +
                "from users where user_name='"+user+"' and user_password='"+password+"'";
        querySQLRequest.userState="database/user/login";
        this.broadCastManager.sendBroadCastMessage("QuerySQLRequest",getGson().toJson(querySQLRequest));

    }

    public void userLoginVerifyWithWebService(){
        String user=  this.getIntent().getExtras().get("user")+"";
        String password=  this.getIntent().getExtras().get("password")+"";
        LoginRequest loginRequest=new LoginRequest();
        loginRequest.userName=user;
        loginRequest.userPassword=password;
        WebServiceCallRequest webServiceCallRequest=new WebServiceCallRequest();
        webServiceCallRequest.messageToBeSent=getGson().toJson(loginRequest);
        webServiceCallRequest.requestMethod="PUT";
        webServiceCallRequest.contentType="application/json";
        webServiceCallRequest.userState="user/login";
        webServiceCallRequest.urlForTheHttpCall=
                "http://172.17.8.100:8989/CECWebService/webresources/cec/userlogin/";
        this.broadCastManager.sendBroadCastMessage("WebServiceCallRequest",getGson().toJson(webServiceCallRequest));




    }

    public void getAllPlacesStoredInDataBase(){
        QuerySQLRequest querySQLRequest=new QuerySQLRequest();
        querySQLRequest.queryString="select place_name,place_description,place_rating,place_image_uri from places";
        querySQLRequest.userState="place/get/all";
        sendThisMessageThroughBroadCast("QuerySQLRequest",getGson().toJson(querySQLRequest));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.download_button){
            DownloadRequest request=new DownloadRequest();
            request.urlForTheDownload="http://www.columbus-web.org/images/delnorte_col.jpg";
            request.canMobileNetworkBeUsed=true;
            broadCastManager.sendBroadCastMessage("DownloadRequest",getGson().toJson(request));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    public Gson getGson(){
        if(gsonObject==null) {
            gsonObject = new Gson();
        }
        return gsonObject;
    }
}
