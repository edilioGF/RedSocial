package com.example.dao.redsocial;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText TextEmail;
    private EditText TextPassword;
    private Button BotonRegistrar;
    private Button BotonLog;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        TextEmail = (EditText) findViewById(R.id.txtEmail);
        TextPassword = (EditText) findViewById(R.id.txtPassword);

        BotonRegistrar = (Button) findViewById(R.id.btnReg);
        BotonLog = (Button) findViewById(R.id.btnLog);

        progressDialog = new ProgressDialog(this);

        BotonRegistrar.setOnClickListener(this);
        BotonLog.setOnClickListener(this);


    }

    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseAuth.getCurrentUser()!= null){
            finish();
            Intent intent = new Intent(this,SocialMediaActivity.class);
            startActivity(intent);
        }
    }

    private void LogIn(){

        String email = TextEmail.getText().toString().trim();
        String password  = TextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Se debe ingresar un email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Falta ingresar la contraseña",Toast.LENGTH_LONG).show();
            return;
        }


        progressDialog.setMessage("Realizando registro en linea...");
        progressDialog.show();

        //creating a new user
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if(task.isSuccessful()){
                           // Toast.makeText(MainActivity.this,"Bienvenido: "+ TextEmail.getText(),Toast.LENGTH_LONG).show();
                            //finish();
                            //Intent intent = new Intent(getApplication(),SocialMediaActivity.class);
                            //startActivity(intent);
                            checkEmailverification();
                        }else if(!isOnlineNet()){
                            Toast.makeText(MainActivity.this,"No hay conexión a internet", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MainActivity.this,"El usuario o la contraseña es incorrecto", Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });

    }



    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btnReg:
                finish();
                Intent intent = new Intent(getApplication(),RegisterActivity.class);
                startActivity(intent);
                break;

            case  R.id.btnLog:
                LogIn();
                break;
        }

    }

    public Boolean isOnlineNet() {

        try {
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.es");

            int val           = p.waitFor();
            boolean reachable = (val == 0);
            return reachable;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public void checkEmailverification() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user.isEmailVerified()) {
            Toast.makeText(MainActivity.this, "Bienvenido: " + TextEmail.getText(), Toast.LENGTH_SHORT).show();
            finish();
            Intent intent = new Intent(getApplication(), SocialMediaActivity.class);
            startActivity(intent);
        } else{
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(MainActivity.this, "El email no ha sido verificado",Toast.LENGTH_SHORT).show();
        }
    }

}
