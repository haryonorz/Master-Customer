package com.example.mastercustomer.view.detail;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class DetailAct extends AppCompatActivity implements OnMapReadyCallback, LocationListener, DetailView{

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
    @BindView(R.id.edit_text_note) EditText outletNoteEditText;

    @BindView(R.id.text_view_detail_owner_outlet) TextView detailOwnerEditText;

    @BindView(R.id.button_save) Button saveButton;

    private ProgressDialog progressDialog;
    private DetailPresenter presenter;

    String custno, imageFileName, address, village, subDistrict, district, province, zip;

    private Customers customers;

    private GoogleMap mMap;

    private static final int MY_LOCATION_REQUEST_CODE =101;
    private static final int MY_CAMERA_REQUEST_CODE = 103;

    public static final int LOCATION_UPDATE_MIN_DISTANCE = 10;
    public static final int LOCATION_UPDATE_MIN_TIME = 5000;

    private Location location;
    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_detail);

        presenter = new DetailPresenter(this);
        ButterKnife.bind(this);

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
        presenter.setDialog(this);
    }

    @OnCheckedChanged(R.id.checbox_get_location)
    void onGetLocationChecked(CompoundButton button, boolean isChecked) {
        if(isChecked){
            mapLayout.setVisibility(View.VISIBLE);

            outletAddressEditText.setText(address);
        }
        else{
            mapLayout.setVisibility(View.GONE);

            outletAddressEditText.setText(customers.getCUSTADD1());
        }
    }

    @OnClick(R.id.text_view_detail_owner_outlet)
    void detailOwnerOnClicked(){
        Intent intent = new Intent(this, DetailOwnerAct.class);
        intent.putExtra("custno", custno);
        startActivity(intent);
    }

    @OnClick(R.id.button_save)
    void saveClicked(){
        if (getLocationCheckBox.isChecked() && !outletGrosirSpinner.getSelectedItem().toString().equals("Select grosir")){
            new android.app.AlertDialog.Builder(this)
                    .setMessage("Apakah anda yakin ingin mengupdate data outlet dan sudah mengupdate data owner?")
                    .setPositiveButton("Ya", (dialog, which) -> {
                        presenter.doUpdateCustomer(custno, outletAddressEditText.getText().toString(),
                                outletVillageEditText.getText().toString(), outletSubDistrictEditText.getText().toString(),
                                outletDistrictEditText.getText().toString(), outletProvinceEditText.getText().toString(),
                                outletGrosirSpinner.getSelectedItem().toString(), String.valueOf(location.getLatitude()),
                                String.valueOf(location.getLongitude()), outletNoteEditText.getText().toString());
                        dialog.dismiss();
                    })
                    .setNegativeButton("Tidak", (dialog, which) -> dialog.dismiss())
                    .show();
        } else {
            Toast.makeText(this,
                    "Location outlet belum didapatkan\natau type grosir belum dipilih." +
                            "\nSilahkan centang\n\"Get Location Outlet\"\nuntuk mendapatkan location",
                    Toast.LENGTH_LONG).show();
        }
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

    @Override
    public void onUpdateSuccess(BaseResponse response) {
        if (response.getMessage().equals("Success")){
            Toast.makeText(this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Data gagal disimpan", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestCameraPermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()){
                            openCamera();
                        } else if (report.isAnyPermissionPermanentlyDenied()){
                            showSettingsDialog();
                        } else {
                            requestCameraPermission();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
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
        imageFileName = customers.getKODECABANG() + "_" + custno + "_MASTER_";
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
                if (GlobalVar.imageOutletURI != null){
                    progressDialog = new ProgressDialog(this);
                    progressDialog.setMessage("Tunggu sebentar...");


                    File actualImage = new File(GlobalVar.imageOutletURI.getPath());

                    File compressedImage = null;
                    try {
                        compressedImage = new Compressor(this)
                                .setMaxWidth(640)
                                .setMaxHeight(480)
                                .setQuality(75)
                                .setCompressFormat(Bitmap.CompressFormat.WEBP)
                                .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                        Environment.DIRECTORY_PICTURES).getAbsolutePath())
                                .compressToFile(actualImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), compressedImage);
                    MultipartBody.Part fileImage = MultipartBody.Part.createFormData("file", compressedImage.getName(), mFile);
                    RequestBody fileName = RequestBody.create(MediaType.parse("text/plain"), compressedImage.getName());
                    presenter.uploadImage(fileImage, fileName);

                    photoOutlet.setImageBitmap(ImageUtils.rotateImage(compressedImage.getAbsolutePath()));
                }
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
            outletGrosirSpinner.setSelection(baseResponse.getCustomer().getOUTLET_EPPM() == null ||
                    baseResponse.getOwner().getOUTLET_EPPM().equals("")?
                    0 : (baseResponse.getOwner().getOUTLET_EPPM().equals("Y")? 1 : 2));
            outletNoteEditText.setText(baseResponse.getCustomer().getNOTE());
        }
    }

    @Override
    public void onResponseError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Log.e("DetailAct", message);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        checkLocationPermission();
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
        outletVillageEditText.setText(village);
        outletSubDistrictEditText.setText(subDistrict);
        outletDistrictEditText.setText(district);
        outletProvinceEditText.setText(province);
    }

    private BitmapDescriptor getMarkerIcon(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(ContextCompat.getColor(this, color), hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        if (location != null){
            latitude = location.getLatitude();
            longitude = location.getLongitude();
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
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void getDeviceLocation(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            if(!mMap.isMyLocationEnabled()) mMap.setMyLocationEnabled(true);

            LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
            if(lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                lm.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        LOCATION_UPDATE_MIN_TIME,
                        LOCATION_UPDATE_MIN_DISTANCE, this);
                location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            } else if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                lm.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        LOCATION_UPDATE_MIN_TIME,
                        LOCATION_UPDATE_MIN_DISTANCE,
                        this);
                location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }

            if (location != null){
                latitude = location.getLatitude();
                longitude = location.getLongitude();
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
        }
    }

    private void checkLocationPermission() {
        if (LocationUtils.checkPermissions(this)) {
            if (LocationUtils.isLocationEnabled(this)) {
                getDeviceLocation();
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

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    getDeviceLocation();
                }
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
