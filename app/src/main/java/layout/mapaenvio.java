package layout;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fabio.org.serviflash_mensajero.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class mapaenvio extends Fragment {


    public mapaenvio() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mapaenvio, container, false);
    }



}
