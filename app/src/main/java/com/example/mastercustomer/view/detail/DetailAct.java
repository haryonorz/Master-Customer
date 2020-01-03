package com.example.mastercustomer.view.detail;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mastercustomer.R;
import com.example.mastercustomer.repository.model.BaseResponse;
import com.example.mastercustomer.repository.model.Customers;
import com.example.mastercustomer.utility.CameraUtils;
import com.example.mastercustomer.utility.GlobalFunc;
import com.example.mastercustomer.utility.GlobalVar;
import com.example.mastercustomer.utility.ImageUtils;
import com.example.mastercustomer.utility.LocationUtils;
import com.example.mastercustomer.view.detail_owner.DetailOwnerAct;
import com.example.mastercustomer.view.photo_preview.PhotoPreviewAct;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class DetailAct extends AppCompatActivity implements OnMapReadyCallback, DetailView{

    @BindView(R.id.image_view_photo_outlet) ImageView photoOutlet;
    @BindView(R.id.progress_bar_loading) ProgressBar loadingView;
    @BindView(R.id.text_view_outlet_code) TextView outletCodeText;
    @BindView(R.id.text_view_outlet_name) TextView outletNameText;
    @BindView(R.id.checbox_get_location) CheckBox getLocationCheckBox;
    @BindView(R.id.layout_map) View mapLayout;
    @BindView(R.id.edit_text_full_address_outlet) EditText outletAddressEditText;
    @BindView(R.id.edit_text_village_name) EditText outletVillageEditText;
    @BindView(R.id.edit_text_sub_district_name) EditText outletSubDistrictEditText;
    @BindView(R.id.edit_text_district_name) EditText outletDistrictEditText;
    @BindView(R.id.edit_text_province_name) EditText outletProvinceEditText;
    @BindView(R.id.spinner_grosir) Spinner outletGrosirSpinner;

    @BindView(R.id.text_view_detail_owner_outlet) TextView detailOwnerEditText;

    @BindView(R.id.button_save) Button saveButton;

    private ProgressDialog progressDialog;
    private DetailPresenter presenter;

    String custno, imageFileName, address, village, subDistrict, district, province, zip;

    private Customers customers;

    private GoogleMap mMap;

    private static final int MY_LOCATION_REQUEST_CODE =101;
    private static final int MY_CAMERA_REQUEST_CODE = 103;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastKnownLocation;
    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_detail);

        presenter = new DetailPresenter(this);
        ButterKnife.bind(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            custno = extras.getString("custno");
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Tunggu sebentar...");

        presenter.getCustomer(custno);

        setUpActionBar();
        interfaceMap();
    }

    @OnClick(R.id.image_view_photo_outlet)
    void photoOnClicked(){
        presenter.setDialog(this, this);
    }

    @OnCheckedChanged(R.id.checbox_get_location)
    void onGetLocationChecked(CompoundButton button, boolean isChecked) {
        if(isChecked){
            mapLayout.setVisibility(View.VISIBLE);

            outletAddressEditText.setText(address);
            outletVillageEditText.setText(village);
            outletSubDistrictEditText.setText(subDistrict);
            outletDistrictEditText.setText(district);
            outletProvinceEditText.setText(province);
        }
        else{
            mapLayout.setVisibility(View.GONE);

            outletAddressEditText.setText(customers.getCUSTADD1());
            outletVillageEditText.setText(customers.getKELURAHAN());
            outletSubDistrictEditText.setText(customers.getKECAMATAN());
            outletDistrictEditText.setText(customers.getKABUPATEN());
            outletProvinceEditText.setText(customers.getPROVINSI());
        }
    }

    @OnClick(R.id.text_view_detail_owner_outlet)
    void detailOwnerOnClicked(){
        Intent intent = new Intent(this, DetailOwnerAct.class);
        intent.putExtra("custno", custno);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    private void setUpActionBar() {
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void interfaceMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);
        mapFragment.getMapAsync(this);
        View mapView = mapFragment.getView();
        if ((mapView != null) && (mapView.findViewById(Integer.parseInt("1")) != null)){
            View locationButton = ((View)mapView.findViewById(Integer.parseInt("1"))
                    .getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            layoutParams.setMargins(0, 0, 30, 30);
        }
    }

    @Override
    public void onLoading() {
        progressDialog.show();
    }

    @Override
    public void onComplete() {
        progressDialog.dismiss();
    }

    @Override
    public void onPreviewPhoto() {
        Intent intent = new Intent(this, PhotoPreviewAct.class);
        intent.putExtra("photo_outlet", customers.getFOTO_T());
        startActivity(intent);
    }

    @Override
    public void checkCameraPermission() {
        if (CameraUtils.checkPermissions(this)) {
            openCamera();
        } else {
            requestCameraPermission();
        }
    }

    private void requestCameraPermission() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        openCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            dialog.cancel();
            CameraUtils.openSettings(this);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageFileName = customers.getKODECABANG() + "-" + custno + "-" + GlobalFunc.getDateNow().replace("-", "") + "_T";
        File photoFile = CameraUtils.getOutputMediaFile(imageFileName);

        if (photoFile != null) {
            GlobalVar.imageOutletURI = Uri.fromFile(photoFile);
        }

        Uri fileUri = CameraUtils.getOutputMediaFileUri(this, photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, MY_CAMERA_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                photoOutlet.setImageBitmap(ImageUtils.rotateImage(GlobalVar.imageOutletURI.getPath()));
            } else if (resultCode == RESULT_CANCELED) {
            } else {
                Toast.makeText(this, "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResponseSuccess(BaseResponse baseResponse) {
        if (baseResponse.getMessage().equals("Success")){
            customers = baseResponse.getCustomer();
            ImageUtils.setImage(this, this, loadingView, photoOutlet, customers.getFOTO_T());

            outletCodeText.setText(baseResponse.getCustomer().getCUSTNO());
            outletNameText.setText(baseResponse.getCustomer().getCUSTNAME());
            outletAddressEditText.setText(getLocationCheckBox.isChecked()? address : customers.getCUSTADD1());
            outletVillageEditText.setText(getLocationCheckBox.isChecked()? village : customers.getKELURAHAN());
            outletSubDistrictEditText.setText(getLocationCheckBox.isChecked()? subDistrict : customers.getKECAMATAN());
            outletDistrictEditText.setText(getLocationCheckBox.isChecked()? district : customers.getKABUPATEN());
            outletProvinceEditText.setText(getLocationCheckBox.isChecked()? province : customers.getPROVINSI());
            outletGrosirSpinner.setSelection(baseResponse.getCustomer().getOUTLET_EPPM() == null ||
                    baseResponse.getOwner().getOUTLET_EPPM().equals("")?
                    0 : (baseResponse.getOwner().getOUTLET_EPPM().equals("Y")? 1 : 2));
        }
    }

    @Override
    public void onResponseError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Log.e("signEror", message);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getLastDeviceLocation();
    }

    private void setMarker(boolean zoom){
        mMap.clear();
        mMap.getUiSettings().setAllGesturesEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        LatLng sMarker = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions()
                .position(sMarker)
                .title(address)
                .icon(getMarkerIcon(R.color.colorPrimary)));
        if (zoom) mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sMarker, 15));
    }

    private BitmapDescriptor getMarkerIcon(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(ContextCompat.getColor(this, color), hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }

    private void getLastDeviceLocation() {
        if (LocationUtils.checkPermissions(this)){
            if (LocationUtils.isLocationEnabled(this)){
                try {
                    fusedLocationProviderClient.getLastLocation()
                            .addOnCompleteListener(this, task -> {
                                lastKnownLocation = task.getResult();
                                if (lastKnownLocation == null)
                                    requestNewDeviceLocation();
                                else{
                                    latitude = lastKnownLocation.getLatitude();
                                    longitude = lastKnownLocation.getLongitude();
                                    if(LocationUtils.getCompleteAddressString(this, latitude, longitude)!=null){
                                        Address addressTemp = LocationUtils.getCompleteAddressString(this, latitude, longitude);
                                        String addressString = addressTemp.getAddressLine(0);
                                        Log.e("alamat", addressString);
                                        if (addressString.contains(", ")){
                                            address = addressString.split(", ")[0];
                                            village = addressString.split(", ")[1];
                                            subDistrict = addressString.split(", ")[2];
                                            district = addressString.split(", ")[3];
                                            zip = addressTemp.getPostalCode();
                                            province = addressString.split(", ")[4].replace(zip,"").trim();
                                        }
                                    }
                                    setMarker(true);
                                }
                            });
                } catch (SecurityException e)  {
                    Log.e("getDeviceLocation", e.getMessage());
                }
            } else {
                LocationUtils.settingLocation(this);
            }
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_LOCATION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_LOCATION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    getLastDeviceLocation();
                }
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @SuppressLint({"RestrictedApi", "MissingPermission"})
    private void requestNewDeviceLocation() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
            if(LocationUtils.getCompleteAddressString(DetailAct.this, latitude, longitude)!=null){
                Address addressTemp = LocationUtils.getCompleteAddressString(DetailAct.this, latitude, longitude);
                String addressString = addressTemp.getAddressLine(0);
                if (addressString.contains(", ")){
                    address = addressString.split(", ")[0];
                    village = addressString.split(", ")[1];
                    subDistrict = addressString.split(", ")[2];
                    district = addressString.split(", ")[3];
                    zip = addressTemp.getPostalCode();
                    province = addressString.split(", ")[4].replace(zip,"").trim();
                }
            }
            setMarker(true);
        }
    };
}
