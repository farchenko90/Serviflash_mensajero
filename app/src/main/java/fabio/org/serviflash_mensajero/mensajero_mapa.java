package fabio.org.serviflash_mensajero;

import android.*;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.Calendar;

import fabio.org.serviflash_mensajero.Dialogos.pagodialogo;
import fabio.org.serviflash_mensajero.Services.Webservices;
import fabio.org.serviflash_mensajero.Util.GPService;
import fabio.org.serviflash_mensajero.Util.General;

public class mensajero_mapa extends AppCompatActivity implements OnMapReadyCallback {

    TextView text,text2,text3,text4;
    int id;
    General gn;
    public Marker origen,destino,marketmensajero;
    double origenlat,origenlon;
    double destinolat,destinolon;
    double latmensajero,lonmensajero;
    public LatLng posicionorigen,posiciondestino,posicionmensajero;
    public GoogleMap map;
    boolean ban = true;
    Marker marker;
    //public Activity myActivity;
    LatLng cali;
    Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensajero_mapa);
        this.context = this;
        text = (TextView) findViewById(R.id.texto);
        text2 = (TextView) findViewById(R.id.texto2);
        text3 = (TextView) findViewById(R.id.texto3);
        text4 = (TextView) findViewById(R.id.texto4);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
        gn = new General(this,null);


        if(bundle != null){
            String datos = bundle.getString("Dato");
            id = Integer.parseInt(datos);
            //text.setText("Id Pedido: "+datos);
        }

        /*try{
            GPService.context = this;
            GPService.ban = true;
            GPService.context = this;
            GPService.gn = gn;
            Intent intent1 = new Intent(getBaseContext(), GPService.class);
            startService(intent1);
        }catch (Exception ex){
            //System.out.println(ex.getMessage());
        }*/

    }

    public void onResume(){
        super.onResume();
        if (checkPermission(android.Manifest.permission.ACCESS_FINE_LOCATION,this.getApplicationContext(),this)) {
            posicionactual();
        }
        else
        {
            requestPermission(android.Manifest.permission.ACCESS_FINE_LOCATION,1,this.getApplicationContext(),this);
        }
    }

    public static boolean checkPermission(String strPermission,Context _c,Activity _a){
        int result = ContextCompat.checkSelfPermission(_c, strPermission);
        if (result == PackageManager.PERMISSION_GRANTED){

            return true;

        } else {

            return false;

        }
    }

    public void onDestroy(){
        super.onDestroy();
        ban = false;
    }

    public void requestPermission(String strPermission, int perCode, Context _c, Activity _a){

        if (ActivityCompat.shouldShowRequestPermissionRationale(_a,strPermission)){
            Toast.makeText(mensajero_mapa.this,"No tienes permiso de GPS",Toast.LENGTH_LONG).show();
        } else {

            ActivityCompat.requestPermissions(_a,new String[]{strPermission},perCode);
        }
    }

    public void posicionactual(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    while (GPService.posicion == null){
                        Thread.sleep(500);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(marketmensajero != null){
                                marketmensajero.remove();
                            }
                            LatLng pos = GPService.posicion;
                            System.out.println("mi pos: "+GPService.posicion);
                            if(pos != null){
                                update_pos(String.valueOf(GPService.lat),String.valueOf(GPService.lng));
                                //System.out.println("mi pos: "+pos);
                                //gn.finishCargando();
                                if(map != null){
                                    //System.out.println("mi posicion actual: "+pos);
                                    marketmensajero = map.addMarker(new MarkerOptions()
                                            .position(pos)
                                            .title("Mi posicion actual")
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.motogps)));
                                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 13.0f));
                                }else{
                                    System.out.println("Error posicion perdida");
                                }
                                //gn.finishCargando();
                            }
                        }
                    });
                }catch (Exception ex){
                    System.out.println(ex.getMessage());
                }
            }
        }).start();
    }

    public void entregado(View v){
        android.app.FragmentManager fragmentManager = getFragmentManager();
        new pagodialogo();
        new pagodialogo(id,context).show(fragmentManager,"LoginDialog");
    }

    public void update_pos(final String lat,final String lng){
        Calendar calendario = Calendar.getInstance();
        int hora, minutos, segundos;
        hora =calendario.get(Calendar.HOUR_OF_DAY);
        minutos = calendario.get(Calendar.MINUTE);
        segundos = calendario.get(Calendar.SECOND);
        final String horaactual = hora+":"+minutos+":"+segundos;
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Webservices cs = new Webservices();
                final JSONObject j = cs.Actualizarposicion(gn.getIdCliente(),lat,lng,horaactual);

            }
        }).start();
    }

    private void cargarPedidos(final int idpedido){
        //System.out.println("id "+idpedido);

        new Thread(new Runnable() {
            @Override
            public void run() {

            final Webservices cs = new Webservices();
            final JSONObject j = cs.ubicacionmapa(idpedido);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                try {
                    if (j == null) {
                        Toast.makeText(mensajero_mapa.this, "No hay pedidos realizados", Toast.LENGTH_SHORT).show();
                    } else {
                        text.setText("NOMBRE: "+j.getString("nombreape"));
                        text2.setText("DIR. ORIGEN: "+j.getString("direccionorigen")+" "+j.getString("barrioorigen"));
                        text3.setText("DIR. DESTINO: "+j.getString("direcciondestino")+" "+j.getString("barriodestino"));
                        text4.setText("DESCRIPCION: "+j.getString("descripcion"));
                        origenlat = Double.parseDouble(j.getString("origenlat"));
                        origenlon = Double.parseDouble(j.getString("origenlong"));
                        destinolat = Double.parseDouble(j.getString("destinolat"));
                        destinolon = Double.parseDouble(j.getString("destinolong"));
                        posicionorigen = new LatLng(origenlat,origenlon);
                        posiciondestino = new LatLng(destinolat,destinolon);
                        System.out.println("posicion "+posicionorigen);

                        mostrarposicion();
                    }

                } catch (Exception ex) {
                    Toast.makeText(mensajero_mapa.this, "Error webSerice Principal, " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
                }
            });
            }
        }).start();
    }

    public void mostrarposicion(){

        origen = map.addMarker(new MarkerOptions()
                .position(posicionorigen)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_action_room))
                .title("Origen del pedido"));
        CameraPosition cameraPosition = CameraPosition.builder()
                .target(posicionorigen)
                .zoom(13)
                .build();
        map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        destino = map.addMarker(new MarkerOptions()
                .position(posiciondestino)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_1465164822_pin))
                .title("Destino del pedido"));
        CameraPosition cameradestino = CameraPosition.builder()
                .target(posiciondestino)
                .zoom(13)
                .build();
        map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        this.map.getUiSettings().setZoomControlsEnabled(true);
        this.map.getUiSettings().setCompassEnabled(true);
        this.map.getUiSettings().setMapToolbarEnabled(true);
        this.map.setPadding(0,(int)gn.SpToPx(170),0,(int)gn.SpToPx(60));
        cargarPedidos(id);
        //posicionmensajero();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    while (ban){
                        Thread.sleep(10000);
                        //posicionmensajero();
                    }
                }catch (Exception ex){
                    System.out.println(ex.getMessage());
                }
            }
        }).start();
    }
}
