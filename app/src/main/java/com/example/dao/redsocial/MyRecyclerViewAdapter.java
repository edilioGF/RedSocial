package com.example.dao.redsocial;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyRecycleItemViewHolder> {
    private ArrayList<Publicacion> items;
    private Context context;


    public MyRecyclerViewAdapter(ArrayList<Publicacion> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public MyRecycleItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_view,parent,false);
        MyRecycleItemViewHolder holder = new MyRecycleItemViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyRecycleItemViewHolder holder, int position) {
     final Publicacion publicacion = items.get(position);
     holder.encabezado.setText(publicacion.getTitulo());
     holder.descripcion.setText(publicacion.getDescripcion());
     holder.location.setText(publicacion.getLocation());
     Glide.with(holder.itemView).load(publicacion.getImagen()).into(holder.foto);
     holder.cardView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
             StrictMode.setVmPolicy(builder.build());
             Intent shareIntent;
             Bitmap bitmap = ((BitmapDrawable) holder.foto.getDrawable()).getBitmap();
             String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/Share.jpeg";
             OutputStream out = null;
             File file=new File(path);
             try {
                 out = new FileOutputStream(file);
                 bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                 out.flush();
                 out.close();
             } catch (Exception e) {
                 e.printStackTrace();
             }
             path=file.getPath();
             Uri bmpUri = Uri.parse("file://"+path);
             shareIntent = new Intent(android.content.Intent.ACTION_SEND);
             shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
             shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
             shareIntent.putExtra(Intent.EXTRA_TEXT,"Title: " + publicacion.getTitulo() + "\n" +
                     "Description: " +  publicacion.getDescripcion() + "\n" +
                     "Published From: " + publicacion.getLocation());
             shareIntent.setType("image/jpeg");
             holder.itemView.getContext().startActivity(Intent.createChooser(shareIntent,"Compartir con: " ));
         }
     });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MyRecycleItemViewHolder extends RecyclerView.ViewHolder {
        private TextView encabezado;
        private TextView descripcion;
        private ImageView foto;
        private TextView location;
        private CardView cardView;

        public MyRecycleItemViewHolder(View itemView) {
            super(itemView);
            location = (TextView) itemView.findViewById(R.id.my_location_text_view);
            foto = (ImageView) itemView.findViewById(R.id.my_image_view);
            encabezado = (TextView) itemView.findViewById(R.id.my_name_text_view);
            descripcion = (TextView) itemView.findViewById(R.id.my_description_text_view);
            cardView = (CardView) itemView.findViewById(R.id.my_card_view);
        }
    }
}
