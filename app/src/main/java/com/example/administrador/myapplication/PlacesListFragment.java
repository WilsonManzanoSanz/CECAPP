package com.example.administrador.myapplication;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by Administrador on 21/04/2017.
 */

public class PlacesListFragment extends Fragment {

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, Bundle savedInstanceState) {

        ((MainActivity)getActivity()).getAllPlacesStoredInDataBase();

        View result=inflater.inflate(R.layout.places_list_layout,container,false);
        ListView listView= (ListView) result.findViewById(R.id.places_list_list_view);
        CustomListAdapter customListAdapter=new CustomListAdapter(this.getActivity(),
                ((MainActivity)getActivity()).getVectorOfPlaces());
        listView.setAdapter(customListAdapter);
        return  result;
    }

    public void updateList(){
        ListView listView= (ListView) getView().findViewById(R.id.places_list_list_view);
        CustomListAdapter customListAdapter=new CustomListAdapter(this.getActivity(),
                ((MainActivity)getActivity()).getVectorOfPlaces());
        listView.setAdapter(customListAdapter);
    }
}
