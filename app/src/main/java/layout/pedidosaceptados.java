package layout;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import fabio.org.serviflash_mensajero.Adapter.AdaptadorAceptados;
import fabio.org.serviflash_mensajero.Modelos.Empleado;
import fabio.org.serviflash_mensajero.R;
import fabio.org.serviflash_mensajero.Services.Webservices;
import fabio.org.serviflash_mensajero.Util.General;

/**
 * A simple {@link Fragment} subclass.
 */
public class pedidosaceptados extends Fragment {


    General gn;
    View rootView;
    Activity myActivity;
    ListView listaPedidos;
    ArrayList<JSONObject> listaDatosPedidos;
    AdaptadorAceptados adaptadorPedidos;
    ImageView lblEstado;
    TextView texto;

    public pedidosaceptados() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        myActivity = (AppCompatActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_pedidosaceptados, container, false);
        gn = new General(myActivity,null);
        listaPedidos = (ListView) rootView.findViewById(R.id.listaceptado);
        lblEstado = (ImageView) rootView.findViewById(R.id.lblEstadoaceptado);
        texto = (TextView) rootView.findViewById(R.id.lblmensajeaceptado);
        //cerraroabrirsesion("ACTIVO");
        return rootView;
    }

    public void onResume(){
        super.onResume();
        cargarPedidos();
    }

    @Override
    public void onStart() {
        super.onStart();
        cargarPedidos();

    }

    private void cargarPedidos(){
        new Thread(new Runnable() {
            @Override
            public void run() {
            final Webservices cs = new Webservices();
            final JSONArray j = cs.mispedidosencamino(gn.getIdCliente());
            myActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                try {
                    listaDatosPedidos = new ArrayList<>();
                    if (j == null) {
                        Toast.makeText(myActivity, "No hay pedidos realizados", Toast.LENGTH_SHORT).show();
                    } else {
                        for (int i = 0; i < j.length(); i++) {
                            listaDatosPedidos.add(j.getJSONObject(i));
                        }
                    }
                    adaptadorPedidos = new AdaptadorAceptados(myActivity, listaDatosPedidos);
                    listaPedidos.setAdapter(adaptadorPedidos);
                    if(listaDatosPedidos.size() == 0){
                        lblEstado.setVisibility(View.VISIBLE);
                        texto.setVisibility(View.VISIBLE);
                    }else{
                        lblEstado.setVisibility(View.GONE);
                        texto.setVisibility(View.GONE);
                    }
                } catch (Exception ex) {
                    //Toast.makeText(myActivity, "Error webSerice Principal, " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
                }
            });
            }
        }).start();
    }

    public void onDestroy(){
        super.onDestroy();
        //cerraroabrirsesion("INACTIVO");
        System.out.println("Cerro sesion");
    }

    public void cerraroabrirsesion(final String estado){
        final Empleado c = new Empleado();
        new Thread(new Runnable() {
            @Override
            public void run() {

                Webservices web = new Webservices();
                final JSONObject j = web.estadoempleado(estado,gn.getIdCliente());
                myActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            if (j == null) {
                                System.out.println("InCorrecto");
                            }else{
                                if(j.getInt("std") == 1){
                                    System.out.println("Correcto");
                                }
                            }
                        }catch (Exception ex){
                            System.out.println("Error webSerice login, "+ex.getMessage());
                        }
                    }
                });
            }
        }).start();
    }

}
