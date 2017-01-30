package fabio.org.serviflash_mensajero.Dialogos;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import fabio.org.serviflash_mensajero.R;
import fabio.org.serviflash_mensajero.Services.Webservices;
import fabio.org.serviflash_mensajero.menu_inicial;

/**
 * Created by root on 25/11/16.
 */

public class pagodialogo extends DialogFragment{

    private static final String TAG = pagodialogo.class.getSimpleName();
    int idpedido;
    Activity context;
    EditText pago,cliente;
    public pagodialogo() {
    }

    public pagodialogo(int idpedido, Activity context){
        this.context = context;
        this.idpedido = idpedido;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return createLoginDialogo();
    }

    public AlertDialog createLoginDialogo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //getDialog().getWindow().setLayout(90, 120);
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.dialogopago, null);
        pago = (EditText) v.findViewById(R.id.pago);
        cliente = (EditText) v.findViewById(R.id.cliente);
        builder.setView(v);
        //setCancelable(true);
        //Button signup = (Button) v.findViewById(R.id.crear_boton);
        Button signin = (Button) v.findViewById(R.id.pagar);

        /*signup.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Crear Cuenta...
                        dismiss();
                    }
                }
        );*/

        signin.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(pago.getText().toString().equals("")){
                            Toast.makeText(context.getApplication(), "Error El Pago Es Obligatorio", Toast.LENGTH_SHORT).show();
                        }else{
                            entregado();
                            Double valor = Double.parseDouble(String.valueOf(pago.getText()));
                            String cli = cliente.getText().toString();
                            pagoemp(valor,cli);
                        }

                    }
                }

        );

        return builder.create();
    }

    public void entregado(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Webservices web = new Webservices();
                final JSONObject j = web.CambioEstado(idpedido,"ENTREGADO");
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if(j==null){
                                Toast.makeText(context.getApplication(), "Error en el servidor", Toast.LENGTH_SHORT).show();
                            }else{
                                if(j.getInt("msg") == 1){
                                    /*Toast.makeText(context.getApplication(), j.getString("message"), Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(context.getApplication(),menu_inicial.class);
                                    startActivity(i);*/
                                }
                            }
                        }catch (Exception ex){
                            Toast.makeText(context.getApplication(), "Error webSerice login, "+ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }

    public void pagoemp(final Double valor, final String clie){
        new Thread(new Runnable() {
            @Override
            public void run() {
            Webservices web = new Webservices();
            final JSONObject j = web.pagoempleado(valor,idpedido,clie);
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(j==null){
                            Toast.makeText(context.getApplication(), "Error en el servidor", Toast.LENGTH_SHORT).show();
                        }else{
                            if(j.getString("std").equals("1")){
                                //Toast.makeText(context.getApplication(), j.getString("message"), Toast.LENGTH_SHORT).show();
                                //Intent i = new Intent(context.getApplication(),menu_inicial.class);
                                //startActivity(i);
                                pago.setText("");
                                Intent intent = new Intent(context.getApplication(),menu_inicial.class);
                                intent.putExtra("dialog",1);
                                context.startActivity(intent);
                                context.finish();

                            }
                        }
                    }catch (Exception ex){
                        Toast.makeText(context.getApplication(), "Error webSerice login, "+ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            }
        }).start();
    }

}
