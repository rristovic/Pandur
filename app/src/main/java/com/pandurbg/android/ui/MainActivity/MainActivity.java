package com.pandurbg.android.ui.MainActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.pandurbg.android.R;
import com.pandurbg.android.model.Post;
import com.pandurbg.android.ui.LoginActivity;
import com.pandurbg.android.util.AppExecutor;
import com.pandurbg.android.util.Constants;
import com.pandurbg.android.util.UserCredentials;
import com.pandurbg.android.util.Utils;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import butterknife.ButterKnife;

import static com.pandurbg.android.util.Constants.REQ_LOCATION_PERMISSION;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final float DEFAULT_MAP_ZOOM = 13;
    private FusedLocationProviderClient mFusedLocationProvider;
    private LocationCallback mLocationCallback;
    private MainActivityViewModel mMainViewModel;
    private GoogleMap mMap;
    private CountDownLatch mMapCountdown;
    private List<Marker> mMarkers = new LinkedList<>();
    private LocationRequest mLocationRequest;
    private AppExecutor mTaskExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mTaskExecutor = new AppExecutor();
        mMainViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mMainViewModel.getUserFeed()
                .observe(MainActivity.this, new Observer<Post>() {
                    @Override
                    public void onChanged(@Nullable Post newPost) {
                        if (newPost != null)
                            addMapMarker(newPost);
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        } else {
            init();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mFusedLocationProvider != null) {
            mFusedLocationProvider.removeLocationUpdates(getLocationCallback());
        }
    }

    @SuppressLint("MissingPermission")
    /**
     * Method for initialisation for location updates. Can be called only after location permissions are present.
     */
    private void init() {
        mFusedLocationProvider = LocationServices.getFusedLocationProviderClient(this);
        // Build location request
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(Constants.PARAM_LOCATION_UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(Constants.PARAM_LOCATION_UPDATE_INTERVAL);
        mLocationRequest.setSmallestDisplacement(Constants.PARAM_LOCATION_UPDATE_MIN_DISTANCE);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Check if all requirements are fulfilled for location updates
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        // Continue with updates
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                requestLocationUpdates();
            }
        });
        // Requirements are not complete
        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainActivity.this,
                                Constants.REQ_LOCATION_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void requestLocationUpdates() {
        mFusedLocationProvider.requestLocationUpdates(mLocationRequest, getLocationCallback(), null);
        if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        } else {
            mTaskExecutor.enqueue(new Runnable() {
                @Override
                public void run() {
                    mMap.setMyLocationEnabled(true);
                }
            });
        }
    }

    private LocationCallback getLocationCallback() {
        if (mLocationCallback == null) {
            mLocationCallback = new LocationCallback() {
                boolean init = true;

                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (init) {
                        if (locationResult != null) {
                            moveCamToCurrentLocation(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
                            init = false;
                            mMainViewModel.setInitialCoordinates(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
                        }
                    } else {
                        mMainViewModel.onLocationUpdate(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
                    }
                    super.onLocationResult(locationResult);
                }
            };
        }

        return mLocationCallback;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQ_LOCATION_SETTINGS) {
            if (resultCode == RESULT_OK) {
                requestLocationUpdates();
            } else {
                Utils.showLocationDissabledDialog(this);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQ_LOCATION_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        init();
                    }

                } else {
                    Utils.showLocationPermMissingDialog(this);
                }
                return;
            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mTaskExecutor.executeQueue();
    }

    /**
     * Helper method for checking location permission.
     */
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQ_LOCATION_PERMISSION);
        } else {
            init();
        }
    }


    private void addMapMarker(@NonNull final Post post) {
        LatLng latLng = new LatLng(post.getLocation().getLatitude(), post.getLocation().getLongitude());
        final MarkerOptions mo = new MarkerOptions();
        mo.position(latLng);
        if (mMap != null) {
            Marker m = mMap.addMarker(mo);
            m.setTag(post);
            mMarkers.add(m);
        } else {
            mTaskExecutor.enqueue(new Runnable() {
                @Override
                public void run() {
                    LatLng latLng = new LatLng(post.getLocation().getLatitude(), post.getLocation().getLongitude());
                    final MarkerOptions mo = new MarkerOptions();
                    mo.position(latLng);
                    Marker m = mMap.addMarker(mo);
                    m.setTag(post);
                    mMarkers.add(m);
                }
            });
        }
    }

    /**
     * Helper method for panning map camera to current location
     */
    private void moveCamToCurrentLocation(final double lat, final double lng) {
        if (mMap != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), DEFAULT_MAP_ZOOM));
        } else {
            mTaskExecutor.enqueue(new Runnable() {
                @Override
                public void run() {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), DEFAULT_MAP_ZOOM));
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            new UserCredentials().logout();
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
