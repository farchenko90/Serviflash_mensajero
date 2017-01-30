package fabio.org.serviflash_mensajero;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import fabio.org.serviflash_mensajero.Dialogos.pagodialogo;
import fabio.org.serviflash_mensajero.Modelos.Empleado;
import fabio.org.serviflash_mensajero.Services.Webservices;
import fabio.org.serviflash_mensajero.Util.GPService;
import fabio.org.serviflash_mensajero.Util.General;
import fabio.org.serviflash_mensajero.Util.ImagenesListView.ImageLoader;
import fabio.org.serviflash_mensajero.Util.Notificaciones.initNotificacion;
import layout.Lista_pedidos;
import layout.pedidosaceptados;

import static fabio.org.serviflash_mensajero.Util.General.contexto;

public class menu_inicial extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    General gn;
    FragmentManager fm;
    FragmentTransaction ft;
    pedidosaceptados paceptados;
    Lista_pedidos listapedidos;
    String estado;
    Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_inicial);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View navHeaderView= navigationView.getHeaderView(0);
        gn = new General(this,navHeaderView);
        this.context = this;

        try{
            GPService.context = this;
            GPService.ban = true;
            GPService.context = this;
            GPService.gn = gn;
            Intent intent = new Intent(getBaseContext(), GPService.class);
            startService(intent);
        }catch (Exception ex){
            //System.out.println(ex.getMessage());
        }

        new initNotificacion(this).initPush();
        //notificacion();
        estadopago();
        cerraroabrirsesion("ACTIVO");
        fotomensajero(this);
        aceptar();
        dialogo();
        init_frgPedidos();
        notificacion();



    }

    public void notificacion()
    {
        Intent std = getIntent();
        Bundle bundle = std.getExtras();
        if(bundle != null){
            int mensaje = bundle.getInt("push");
            if(mensaje == 1){
                Lista_pedidos solicitados = new Lista_pedidos();
                getSupportFragmentManager().beginTransaction().replace(R.id.fgl,solicitados).commit();
                String texto = getResources().getString(R.string.pedido);
                setTitle(texto);
            }
        }
    }

    public void fotomensajero(final Context context){
        new Thread(new Runnable() {
            Webservices wb = new Webservices();
            final JSONObject j = wb.mensajero(gn.getIdCliente());
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(j == null){

                        }else{
                            ImageLoader cargarImagen = new ImageLoader(context);

                            try {
                                System.out.println("imagen perfil: "+j.getString("ruta"));
                                montarImagen(j.getString("ruta"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }).start();
    }
    @Override
    public boolean onKeyDown(int keycode, KeyEvent event){
        if(keycode == KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0){
            //event.startTracking();
            return true;
        }
        return super.onKeyDown(keycode,event);
    }

    private void montarImagen(final String ruta){
        new Thread(new Runnable() {
            @Override
            public void run() {
            URL newurl = null;
            Bitmap mIcon_val = null;
            try {
                newurl = new URL(ruta);
                mIcon_val = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
                Bitmap.createScaledBitmap(mIcon_val,500,500,false);
                System.out.println("SE VA A DESCARGAR LA FOTO");
            } catch (IOException e) {
                System.out.println("ERROR EN FOTO "+e.getMessage());
                e.printStackTrace();
            }
            final Bitmap ftax = mIcon_val;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final ImageView img = ((ImageView)findViewById(R.id.imgperfil));
                    Drawable drawable = new BitmapDrawable(ftax);
                    img.setImageDrawable(drawable);
                    System.out.println(ruta + " SE DESCARGO LA IMAGEN");
                }
            });
            }
        }).start();
    }

    public void onDestroy(){
        super.onDestroy();
    }

        public void cerraroabrirsesion(final String estado){
            final Empleado c = new Empleado();
            new Thread(new Runnable() {
                @Override
                public void run() {

                Webservices web = new Webservices();
                final JSONObject j = web.estadoempleado(estado,gn.getIdCliente());
                runOnUiThread(new Runnable() {
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


        public void estadopago(){
            final Empleado c = new Empleado();
            new Thread(new Runnable() {
                @Override
                public void run() {

                Webservices web = new Webservices();
                final JSONObject j = web.consultaestado(gn.getIdCliente());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    try{
                        if (j == null) {

                        }else{
                            estado = j.optString("estadopago");
                            //System.out.println("Estado pago: "+estado);
                            if(estado.equals("DEBE")){
                                gn.quitarCuenta();
                                Intent i = new Intent(getApplicationContext(),Login.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                System.out.println("Estado pago: "+estado);
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

        public void onResume(){
            super.onResume();
            aceptar();
            dialogo();
        }

        public void onStart(){
            super.onStart();
            try{
                GPService.context = this;
                GPService.ban = true;
                GPService.context = this;
                GPService.gn = gn;
                Intent intent = new Intent(getBaseContext(), GPService.class);
                startService(intent);
            }catch (Exception ex){

            }
            estadopago();
            //notificacion();
            aceptar();

        }

        public void aceptar(){
            Intent intentaceptados = getIntent();
            Bundle bundle = intentaceptados.getExtras();
            if(bundle != null){
                int aceptados = bundle.getInt("aceptado");
                if(aceptados == 1){
                    pedidosaceptados aceptado = new pedidosaceptados();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fgl,aceptado).commit();
                    String texto = getResources().getString(R.string.pedido);
                    setTitle(texto);
                }

            }
        }

        public void dialogo(){
            Intent intentaceptados = getIntent();
            Bundle bundle = intentaceptados.getExtras();
            if(bundle != null){
                int dialog = bundle.getInt("dialog");
                if(dialog == 1){
                    pedidosaceptados aceptado = new pedidosaceptados();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fgl,aceptado).commit();
                    String texto = getResources().getString(R.string.pedido);
                    setTitle(texto);
                    pagodialogo pago = new pagodialogo();
                    pago.isHidden();
                }

            }
        }

        private void init_frgPedidos(){
            fm = getSupportFragmentManager();
            listapedidos = new Lista_pedidos();
            ft = fm.beginTransaction();
            ft.replace(R.id.fgl, listapedidos);
            ft.commit();
            String texto = getResources().getString(R.string.lista);
            setTitle(texto);
        }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inicial, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Lista_pedidos lista = new Lista_pedidos();
            getSupportFragmentManager().beginTransaction().replace(R.id.fgl,lista).commit();
            String texto = getResources().getString(R.string.lista);
            setTitle(texto);
        } else if (id == R.id.nav_gallery) {
            pedidosaceptados aceptado = new pedidosaceptados();
            getSupportFragmentManager().beginTransaction().replace(R.id.fgl,aceptado).commit();
            String texto = getResources().getString(R.string.pedido);
            setTitle(texto);
        }else if(id == R.id.nav_disponible){
            //Toast.makeText(this,"Entro",Toast.LENGTH_LONG).show();
            AlertDialog.Builder c = new AlertDialog.Builder(this);
            c.setTitle("serviflash");
            c.setMessage("Aceder a tu posicion actual y disponibilidad");
            c.setCancelable(false);
            c.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try{
                        GPService.context = context;
                        GPService.ban = true;
                        GPService.context = context;
                        GPService.gn = gn;
                        Intent intent = new Intent(getBaseContext(), GPService.class);
                        startService(intent);
                    }catch (Exception ex){
                        //System.out.println(ex.getMessage());
                    }
                    dialog.cancel();
                }
            });
            c.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog al = c.create();
            al.show();
        }else if (id == R.id.nav_share) {
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setTitle("serviflash");
            b.setMessage("Quieres cerrar la sesión ?");
            b.setCancelable(false);
            b.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    gn.quitarCuenta();
                    dialog.cancel();
                    cerraroabrirsesion("INACTIVO");
                    //LoginManager.getInstance().logOut();
                    Intent i = new Intent(getApplicationContext(),Login.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);


                }
            });
            b.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog al = b.create();
            al.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
