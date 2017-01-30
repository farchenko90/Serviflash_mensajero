package fabio.org.serviflash_mensajero.Util;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import fabio.org.serviflash_mensajero.Modelos.Empleado;
import fabio.org.serviflash_mensajero.Services.Webservices;
import fabio.org.serviflash_mensajero.mensajero_mapa;

/**
 * Created by root on 30/07/16.
 */
public class GPService extends Service implements LocationListener {
    LocationManager locationManager;
    String provider;
    int TIEMPO_GPS = 20000;
    long TIEMPO_ESPERA = 5000;
    public static General gn;

    public static LatLng posicion = null;
    public static Double lat,lng;
    public static boolean ban = false;
    public static Activity context;

    public GPService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        gn = new  General(context,null);

    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(this);
        }
        System.out.println("Servicio Finalizado");
        return;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            System.out.println("Servicio iniciado");
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            provider = locationManager.getBestProvider(criteria, false);
            if (provider != null && !provider.equals("")) {
                boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (isGPSEnabled) {
                    System.out.println("SERVICIO POR GPS");

                    //gn.initCargando("Obteniendo Posicion Global");

                    initHilo();
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TIEMPO_GPS, 0, this);
                } else {
                    boolean isREDEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                    if (isREDEnabled) {
                        System.out.println("SERVICIO POR RED");
                        //gn.initCargando("Obteniendo Posicion Global");
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, TIEMPO_GPS, 0, this);
                    } else {
                        if(ban){
                            mostarAlertDeConfiguracion();
                        }
                        onDestroy();
                    }
                }
            }
        }else{
            System.out.println("NO TIENES PERMISO PARA ACCEDER TU UBICACION");
        }
        return Service.START_NOT_STICKY;
    }

    public void mostarAlertDeConfiguracion() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Configuración del GPS");
        alertDialog.setMessage("Debes activar el GPS para continuar.");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Configurar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });
        AlertDialog al = alertDialog.create();
        al.show();
    }


    private void initHilo(){
        new Thread(new Runnable() {
            @Override
            public void run() {
            try{
                Thread.sleep(TIEMPO_ESPERA);
            }catch(Exception e){}
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    boolean isREDEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                    if (isREDEnabled) {
                        if (ActivityCompat.checkSelfPermission(GPService.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(GPService.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            locationManager.removeUpdates(GPService.this);
                            System.out.println("SE QUITO POR GPS");
                        }
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, TIEMPO_GPS, 0, GPService.this);
                    }
                }
            });
            }
        }).start();
    }

    boolean banDireccion = true;
    private void obtenerDireccion(LatLng pos){
        if(banDireccion){
            //banDireccion = false;
            Geocoder geoCoder = new Geocoder(context, Locale.getDefault());
            try {
                List<Address> addresses = geoCoder.getFromLocation(pos.latitude,  pos.longitude, 1);
                //System.out.println("GECODER RESULTADO: "+addresses.get(0).getAddressLine(1).split(",")[0]);
                //String address = addresses.get(0).getLocality();
                String address = addresses.get(0).getAddressLine(0).split(",")[0];
                //System.out.println("UBICACION Y DIRECCION: "+address);

            } catch (IOException e) {
                System.out.println("GEOCODER: "+e.getMessage());
            }
        }

    }

    boolean banZoom = true;
    @Override
    public void onLocationChanged(Location location) {
        if(location != null){
            //gn.finishCargando();
            posicion = new LatLng(location.getLatitude(),location.getLongitude());
            lat = location.getLatitude();
            lng = location.getLongitude();
            System.out.println("POSICION: "+location.toString());
            actualizarpos();
            //General.establecerUbicacion(banZoom);
            banZoom = false;
        }
    }

    public void actualizarpos(){
        Calendar calendario = Calendar.getInstance();
        int hora, minutos, segundos;
        hora =calendario.get(Calendar.HOUR_OF_DAY);
        minutos = calendario.get(Calendar.MINUTE);
        segundos = calendario.get(Calendar.SECOND);
        final String horaactual = hora+":"+minutos+":"+segundos;
        //System.out.println("hora actual: "+horaactual);
        new Thread(new Runnable() {
            @Override
            public void run() {
            final Webservices cs = new Webservices();
            final JSONObject j = cs.Actualizarposicion(gn.getIdCliente(),String.valueOf(lat),String.valueOf(lng),horaactual);
            }
        }).start();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
