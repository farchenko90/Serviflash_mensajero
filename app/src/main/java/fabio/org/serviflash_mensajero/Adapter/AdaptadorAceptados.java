package fabio.org.serviflash_mensajero.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import fabio.org.serviflash_mensajero.R;
import fabio.org.serviflash_mensajero.Services.Server;
import fabio.org.serviflash_mensajero.Util.General;
import fabio.org.serviflash_mensajero.Util.ImagenesListView.ImageLoader;
import fabio.org.serviflash_mensajero.mensajero_mapa;

/**
 * Created by root on 24/08/16.
 */
public class AdaptadorAceptados extends ArrayAdapter{

    Activity context;
    ArrayList<JSONObject> listaDatos;
    ArrayList<JSONObject> listaGlobal;
    General gn;
    String idpedido;
    Button frgmapa;
    View item;
    int min;

    public AdaptadorAceptados(Activity context,ArrayList<JSONObject> lista){
        super(context, R.layout.layout_aceptados,lista);
        this.context = context;
        this.listaDatos = lista;

        gn = new General(context,null);

        listaGlobal = new ArrayList<>();
        listaGlobal.addAll(lista);
    }

    public View getView(final int position, View convertView, final ViewGroup parent){
        item = convertView;

        item = context.getLayoutInflater().inflate(R.layout.layout_aceptados, null);
        ImageLoader cargarImagen = new ImageLoader(context);
        TextView lblNombre = (TextView) item.findViewById(R.id.lbnombre);
        TextView lblDescripcion = (TextView) item.findViewById(R.id.lbdireccionorigen);
        TextView lblEstado = (TextView) item.findViewById(R.id.lbdirecciondestino);
        TextView descrip = (TextView) item.findViewById(R.id.descripcionaceptados);
        TextView barrio = (TextView) item.findViewById(R.id.barrio1);
        TextView barriodestino = (TextView) item.findViewById(R.id.barriodestino1);
        ImageView foto = (ImageView) item.findViewById(R.id.circle_image);
        frgmapa = (Button) item.findViewById(R.id.frgmapa);

        try{
            if(listaDatos.get(position).getString("nombreape") == null){
                lblNombre.setText("No hay pedidos solicitados");
            }else{
                //idpedido = listaDatos.get(position).getString("id");
                lblNombre.setText("CLIENTE: "+listaDatos.get(position).getString("nombreape"));
                lblDescripcion.setText("DIR. ORIGEN: "+listaDatos.get(position).getString("direccionorigen"));
                lblEstado.setText("DIR. DESTINO: "+listaDatos.get(position).getString("direcciondestino"));
                barrio.setText("BARRIO ORIGEN: "+listaDatos.get(position).getString("barrioorigen"));
                barriodestino.setText("BARRIO DESTINO: "+listaDatos.get(position).getString("barriodestino"));
                descrip.setText("DESCRIPCIÓN: \n"+listaDatos.get(position).getString("descripcion"));
                if(listaDatos.get(position).getString("imagen").equals("")){
                    cargarImagen.DisplayImage(Server.rutaimg+"man.png",foto);
                }else{
                    cargarImagen.DisplayImage(listaDatos.get(position).getString("imagen"),foto);
                }
                /*if(listaDatos.get(position).getString("sexo").equals("M")){
                    cargarImagen.DisplayImage(Server.rutaimg+"man.png",foto);
                }else{
                    cargarImagen.DisplayImage(Server.rutaimg+"female.png",foto);
                }*/
                //System.out.println(listaDatos.get(position).getString("imagen"));
                /*if(listaDatos.get(position).getString("imagen").equals("")){

                }else{
                    montarImagen(listaDatos.get(position).getString("imagen"));
                }*/


                //System.out.println("id "+idpedido);
            }

        }catch (Exception ex){
            System.out.println("ERROR LISTA PETICIONES: "+ex.getMessage());
        }

        frgmapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mapaenvio mapa = new mapaenvio();
                //Bundle bundle = new Bundle();
                //bundle.putInt("Id",idpedido);

            try{
                //System.out.println("pos: "+position);
                idpedido = listaDatos.get(position).getString("id");
                Intent intent = new Intent(context,mensajero_mapa.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("Dato",idpedido);
                context.startActivity(intent);
            }catch (Exception ex){
                System.out.println("ERROR: "+ex.getMessage());
            }


            }
        });

        return item;
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
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final ImageView img = ((ImageView)item.findViewById(R.id.circle_image));
                        Drawable drawable = new BitmapDrawable(ftax);
                        img.setImageDrawable(drawable);
                    }
                });
            }
        }).start();
    }

}
