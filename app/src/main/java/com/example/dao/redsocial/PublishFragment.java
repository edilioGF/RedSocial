package com.example.dao.redsocial;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class PublishFragment extends Fragment{
    private static final int CHOOSE_IMAGE = 101;
    private String newImage;
    private Button botonLoad;
    private Button botonPublicar;
    private ImageView foto;
    private TextView textoLoc;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private double lat;
    private double lon;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Uri selectedImage;
    private TextView txtEncabezado;
    private TextView txtDescripcion;
    private ProgressBar progressBar;
    private StorageTask storageTask;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_publish, container, false);
        botonLoad = (Button) v.findViewById(R.id.btnLoadFoto);
        botonPublicar = (Button) v.findViewById(R.id.btnPublicar);
        foto = (ImageView) v.findViewById(R.id.imgFoto);
        txtEncabezado = (TextView) v.findViewById(R.id.txtEnc);
        txtDescripcion = (TextView) v.findViewById(R.id.txtDesc);
        firebaseDatabase = FirebaseDatabase.getInstance();
        progressBar = (ProgressBar)v.findViewById(R.id.my_progress_bar);
        databaseReference = firebaseDatabase.getReference();
        foto.setImageResource(R.drawable.camera_icon_hi);
        botonLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { showImageChooser(); }
        });
        botonPublicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               if(storageTask != null && storageTask.isInProgress()){
                   Toast.makeText(getContext(), "Cargando Publicación", Toast.LENGTH_SHORT).show();
               }else{
                loadImageOnFirebaseStorage();
               }
            }
        });

        textoLoc = (TextView) v.findViewById(R.id.txtLoc);


        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
              lat = location.getLatitude();
              lon = location.getLongitude();
              textoLoc.setText(location.getLatitude() + ", " + location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

            if (ActivityCompat.
                    checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);

            } else {
                locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        return v;
    }

    private void showImageChooser() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Context context = getContext();
            Activity activity = getActivity();
            if (context != null && activity != null) {
                if (permissionIsGranted(context,1)) {
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            1);
                } else {
                    Log.wtf("TAG", "PERMISSION GRANTED");
                    getPhoto();
                }
            }
        } else {
            //Is lower than API23 and permission is given when the app is installed;
            getPhoto();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            selectedImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContext().getContentResolver(), selectedImage);
                foto.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto();
            }
        }

        if(requestCode == 2){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if(ContextCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER,0,0,locationListener);
                }

            }
        }
    }

    public void getPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    public boolean permissionIsGranted(Context context, int code) {
        if (code == 1) {
            return ActivityCompat.
                    checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED;
        }

        return false;
    }



    public String hereLocation(double lat, double lon) {
        String ourCity ="";

        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addressList;
        try{
            addressList = geocoder.getFromLocation(lat, lon , 1);
            if(addressList.size() > 0){
                ourCity = addressList.get(0).getLocality();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return ourCity;
    }


    //LOADS IMAGE TO STORAGE AND CREATES NEWS FOR DATABASE
    private void loadImageOnFirebaseStorage() {
        //FIREBASE STORAGE REFERENCE
        StorageReference newsImageRef = FirebaseStorage.getInstance().
                getReference("NewsPics/" + System.currentTimeMillis() + ".jpg");

        if(selectedImage != null){
            //Añadir imagen al firebase
            storageTask = newsImageRef.putFile(selectedImage).
                    addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //Crear un runnable para el progress bar
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(0);
                                }
                            }, 500);
                            taskSnapshot.getMetadata().getReference().getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            //lo que se sube a la base de datos es el string de un URL
                                            newImage = uri.toString();
                                            addNews();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //error que te da android
                            Toast.makeText(getActivity(), e.getMessage(),
                                    Toast.LENGTH_SHORT).show();

                        }
                    })
            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    //progess bar de la transferencia de bytes
                    double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    progressBar.setProgress((int)progress);
                }
            });
        }
    }

    //NEWS CREATOR
    private void addNews() {
        //ADD NEWS ON DATABASE
        String title = txtEncabezado.getText().toString().trim();
        String description = txtDescripcion.getText().toString().trim();
        String locationCity = hereLocation(lat,lon);

        if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(description)){

            String id = databaseReference.push().getKey();

            //Crear una publicacion
            Publicacion publicacion = new Publicacion(title,description,newImage,locationCity);

            //Añadir la publicacion a la base de datos
            databaseReference.child(id).setValue(publicacion);

            Toast.makeText(getActivity(), "Noticia Agregada!", Toast.LENGTH_SHORT).show();
            resetPublication();
        }else{
            Toast.makeText(getActivity(), "Todos los campos deben ser llenados", Toast.LENGTH_SHORT).show();
        }
    }

    public void resetPublication(){

        txtEncabezado.setText("");
        txtDescripcion.setText("");
        foto.setImageResource(R.drawable.camera_icon_hi);
    }


}



