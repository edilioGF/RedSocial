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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private EditText TextEmail;
    private EditText TextPassword;
    private EditText TextPassword2;
    private Button BtnRegistrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        TextEmail = (EditText) findViewById(R.id.txtEmail);
        TextPassword = (EditText) findViewById(R.id.txtPassword);
        TextPassword2 = (EditText) findViewById(R.id.txtPass2);
        BtnRegistrar = (Button) findViewById(R.id.btnReg);
        BtnRegistrar.setOnClickListener(this);

    }

    private void registrarUsuario(){

        String email = TextEmail.getText().toString().trim();
        final String password  = TextPassword.getText().toString().trim();
        final String pass2 = TextPassword2.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Se debe ingresar un email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Falta ingresar la contraseña",Toast.LENGTH_LONG).show();
            return;
        }
        if (!(password.matches(pass2))) {
            Toast.makeText(RegisterActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }


        progressDialog.setMessage("Realizando registro en linea...");
        progressDialog.show();

        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                            if (task.isSuccessful()) {
                                sendVerificationEmail();
                            } else {
                                if (!isOnlineNet()) {
                                    Toast.makeText(RegisterActivity.this, "No hay conexión a internet", Toast.LENGTH_SHORT).show();
                                }
                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    Toast.makeText(RegisterActivity.this, "El usuario ya existe", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(RegisterActivity.this, "No se pudo registrar el usuario ", Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressDialog.dismiss();
                        }
                });

    }

    public Boolean isOnlineNet() {

        try {
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.es");

            int val = p.waitFor();
            boolean reachable = (val == 0);
            return reachable;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    private void sendVerificationEmail()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // email sent
                            Toast.makeText(RegisterActivity.this, "AN EMAIL WAS SENT TO VERIFY YOUR ACCOUNT", Toast.LENGTH_SHORT).show();
                            // after email is sent just logout the user and finish this activity
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            // email not sent, so display message and restart the activity or do whatever you wish to do
                            Toast.makeText(RegisterActivity.this, "THERE WAS AN ERROR SENDING EMAIL, TRY VERIFYING IF EMAIL EXISTS", Toast.LENGTH_SHORT).show();
                            //restart this activity
                            overridePendingTransition(0, 0);
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        registrarUsuario();
    }
}
