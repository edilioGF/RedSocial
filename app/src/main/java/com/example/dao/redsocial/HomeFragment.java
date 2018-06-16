package com.example.dao.redsocial;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment  {
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private ArrayList<Publicacion> publicaciones = new ArrayList();
    private MyRecyclerViewAdapter adapter;
    private RecyclerView rv;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                publicaciones.clear();
                for (DataSnapshot publicacionSnapshot: dataSnapshot.getChildren() ) {
                    Publicacion publicacion = publicacionSnapshot.getValue(Publicacion.class);
                    publicaciones.add(0,publicacion);
                    Log.wtf("publicacion",publicacion.getTitulo());
                }
                adapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        View v =inflater.inflate(R.layout.fragment_home, container, false);
        rv = (RecyclerView) v.findViewById(R.id.my_recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MyRecyclerViewAdapter(publicaciones,getActivity());
        rv.setAdapter(adapter);




        return v;
    }

}
