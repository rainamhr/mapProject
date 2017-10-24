package com.example.queen.parproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.LoginFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import static com.example.queen.parproject.R.id.disableHome;
import static com.example.queen.parproject.R.id.login_toolbar;

public class LoginActivity extends AppCompatActivity {

    EditText mLoginEmail, mLoginPassword;
    Button mLoginButton;

    //progress bar
    private ProgressDialog mProgressDialog;

    //firebase authentication
    FirebaseAuth firebaseAuth;

    //toolbar
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //set toolbar
        mToolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //firebase authentication
        firebaseAuth = FirebaseAuth.getInstance();

        //initializing progress bar
        mProgressDialog = new ProgressDialog(this);

        //initializing id
        mLoginEmail = (EditText) findViewById(R.id.login_input_email);
        mLoginPassword = (EditText) findViewById(R.id.login_input_password);
        mLoginButton = (Button) findViewById(R.id.login_button);

        //checking the validity of  email and password
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mLoginEmail.getText().toString();
                String password = mLoginPassword.getText().toString();

                if (!TextUtils.isEmpty(email) || (!TextUtils.isEmpty(password))){

                    mProgressDialog.setTitle("Logging In");
                    mProgressDialog.setMessage("Please wait while we check your credential");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();
                    loginUser(email, password);
                }
            }
        });
    }

    //user login through email and password, then taking to main page
    private void loginUser(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    mProgressDialog.dismiss();

                    Intent main_intent = new Intent(LoginActivity.this, MapsActivity.class);
                    main_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(main_intent);
                    finish();
                }else{
                    mProgressDialog.hide();
                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}
