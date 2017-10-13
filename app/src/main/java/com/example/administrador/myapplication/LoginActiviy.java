package com.example.administrador.myapplication;


import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Administrador on 01/04/2017.
 */

public class LoginActiviy extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_login);
        ((Button)findViewById(R.id.login_login)).setOnClickListener(this);
        if(!isMyServiceRunning("CECService")){
            startBackgroundService();
        }
    }


    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.login_login){
            Intent intentToBeCalled=new Intent(this,MainActivity.class);
            EditText editText= (EditText)findViewById(R.id.user_edit_text);
            intentToBeCalled.putExtra("user",
                    editText.getText());
            intentToBeCalled.putExtra("password",
                    ((EditText)findViewById(R.id.password_edit_text)).getText());

            startActivity(intentToBeCalled);
        }
    }

    public boolean isMyServiceRunning(String serviceName){

        ActivityManager manager=(ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo current:manager.getRunningServices(Integer.MAX_VALUE)){
            if(current.service.getClassName().indexOf(serviceName)!=-1){
                return true;
            }
        }
        return false;
    }

    public void startBackgroundService(){
        try{
            Intent intentToCallTheService=
                    new Intent("co.edu.uninorte.cec.mobile.cecservice.core.CECService");
            intentToCallTheService.
                    setPackage("co.edu.uninorte.cec.mobile.cecservice");

            startService(intentToCallTheService);
        }catch (Exception error){
            Log.e("IntentsFragment","startBackgroundService: "+error.toString());
        }
    }

}
