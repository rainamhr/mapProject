package com.example.queen.parproject;

import android.*;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.queen.parproject.Adapter.CustomAdapter;
import com.example.queen.parproject.model.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.onClick;
import static android.R.attr.start;

public class MapsActivity extends ActionBarActivity implements OnMapReadyCallback, LocationListener {

    //toolbar
    public Toolbar mtoolbar;

    //firebase authentication
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //recuclerView
    RecyclerView recyclerView;
    CustomAdapter rec_adapter;

    List<User> userlist = new ArrayList<User>();

    //firebase database
    DatabaseReference mFirebaseDatabase;


    //google map
    private GoogleMap mMap;
    final static int PERMISSION_ALL = 1;
    final static String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};
 //   MarkerOptions mo;
    Marker marker;
    LocationManager locationManager;

    private static final String TAG = "Result";

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //toolbar set
        mtoolbar = (Toolbar) findViewById(R.id.map_page_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Parmod Project");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //recyclerview set
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_layout);

        //map set
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (Build.VERSION.SDK_INT > 18 && !isPermissionGranted()) {
                requestPermissions(PERMISSIONS,PERMISSION_ALL);
            }else requestLocation();
        }*/
        if(!isLocationEnabled()){
            showAlert(1);
        }

        //firebase authentication
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Intent startIntent = new Intent(MapsActivity.this, StartActivity.class);
                    startActivity(startIntent);
                    finish();

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                }
            }
        };
    }

    //from firebase assistant
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

        FirebaseDatabase mFirebaseInstance = FirebaseDatabase.getInstance();

        mFirebaseDatabase = mFirebaseInstance.getReference("users");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("mData", dataSnapshot.getKey());
                Log.d("mData", String.valueOf(dataSnapshot.hasChildren()));
                Log.d("mData", String.valueOf(dataSnapshot.getChildrenCount()));

                userlist.clear();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    Log.d("mData Key", userSnapshot.getKey());

                    Log.d("mdata", userSnapshot.toString());

                    User _user = userSnapshot.getValue(User.class);
                    User user = new User(_user, userSnapshot.getKey());
                    // User user = userSnapshot.getValue(User.class);
                    Log.d("mData idvalue", user.toString());
                    userlist.add(user);
                    //Log.d("mData User", user.getEmail());
                }

                recyclerView.setLayoutManager(new LinearLayoutManager(getApplication()));

                rec_adapter = new CustomAdapter(userlist);
                recyclerView.setAdapter(rec_adapter);
                recyclerView.setHasFixedSize(true);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    //menu create
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    //menu option select
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.main_logout_button) {
            FirebaseAuth.getInstance().signOut();
        }


        return true;
    }


    @Override
    public void onLocationChanged(Location location) {
        LatLng myCordinates = new LatLng(location.getLatitude(), location.getLongitude());
        marker.setPosition(myCordinates);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myCordinates));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public void requestLocation() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        String provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(provider, 5000, 5, this);
    }

    private boolean isLocationEnabled(){
        return locationManager.isProviderEnabled(locationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER);
    }

   /* @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private boolean isPermissionGranted(){
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            Log.v("myLog", "permission granted");
            return true;
        }else{
            Log.v("myLog","permission not granted");
            return false;
        }
    }*/

    private void showAlert(final int status){
        String msg, title, btnText;
        if (status==1){
                msg = "your location setting is set 'off'.\n" +
                        "Please enable location to" +
                        "use this app";
            title = "emable location";
            btnText = "location setting";
        }else {
            msg = "please allow this app to access location";
            title = "permission access";
            btnText = "Grant";
        }
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setTitle(title)
                .setMessage(msg)
                .setPositiveButton(btnText, new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (status == 1) {
                            Intent map_intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(map_intent);
                        }else{
                            requestPermissions(PERMISSIONS,PERMISSION_ALL);
                        }

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        dialog.show();
    }

    public void logout() {
        Log.d("signout", "home");
        Intent logout = new Intent(MapsActivity.this, StartActivity.class);
        logout.putExtra("logout1", true);
        startActivity(logout);
        finish();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("Exit?");
        builder.setPositiveButton("Yes",   new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logout();
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        final LatLng MYHOME = new LatLng(27.668639, 85.278899);
        final LatLng PERTH = new LatLng(-31.952854, 115.857342);
        final LatLng WLINK = new LatLng(27.672439, 85.314113);
        final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);
        final LatLng PARMODHOME = new LatLng(27.656565, 85.314257);
        mMap.addMarker(new MarkerOptions().position(MYHOME).title("Current Location").draggable(true));
        mMap.addMarker(new MarkerOptions().position(PERTH).title("Perth").draggable(true));
        mMap.addMarker(new MarkerOptions().position(WLINK).title(("Wlink")));
        mMap.addMarker(new MarkerOptions().position(SYDNEY).title(("Sydney")));
        mMap.addMarker(new MarkerOptions().position(PARMODHOME).title(("Parmod Home")));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MYHOME,13));
    }
}
