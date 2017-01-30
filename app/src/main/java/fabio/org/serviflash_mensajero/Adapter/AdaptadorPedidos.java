package fabio.org.serviflash_mensajero.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.Fragment;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import fabio.org.serviflash_mensajero.R;
import fabio.org.serviflash_mensajero.Services.Server;
import fabio.org.serviflash_mensajero.Services.Webservices;
import fabio.org.serviflash_mensajero.Util.General;
import fabio.org.serviflash_mensajero.Util.ImagenesListView.ImageLoader;
import fabio.org.serviflash_mensajero.menu_inicial;
import layout.pedidosaceptados;


/**
 * Created by root on 20/08/16.
 */
public class AdaptadorPedidos extends ArrayAdapter {

    Activity context;
    ArrayList<JSONObject> listaGlobal;
    ArrayList<JSONObject> listaDatos;
    General gn;
    int idpedido;
    Button aceptar,cancelar;
    View item;
    Calendar calendario = new GregorianCalendar();
    int hora, minutos, segundos;

    public AdaptadorPedidos(Activity context,ArrayList<JSONObject> lista){

        super(context, R.layout.layout_pedidos,lista);
        this.context = context;

        this.listaDatos = lista;
        gn = new General(context,null);

        listaGlobal = new ArrayList<>();
        listaGlobal.addAll(lista);

    }

    public View getView(final int position, View convertView, final ViewGroup parent){
        item = convertView;

        item = context.getLayoutInflater().inflate(R.layout.layout_pedidos, null);

        hora =calendario.get(Calendar.HOUR_OF_DAY);
        minutos = calendario.get(Calendar.MINUTE);
        segundos = calendario.get(Calendar.SECOND);
        System.out.println("horaactual: "+hora + ":" + minutos + ":" + segundos);
        ImageLoader cargarImagen = new ImageLoader(context);
        TextView lblNombre = (TextView) item.findViewById(R.id.lblNombres);
        TextView tiempo= (TextView) item.findViewById(R.id.tiempo);
        TextView lblDescripcion = (TextView) item.findViewById(R.id.lbldireccionorigen);
        TextView lblEstado = (TextView) item.findViewById(R.id.lbldirecciondestino);
        TextView descripcion1 = (TextView) item.findViewById(R.id.descripcion1);
        TextView barrio = (TextView) item.findViewById(R.id.barrio);
        TextView barriodestino = (TextView) item.findViewById(R.id.barriodestino);
        ImageView foto = (ImageView) item.findViewById(R.id.clienteimage);
        aceptar = (Button) item.findViewById(R.id.aceptar);

        //cancelar = (Button) item.findViewById(R.id.cancelar);

        try{
            if(listaDatos.get(position).getString("nombreape") == null){
                lblNombre.setText("No hay pedidos solicitados");
            }else{
                idpedido = Integer.parseInt(listaDatos.get(position).getString("id"));
                lblNombre.setText("CLIENTE: "+listaDatos.get(position).getString("nombreape"));
                lblDescripcion.setText("DIR. ORIGEN: "+listaDatos.get(position).getString("direccionorigen"));
                lblEstado.setText("DIR. DESTINO: "+listaDatos.get(position).getString("direcciondestino"));
                descripcion1.setText("DESCRIPCIÓN: \n"+listaDatos.get(position).getString("descripcion"));
                barrio.setText("BARRIO ORIGEN: "+listaDatos.get(position).getString("barrioorigen"));
                barriodestino.setText("BARRIO DESTINO: "+listaDatos.get(position).getString("barriodestino"));
                if(listaDatos.get(position).getString("imagen").equals("")){
                    cargarImagen.DisplayImage(Server.rutaimg+"man.png",foto);
                }else{
                    cargarImagen.DisplayImage(listaDatos.get(position).getString("imagen"),foto);
                }
                String tiempopedido = listaDatos.get(position).getString("hora");
                DateFormat df = new SimpleDateFormat("hh:mm:ss");
                Date hora1 = df.parse(tiempopedido);
                Date hora2 = df.parse(hora + ":" + minutos + ":" + segundos);
                long diff = hora1.getTime() - hora2.getTime();
                long timeInSeconds = diff / 1000;
                long minutes,hours = 0;
                timeInSeconds = timeInSeconds - (hours * 3600);
                minutes = (timeInSeconds / 60)*-1;
                tiempo.setText(minutes+" minutos");
                /*String[] lista = tiempopedido.split(":");
                int minpedido;
                if(lista[1].equals("-")){
                    minpedido = Integer.parseInt(lista[1]) * -1;
                }else{
                    minpedido = Integer.parseInt(lista[1]);
                }
                int horapedido = Integer.parseInt(lista[0]);

                int secpedido = Integer.parseInt(lista[2]);
                System.out.println("horapedido: "+horapedido + ":" + minpedido + ":" + secpedido);
                long difhoras = hora - horapedido;
                long difminutos = (minutos - minpedido) * -1;
                long difsecons = segundos - secpedido;
                tiempo.setText(difhoras+":"+difminutos);*/
                //System.out.println("La diferencia es de: "+difhoras+":"+difminutos+":"+difsecons);

            }

        }catch (Exception ex){
            System.out.println("ERROR LISTA PETICIONES: "+ex.getMessage());
        }

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gn.initCargando("Cargando Pedidos");
            new Thread(new Runnable() {
                @Override
                public void run() {
                final Webservices web = new Webservices();
                final JSONObject j;
                j = web.estadopedido("ENCAMINO",idpedido);
                    gn.finishCargando();
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    try {
                        if(j==null){
                            Toast.makeText(context.getApplication(),j.getString("message"),Toast.LENGTH_LONG).show();
                        }else if (j.getInt("std") == 1) {
                            Toast.makeText(context.getApplication(),"Pedido A Sido Aceptado",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(context,menu_inicial.class);
                            intent.putExtra("aceptado",1);
                            context.startActivity(intent);
                            context.finish();
                        }
                    }catch (Exception ex){
                        Toast.makeText(context.getApplication(),"Error webSerice login, "+ex.getMessage(),Toast.LENGTH_LONG).show();
                    }
                    }
                });
                }
            }).start();
            }
        });

        /*cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final Webservices web = new Webservices();
                        final JSONObject j;
                        j = web.estadopedido("RECHAZADO",idpedido);
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if(j==null){
                                        Toast.makeText(context.getApplication(),j.getString("message"),Toast.LENGTH_LONG).show();
                                    }else{
                                        if(j.getInt("std") == 1){
                                            Toast.makeText(context.getApplication(),j.getString("message"),Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }catch (Exception ex){
                                    Toast.makeText(context.getApplication(),"Error webSerice login, "+ex.getMessage(),Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }).start();
            }
        });*/

        return item;
    }

    private void montarImagen(final String ruta){
        //System.out.println("img: "+ruta[pos]);
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
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final ImageView img = ((ImageView)item.findViewById(R.id.clienteimage));
                        Drawable drawable = new BitmapDrawable(ftax);
                        img.setImageDrawable(drawable);
                    }
                });
            }
        }).start();
    }

}
