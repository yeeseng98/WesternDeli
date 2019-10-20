package com.yeeseng.westerndeli.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.core.exceptions.ServicesException;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.yeeseng.westerndeli.R;
import com.yeeseng.westerndeli.model.Order_Item;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.mapbox.mapboxsdk.style.layers.Property.NONE;
import static com.mapbox.mapboxsdk.style.layers.Property.VISIBLE;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.visibility;

public class PlacePickerActivity extends AppCompatActivity implements PermissionsListener, OnMapReadyCallback {

    private static final String TAG = "PlacePickerActivity";
    private static final String DROPPED_MARKER_LAYER_ID = "DROPPED_MARKER_LAYER_ID";
    private MapView mapView;
    private MapboxMap mapboxMap;
    private Button selectLocationButton;
    private PermissionsManager permissionsManager;
    private ImageView hoveringMarker;

    @OnClick(R.id.backBt)
    public void back() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Mapbox access token is configured here. This needs to be called either in your application
// object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, getString(R.string.access_token));

        setContentView(R.layout.place_picker);
        ButterKnife.bind(this);

        try {
            mapView = findViewById(R.id.mapView);
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(this);
        } catch (Exception e) {
            onButtonShowErrorWindowClick(PlacePickerActivity.this.findViewById(android.R.id.content), "Something went wrong! Have you enabled your location?");
        }
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        PlacePickerActivity.this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull final Style style) {
                enableLocationPlugin(style);

// Toast instructing user to tap on the mapboxMap
                Toast.makeText(
                        PlacePickerActivity.this,
                        getString(R.string.move_map_instruction), Toast.LENGTH_SHORT).show();

// When user is still picking a location, we hover a marker above the mapboxMap in the center.
// This is done by using an image view with the default marker found in the SDK. You can
// swap out for your own marker image, just make sure it matches up with the dropped marker.
                hoveringMarker = new ImageView(PlacePickerActivity.this);
                hoveringMarker.setImageResource(R.drawable.red_marker);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
                hoveringMarker.setLayoutParams(params);
                mapView.addView(hoveringMarker);

// Initialize, but don't show, a SymbolLayer for the marker icon which will represent a selected location.
                initDroppedMarker(style);

// Button for user to drop marker or to pick marker back up.
                selectLocationButton = findViewById(R.id.select_location_button);
                selectLocationButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (hoveringMarker.getVisibility() == View.VISIBLE) {

// Use the map target's coordinates to make a reverse geocoding search
                            final LatLng mapTargetLatLng = mapboxMap.getCameraPosition().target;

// Hide the hovering red hovering ImageView marker
                            hoveringMarker.setVisibility(View.INVISIBLE);

// Transform the appearance of the button to become the cancel button
                            selectLocationButton.setBackgroundColor(
                                    ContextCompat.getColor(PlacePickerActivity.this, R.color.colorAccent));
                            selectLocationButton.setText(getString(R.string.cancel));

// Show the SymbolLayer icon to represent the selected map location
                            if (style.getLayer(DROPPED_MARKER_LAYER_ID) != null) {
                                GeoJsonSource source = style.getSourceAs("dropped-marker-source-id");
                                if (source != null) {
                                    source.setGeoJson(Feature.fromGeometry(Point.fromLngLat(
                                            mapTargetLatLng.getLongitude(), mapTargetLatLng.getLatitude())));
                                }
                                style.getLayer(DROPPED_MARKER_LAYER_ID).setProperties(visibility(VISIBLE));
                            }

// Use the map camera target's coordinates to make a reverse geocoding search
                            reverseGeocode(style, Point.fromLngLat(mapTargetLatLng.getLongitude(), mapTargetLatLng.getLatitude()));

                        } else {

// Switch the button appearance back to select a location.
                            selectLocationButton.setBackgroundColor(
                                    ContextCompat.getColor(PlacePickerActivity.this, R.color.colorPrimary));
                            selectLocationButton.setText(getString(R.string.confirm));
                            Button confirmBt = (Button) findViewById(R.id.confirm_location_button);
                            confirmBt.setVisibility(view.GONE);

// Show the red hovering ImageView marker
                            hoveringMarker.setVisibility(View.VISIBLE);

// Hide the selected location SymbolLayer
                            if (style.getLayer(DROPPED_MARKER_LAYER_ID) != null) {
                                style.getLayer(DROPPED_MARKER_LAYER_ID).setProperties(visibility(NONE));
                            }
                        }
                    }
                });
            }
        });
    }

    private void initDroppedMarker(@NonNull Style loadedMapStyle) {
// Add the marker image to map
        loadedMapStyle.addImage("dropped-icon-image", BitmapFactory.decodeResource(
                getResources(), R.drawable.blue_marker));
        loadedMapStyle.addSource(new GeoJsonSource("dropped-marker-source-id"));
        loadedMapStyle.addLayer(new SymbolLayer(DROPPED_MARKER_LAYER_ID,
                "dropped-marker-source-id").withProperties(
                iconImage("dropped-icon-image"),
                visibility(NONE),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
        ));
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted && mapboxMap != null) {
            Style style = mapboxMap.getStyle();
            if (style != null) {
                enableLocationPlugin(style);
            }
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void reverseGeocode(@NonNull final Style style, final Point point) {
        try {
            Geocoder client = new Geocoder(this, Locale.ENGLISH);

            try {
                List<Address> addresses = client.getFromLocation(point.latitude(), point.longitude(), 1);

                if (addresses.size() > 0) {
                    Address first = addresses.get(0);
                    if (style.isFullyLoaded() && style.getLayer(DROPPED_MARKER_LAYER_ID) != null) {

                        Toast.makeText(PlacePickerActivity.this,
                                String.format(getString(R.string.location_picker_place_name_result),
                                        first.getAddressLine(0)), Toast.LENGTH_SHORT).show();

                        Button confirmBt = (Button) findViewById(R.id.confirm_location_button);
                        confirmBt.setVisibility(View.VISIBLE);
                        confirmBt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Bundle extras = getIntent().getExtras();
                                ArrayList<Order_Item> orderList = new ArrayList<>();
                                orderList = (ArrayList<Order_Item>) extras.get("OrderList");

                                Intent finalize = new Intent(PlacePickerActivity.this, FinalizeOrderActivity.class);
                                finalize.putExtra("OrderList", orderList);
                                finalize.putExtra("Address", first.getAddressLine(0));
                                startActivity(finalize);
                            }
                        });
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(PlacePickerActivity.this,
                        getString(R.string.location_picker_dropped_marker_snippet_no_results), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationPlugin(@NonNull Style loadedMapStyle) {
// Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

// Get an instance of the component. Adding in LocationComponentOptions is also an optional
// parameter
            LocationComponent locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(this, loadedMapStyle);
            locationComponent.setLocationComponentEnabled(true);

// Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.setRenderMode(RenderMode.NORMAL);

        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    public void onButtonShowErrorWindowClick(View view, String popupText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(popupText)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
