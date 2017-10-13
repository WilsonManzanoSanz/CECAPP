package com.example.administrador.myapplication;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Administrador on 21/04/2017.
 */

public class IntentsFragment extends Fragment implements View.OnClickListener {

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, Bundle savedInstanceState) {

        View result=inflater.inflate(R.layout.intents_layout,container,false);
        ((Button)result.findViewById(R.id.intents_fragment_start_service_button)).
                setOnClickListener(this);
        ((Button)result.findViewById(R.id.intents_fragment_stop_service_button)).
                setOnClickListener(this);
        ((Button)result.findViewById(R.id.send_this_message_to_service_button)).
                setOnClickListener(this);
        return  result;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.intents_fragment_start_service_button){
            startBackgroundService();

        }
        if(v.getId()==R.id.intents_fragment_stop_service_button){
            stopBackgroundService();

        }

        if(v.getId()==R.id.send_this_message_to_service_button){

            ((MainActivity)getActivity()).
                    sendThisMessageThroughBroadCast(
                            "PlainText",
                            ((EditText)getActivity().
                                    findViewById(R.id.edit_text_for_service_message)).
                                    getText().toString());

        }
    }

    public void startBackgroundService(){
        try{
            Intent intentToCallTheService=
                    new Intent("co.edu.uninorte.cec.mobile.cecservice.core.CECService");
            intentToCallTheService.
                    setPackage("co.edu.uninorte.cec.mobile.cecservice");

            getActivity().startService(intentToCallTheService);
        }catch (Exception error){
            Log.e("IntentsFragment","startBackgroundService: "+error.toString());
        }
    }

    public void stopBackgroundService(){
        try{
            Intent intentToCallTheService=
                    new Intent("co.edu.uninorte.cec.mobile.cecservice.core.CECService");
            intentToCallTheService.
                    setPackage("co.edu.uninorte.cec.mobile.cecservice");
            getActivity().stopService(intentToCallTheService);
        }catch (Exception error){
            Log.e("IntentsFragment","stopBackgroundService: "+error.toString());
        }
    }
}
