package fabio.org.serviflash_mensajero;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import fabio.org.serviflash_mensajero.Util.GPService;
import fabio.org.serviflash_mensajero.Util.General;

public class splat extends AppCompatActivity {

    ArrayList<String> listaPermisos;
    General gn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splat);
        //View navHeaderView= navigationView.getHeaderView(0);
        gn = new General(this,null);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(2000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Boolean ban = verificarPermisos();
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                if(ban){
                                    pedirPermisos(null);
                                }else{
                                    Intent intent = new Intent(splat.this,Login.class);
                                    startActivity(intent);
                                }
                            }else{
                                Intent intent = new Intent(splat.this,Login.class);
                                startActivity(intent);
                            }
                        }
                    });
                }catch (Exception e){}
            }
        }).start();
    }

    public void pedirPermisos(View v){
        String[] strLista = arrayToStringArray(listaPermisos);
        Toast.makeText(this,String.valueOf(strLista.length),Toast.LENGTH_LONG).show();
        ActivityCompat.requestPermissions(this,strLista,1);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        boolean ban = true;
        for(int i = 0; i < grantResults.length; i++){
            if(grantResults[i] == PackageManager.PERMISSION_DENIED){
                ban = false;
            }
        }
        if(ban){
            Intent intent = new Intent(this,Login.class);
            startActivity(intent);
        }
    }

    private boolean verificarPermisos(){
        boolean ban = false;
        listaPermisos = new ArrayList<>();
        if ((ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)) {
            listaPermisos.add(Manifest.permission.READ_PHONE_STATE);
            ban = true;
        }
        if ((ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            listaPermisos.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            ban = true;

        }
        if ((ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            listaPermisos.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            ban = true;
        }
        if ((ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            listaPermisos.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
            ban = true;
        }
        return ban;
    }

    private String[] arrayToStringArray(ArrayList<String> listaPermisos){
        String[] listaNueva = new String[listaPermisos.size()];
        for(int i = 0; i < listaPermisos.size(); i ++){
            listaNueva[i] = listaPermisos.get(i);
        }
        return listaNueva;
    }

}
