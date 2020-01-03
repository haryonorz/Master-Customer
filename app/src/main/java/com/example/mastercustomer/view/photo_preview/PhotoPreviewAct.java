package com.example.mastercustomer.view.photo_preview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.mastercustomer.R;
import com.example.mastercustomer.utility.ImageUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhotoPreviewAct extends AppCompatActivity {

    @BindView(R.id.progress_bar_loading) ProgressBar loadingView;
    @BindView(R.id.photo_view) ImageView photoView;

    String photoOutlet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_photo_preview);

        ButterKnife.bind(this);

        setupActionBar();

        Bundle extras = getIntent().getExtras();
        if (extras != null) photoOutlet = extras.getString("photo_outlet");

        ImageUtils.setImage(this, this, loadingView, photoView, photoOutlet);
    }

    private void setupActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
    }

}
