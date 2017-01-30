package fabio.org.serviflash_mensajero.Util.Notificaciones;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import fabio.org.serviflash_mensajero.R;
import fabio.org.serviflash_mensajero.mensajero_mapa;
import fabio.org.serviflash_mensajero.menu_inicial;


/**
 * Created by giocni on 1/2/15.
 */
public class GCMNotificationIntentService extends IntentService {

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    static MediaPlayer player;
    Activity myActivity;

    public GCMNotificationIntentService() {
        super("GcmIntentService");

    }

    LocalBroadcastManager broadcaster;
    public static final String TAG = "GCMNotificationIntentService";

    public boolean actualizarActivity(String idServicio, String idSucursal, String isEmpleado, String servicio, String empleado) {
        Intent broadcastIntent = new Intent("calificar");

        broadcastIntent.putExtra("idServicio",idServicio);
        broadcastIntent.putExtra("idSucursal",idSucursal);
        broadcastIntent.putExtra("idEmpleado",isEmpleado);
        broadcastIntent.putExtra("empleado",empleado);
        broadcastIntent.putExtra("servicio", servicio);

        boolean var = broadcaster.sendBroadcast(broadcastIntent);
        Log.i("ENVIO EN ESTADO: ", String.valueOf(var));
        return var;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            try {
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                sendNotification(extras);
                Log.i(TAG, "Received: " + extras.toString());
            }catch(Exception e){
                Log.i("EXCEPTION NOTIFICACION",e.getMessage());
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }



    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void sendNotification(Bundle msg) {
        System.out.println("Preparing to send notification...: " + msg);

        mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, menu_inicial.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //FragmentManager fragmentManager = getFragmentManager();
        int ban = Integer.parseInt(msg.getString("std"));
        try{
            switch (ban){
                case 1:
                    intent = new Intent(this,menu_inicial.class);
                    intent.putExtra("push",1);
                    //this.startActivity(intent1);
                    //this.finish();
                case 10:

            }
        }catch(Exception ex){
            System.out.println("ERROR NOTIIIII " + ex.getMessage());
        }

        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        builder.setSmallIcon(R.drawable.ic_stat_icono)
                .setContentTitle(msg.getString("title"))
                .setContentInfo(msg.getString("message"))
                .setContentIntent(pIntent);
        Notification notification = builder.getNotification();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.defaults |= Notification.DEFAULT_SOUND;

        mNotificationManager.notify(0, notification);
        System.out.println("Notification sent successfully.");
    }

    private void reproducirSonidoDefault(){
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        MediaPlayer thePlayer = MediaPlayer.create(getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        try {
            thePlayer.setVolume((float) (audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) / 7.0),
                    (float) (audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) / 7.0));
        } catch (Exception e) {
            e.printStackTrace();
        }

        thePlayer.start();
    }

}
