package com.example.dao.redsocial;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class UserLogOut extends AppCompatActivity implements View.OnClickListener{
    private TextView email;
    private Button botonSalir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_log_out);

        email = (TextView)findViewById(R.id.txtShowEmail);
        email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        botonSalir = (Button) findViewById(R.id.btnSalir);
        botonSalir.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(UserLogOut.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
