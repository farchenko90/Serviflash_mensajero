package fabio.org.serviflash_mensajero.Util;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.MarkerOptions;

import fabio.org.serviflash_mensajero.mensajero_mapa;

/**
 * Created by root on 6/09/16.
 */
public class InitServicio extends Service{

    int TIEMPO_GPS = 5000;
    public static General gn;
    mensajero_mapa mapa;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId){
        try {
            while (mapa.posicionorigen == null){
                Thread.sleep(500);
                gn.initCargando("iniciando");
            }
            gn.finishCargando();
            crearmarkets();
        }catch (Exception Ex){
            System.out.println("ERROR: " + Ex.getMessage());
        }
        return Service.START_NOT_STICKY;
    }

    public void crearmarkets(){
        mapa.map.addMarker(new MarkerOptions()
                    .position(mapa.posicionorigen)
                    .title("Origen Destino"));
        CameraPosition cameraPosition = CameraPosition.builder()
                .target(mapa.posicionorigen)
                .zoom(13)
                .build();
        mapa.map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

}
