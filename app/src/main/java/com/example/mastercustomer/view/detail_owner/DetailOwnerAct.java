package com.example.mastercustomer.view.detail_owner;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mastercustomer.R;
import com.example.mastercustomer.repository.model.BaseResponse;
import com.example.mastercustomer.utility.GlobalVar;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailOwnerAct extends AppCompatActivity implements DetailOwnerView{

    @BindView(R.id.edit_text_owner_name) EditText ownerNameText;
    @BindView(R.id.spinner_gender) Spinner ownerGenderSpinner;
    @BindView(R.id.edit_text_city) EditText ownerCityText;
    @BindView(R.id.edit_text_birtdate) EditText ownerBirtdateText;
    @BindView(R.id.edit_text_religion) EditText ownerReligionText;
    @BindView(R.id.edit_text_email) EditText ownerEmailText;
    @BindView(R.id.edit_text_handphone) EditText ownerHandphoneText;
    @BindView(R.id.edit_text_fax) EditText ownerFaxText;

    private String custno;

    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private ProgressDialog progressDialog;
    private DetailOwnerPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_detail_owner);

        presenter = new DetailOwnerPresenter(this);
        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            custno = extras.getString("custno");
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Tunggu sebentar...");

        presenter.getOwner(custno);

        setUpActionBar();
    }

    @OnClick(R.id.edit_text_birtdate)
    void onBirthdayClicked(){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener,
                year,month,day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    @OnClick(R.id.button_save)
    void onSaveClicked(){
        if (ownerNameText.getText().toString().equals("") || ownerGenderSpinner.getSelectedItem().toString().equals("Select Gender") ||
                ownerCityText.getText().toString().equals("") || ownerBirtdateText.getText().toString().equals("") ||
                ownerReligionText.getText().toString().equals("") || ownerEmailText.getText().toString().equals("") ||
                ownerHandphoneText.getText().toString().equals("") || ownerFaxText.getText().toString().equals("")){
            Toast.makeText(this, "Maaf, Anda belum mengisi semua dari bidang form", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Tunggu sebentar...");

            presenter.doUpdateOwner(custno, ownerNameText.getText().toString(),
                    ownerGenderSpinner.getSelectedItem().toString().equals("Men")? "M" : "W",
                    ownerCityText.getText().toString(), ownerBirtdateText.getText().toString(),
                    ownerReligionText.getText().toString(), ownerEmailText.getText().toString(),
                    ownerHandphoneText.getText().toString(), ownerFaxText.getText().toString());
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

    @Override
    public void onLoading() {
        progressDialog.show();
    }

    @Override
    public void onComplete() {
        progressDialog.dismiss();
    }

    @Override
    public void onResponseSuccess(BaseResponse baseResponse, String type) {
        if (baseResponse.getMessage().equals("Success")){
            if (type.equals("get")){
                ownerNameText.setText(baseResponse.getOwner().getCCONTACT());
                ownerGenderSpinner.setSelection(baseResponse.getOwner().getJENIS_KELAMIN() == null ||
                        baseResponse.getOwner().getJENIS_KELAMIN().equals("")?
                        0 : (baseResponse.getOwner().getJENIS_KELAMIN().equals("M")? 1 : 2));
                ownerCityText.setText(baseResponse.getOwner().getCCITY());
                ownerBirtdateText.setText(baseResponse.getOwner().getTTL());
                ownerReligionText.setText(baseResponse.getOwner().getAGAMA());
                ownerEmailText.setText(baseResponse.getOwner().getEMAIL());
                ownerHandphoneText.setText(baseResponse.getOwner().getHP1());
                ownerFaxText.setText(baseResponse.getOwner().getFAX());

                mDateSetListener = (datePicker, year, month, day) -> {
                    month = month + 1;
                    String date =  String.valueOf(year).concat("-")
                            .concat(String.format("%02d", month)).concat("-")
                            .concat(String.format("%02d", day));
                    ownerBirtdateText.setText(date);
                };
            } else {
                Toast.makeText(this, "Saved successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public void onResponseError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Log.e("signEror", message);
    }
}
