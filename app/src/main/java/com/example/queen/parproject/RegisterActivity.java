package com.example.queen.parproject;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import static android.R.attr.name;
import static android.R.attr.password;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout mName;
    private TextInputLayout mAddress;
    private TextInputLayout mNumber;
    private TextInputLayout mEmail;
    private EditText mPassword;
    private Button mCreateButton;
  /*  private TextView gps;
    private EditText mlatitude, mlongitude;*/

    private String userId;

    //toolbar
    private Toolbar mtoolbar;

    //Firebase Authentication
    public DatabaseReference mFirebaseDatabase;
    private FirebaseUser mCurrentUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //firebase authentication
        mAuth = FirebaseAuth.getInstance();

        //toolbar set
        mtoolbar = (Toolbar) findViewById(R.id.register_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //initializing id
        mName = (TextInputLayout) findViewById(R.id.reg_input_name);
        mAddress = (TextInputLayout) findViewById(R.id.reg_input_address);
        mNumber = (TextInputLayout) findViewById(R.id.reg_input_number);
        mEmail = (TextInputLayout) findViewById(R.id.reg_input_email);
        mPassword = (EditText) findViewById(R.id.reg_input_password);
      /*  gps = (TextView) findViewById(R.id.gps);
        mlatitude = (EditText) findViewById(R.id.input_latitude);
        mlongitude = (EditText) findViewById(R.id.input_longitude);*/
        mCreateButton = (Button) findViewById(R.id.reg_create_button);

        //create button working process
        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ProgressDialog progressDialog = ProgressDialog.show(RegisterActivity.this, "please wait", "processing", true);

                mAuth.createUserWithEmailAndPassword(mEmail.getEditText().getText().toString(), mPassword.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.dismiss();

                                if (task.isSuccessful()){
                                    //inserting data into firebase database
                                    mFirebaseDatabase = FirebaseDatabase.getInstance().getReference("users");
                                    createUser();
                                }
                                else{
                                    progressDialog.hide();
                                    Log.e("ERROR", task.getException().getMessage());
                                    Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });

            }
        });
    }

    //creating a new user
    public void createUser() {

        String name = mName.getEditText().getText().toString();
        String address = mAddress.getEditText().getText().toString();
        String number = mNumber.getEditText().getText().toString();
        String email = mEmail.getEditText().getText().toString();
        String password = mPassword.getText().toString();
     /*   String latitude = mlatitude.getText().toString();
        String longitude = mlongitude.getText().toString();*/


        //checking if the fields are empty or not
        if ((!TextUtils.isEmpty(name)) && (!TextUtils.isEmpty(address))
                && (!TextUtils.isEmpty(number)) && (!TextUtils.isEmpty(email)) && (!TextUtils.isEmpty(password))) {

            Map userMap = new HashMap();
            userMap.put("name", name);
            userMap.put("address", address);
            userMap.put("number", number);
            userMap.put("email", email);
            userMap.put("password", password);
       /*     userMap.put("latitude",latitude);
            userMap.put("longitude",longitude);*/

            //firebase database
            if (mAuth != null) {
                mCurrentUser = mAuth.getCurrentUser();
                userId = mCurrentUser.getUid();
                mFirebaseDatabase.child(userId).setValue(userMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            showAlert("Success!", "User successfully registered");
                        }
                    }
                });
            }
        } else {
            Toast.makeText(getApplicationContext(), "Blank field", Toast.LENGTH_LONG).show();
        }
    }

    //alert box for successful signing in and taking into main activity
    private void showAlert(String title, String msg) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle(title)
                .setMessage(msg)
                .setNeutralButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent register_intent = new Intent(RegisterActivity.this, MapsActivity.class);
                        register_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(register_intent);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
