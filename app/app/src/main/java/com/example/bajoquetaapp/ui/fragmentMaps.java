package com.example.bajoquetaapp.ui;

import static android.app.Activity.RESULT_OK;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.bajoquetaapp.R;
import com.example.bajoquetaapp.databinding.FragmentMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class fragmentMaps extends Fragment implements OnMapReadyCallback{
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final float DEFAULT_ZOOM = 15;
    private FragmentMapsBinding binding;
    double currentLat = 0.0, currentLng = 0.0;
    private boolean locationPermissionGranted;
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private Location lastKnownLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);

    public List<LatLng> listaSupermercados = new ArrayList<>();
    public List<String> nombreListaSupermercados = new ArrayList<>();

    //Asistente De Voz
    String strSpeech2Text=null;
    private static final int RECOGNIZE_SPEECH_ACTIVITY = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMapsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Initialize map fragment
        mapFragment=(SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        ImageButton boton_miUbicacion = (ImageButton) view.findViewById(R.id.imageButton_miUbicacion);
        boton_miUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceLocation();
            }
        });

        ImageButton boton_microfono = (ImageButton) view.findViewById(R.id.img_btn_hablar);
        boton_microfono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentActionRecognizeSpeech = new Intent(
                        RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                // Configura el Lenguaje (Español-México)
                intentActionRecognizeSpeech.putExtra(
                        RecognizerIntent.EXTRA_LANGUAGE_MODEL, "es-MX");
                try {
                    startActivityForResult(intentActionRecognizeSpeech,
                            RECOGNIZE_SPEECH_ACTIVITY);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getContext().getApplicationContext(),
                            "Tú dispositivo no soporta el reconocimiento por voz",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;

        LatLng carrefour, mercadona, lidl;
        listaSupermercados.add(
                carrefour = new LatLng(38.9971, -0.1626)
        );
        nombreListaSupermercados.add("Carrefour");
        listaSupermercados.add(
                mercadona = new LatLng(38.9913, -0.1628)
        );
        nombreListaSupermercados.add("Mercadona");
        listaSupermercados.add(
                lidl = new LatLng(38.9685, -0.1714)
        );
        nombreListaSupermercados.add("Lidl");

        for (int i=0;i<listaSupermercados.size();i++){
            map.addMarker(new MarkerOptions()
                    .position(listaSupermercados.get(i))
                    .title(""+nombreListaSupermercados.get(i)));
        }

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
    }
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.requireActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this.requireActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode
                == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d("location_debug", "Current location is null. Using defaults.");
                            Log.e("location_debug", "Exception: %s", task.getException());
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    //----------------------------------------------------------------------------------------------
    //Asistente de voz
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RECOGNIZE_SPEECH_ACTIVITY:
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> speech = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    strSpeech2Text = speech.get(0);

                    moverCamara(strSpeech2Text);
                }
                break;
            default:
                break;
        }
    }
    public void moverCamara(String lugar){
        if (nombreListaSupermercados.contains(lugar)){
            for (int i=0;i<nombreListaSupermercados.size();i++){
                if (lugar.equals(nombreListaSupermercados.get(i))){
                    map.moveCamera(CameraUpdateFactory
                            .newLatLngZoom(listaSupermercados.get(i), DEFAULT_ZOOM));
                }
            }
        }
        else if(lugar.equals("mi ubicación")||lugar.equals("Mi ubicación")){
            getDeviceLocation();
        }
        else{
            Toast.makeText(getContext().getApplicationContext(),
                    "El sitio que buscas no está en nuestra base de datos",
                    Toast.LENGTH_SHORT).show();
        }
    }
}

