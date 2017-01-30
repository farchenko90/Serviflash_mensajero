package fabio.org.serviflash_mensajero;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import fabio.org.serviflash_mensajero.Modelos.Empleado;
import fabio.org.serviflash_mensajero.Services.Webservices;
import fabio.org.serviflash_mensajero.Util.General;

public class Login extends AppCompatActivity {

    EditText email,pass;
    General gn;
    String estado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initComponent();
        gn = new General(this,null);
        validarSesion();

        //estadopago();

    }



    public void onStart(){
        super.onStart();
    }

    public void initComponent(){
        email = (EditText) findViewById(R.id.email);
        pass = (EditText) findViewById(R.id.password);
    }

    private void validarSesion(){
        if(gn.sesion()){
            Intent i = new Intent(Login.this,menu_inicial.class);
            i.putExtra("ban",0);
            startActivity(i);
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event){
        if(keycode == KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0){
            //event.startTracking();
            return true;
        }
        return super.onKeyDown(keycode,event);
    }

    /*public void estadopago(){
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
                                    System.out.println("Estado pago: "+estado);
                                }else{
                                    validarSesion();
                                }
                            }
                        }catch (Exception ex){
                            System.out.println("Error webSerice login, "+ex.getMessage());
                        }
                    }
                });
            }
        }).start();
    }*/

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

    public boolean validarusuario(){
        if(email.getText().toString().equals("") && pass.getText().toString().equals(""))
            return true;
        else
            return false;
    }

    public void iniciar(View view){
        if(validarusuario() == true){
            Toast.makeText(this,"Los Datos Son Obligatorio",Toast.LENGTH_LONG).show();
        }else{
            final Empleado c = new Empleado();
            c.setLogin(email.getText().toString());
            c.setClave(pass.getText().toString());
            gn.initCargando("Iniciando sesión...");
            new Thread(new Runnable() {
                @Override
                public void run() {
                Webservices cs = new Webservices();
                final JSONObject j = cs.login(c);
                gn.finishCargando();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    try {
                        if (j == null) {
                            Toast.makeText(Login.this, "Error no hay datos o internet", Toast.LENGTH_SHORT).show();
                        } else{
                            if(j.getInt("message") == 3){
                                JSONObject temp = j.getJSONObject("admin");
                                Toast.makeText(Login.this,"Bienvenido "+temp.getString("nombreape"),Toast.LENGTH_SHORT).show();
                                cerraroabrirsesion("ACTIVO");
                                Empleado c = new Empleado();
                                c.setId(temp.getInt("id"));
                                c.setCorreo(temp.getString("correo"));
                                c.setNombreape(temp.getString("nombreape"));
                                c.setRuta(temp.getString("ruta"));
                                c.setPlacamoto(temp.getString("placamoto"));
                                c.setClave(pass.getText().toString());
                                gn.guardarCliente(c,false);
                                Intent i = new Intent(Login.this,menu_inicial.class);
                                startActivity(i);
                                finish();

                            }if(j.getInt("message") == 2){
                                Toast.makeText(Login.this, j.getString("request"), Toast.LENGTH_SHORT).show();
                            }if(j.getInt("message") == 1){
                                Toast.makeText(Login.this, j.getString("request"), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }catch (Exception ex){
                        Toast.makeText(Login.this, "Error webSerice login, "+ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    }
                });

                }
            }).start();
        }
    }
}
