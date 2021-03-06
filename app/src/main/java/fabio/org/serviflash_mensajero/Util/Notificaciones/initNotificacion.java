package fabio.org.serviflash_mensajero.Util.Notificaciones;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;


import org.json.JSONObject;

import java.io.IOException;

import fabio.org.serviflash_mensajero.Services.Webservices;
import fabio.org.serviflash_mensajero.Util.General;
import fabio.org.serviflash_mensajero.menu_inicial;


/**
 * Created by giocni on 15/07/15.
 */
public class initNotificacion {

    public static final String REG_ID = "regId";
    private static final String APP_VERSION = "appVersion";

    static final String TAG = "Register Activity";

    String regId;
    GoogleCloudMessaging gcm;
    Context context;
    General gn;

    public initNotificacion(Context context){
        this.context = context;
        gn = new General(context,null);
    }

    public void initPush(){
        regId = registerGCM();
        Log.d("RegisterActivity", "GCM RegId: " + regId);
    }

    private String registerGCM() {

        gcm = GoogleCloudMessaging.getInstance(context);
        regId = getRegistrationId(context);

        if (TextUtils.isEmpty(regId)) {

            registerInBackground();

            Log.d("RegisterActivity",
                    "registerGCM - successfully registered with GCM server - regId: "
                            + regId);
        } else {
            Toast.makeText(context,
                    "RegId already available. RegId: " + regId,
                    Toast.LENGTH_LONG).show();
        }
        return regId;
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = this.context.getSharedPreferences(
                menu_inicial.class.getSimpleName(), Context.MODE_PRIVATE);
        String registrationId = prefs.getString(REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.i("RegisterActivity",
                    "I never expected this! Going down, going down!" + e);
            throw new RuntimeException(e);
        }
    }

    private void registerInBackground() {
        new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regId = gcm.register(Config.GOOGLE_PROJECT_ID);
                    Log.i("RegisterActivity", "registerInBackground - regId: "
                            + regId);

                    //ServiciosEmpleado us = new ServiciosEmpleado();
                    //us.update_idpush(gn.getIdEmpleado(),regId);

                    msg = "Device registered, registration ID=" + regId;

                    //webServices s = new webServices(getServer());
                    //JSONObject j = s.registrarIdPush(getIMEI(),regId);
                    Webservices ws = new Webservices();
                    JSONObject j = ws.updatepush(regId,gn.getIdCliente());

                    /*Log.i("REGISTRO EN BD", "estado: "
                            + j.toString());*/

                    //storeRegistrationId(context, regId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    Log.i("RegisterActivity", "Error: " + msg);
                }
                Log.i("RegisterActivity", "AsyncTask completed: " + msg);
                return msg;
            }
        }.execute(null, null, null);
    }

}
