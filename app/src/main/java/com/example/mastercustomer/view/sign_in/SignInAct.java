package com.example.mastercustomer.view.sign_in;

import android.Manifest;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.widget.Toast;

import com.example.mastercustomer.R;
import com.example.mastercustomer.repository.model.BaseResponse;
import com.example.mastercustomer.utility.DirectoryUtils;
import com.example.mastercustomer.utility.SessionManager;
import com.example.mastercustomer.view.home.HomeAct;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class SignInAct extends AppCompatActivity implements SignInView{

    @BindView(R.id.edit_text_username) TextInputEditText usernameEditText;
    @BindView(R.id.edit_text_password) TextInputEditText passwordEditText;

    private SessionManager sessionManager;
    private SignInPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_sign_in);

        sessionManager = new SessionManager(this);
        presenter = new SignInPresenter(this);

        ButterKnife.bind(this);

        checkDirectoryPermission();
    }

    @OnClick(R.id.button_sign_in)
    void onSignInClicked(){
        if (Objects.requireNonNull(usernameEditText.getText()).length() == 0 ||
                Objects.requireNonNull(passwordEditText.getText()).length() == 0)
            Toast.makeText(this, "Maaf, anda belum memasukkan username atau password", Toast.LENGTH_SHORT).show();
        else
            presenter.doLogin(Objects.requireNonNull(usernameEditText.getText().toString()),
                    Objects.requireNonNull(passwordEditText.getText().toString()));

    }

    private void checkDirectoryPermission() {
        if (DirectoryUtils.checkPermissions(getApplicationContext())) {
            DirectoryUtils.createDirectory();
        } else {
            requestDirectoryPermission();
        }
    }

    private void requestDirectoryPermission() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        DirectoryUtils.createDirectory();
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
            DirectoryUtils.openSettings(this);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }


    @Override
    public void onResponseSuccess(BaseResponse baseResponse) {
        if (baseResponse.getMessage().equals("Success")){
            sessionManager.createSession(Objects.requireNonNull(usernameEditText.getText()).toString(),
                    Objects.requireNonNull(passwordEditText.getText()).toString());
            startActivity(new Intent(this, HomeAct.class));
            finish();
        }
        else{
            Toast.makeText(this, "Username atau password anda salah", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResponseError(String message) {
        usernameEditText.setText(null);
        passwordEditText.setText(null);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
