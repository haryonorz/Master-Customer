package com.example.mastercustomer.view.detail;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mastercustomer.R;
import com.example.mastercustomer.repository.ApiClient;
import com.example.mastercustomer.repository.ApiInterfance;
import com.example.mastercustomer.repository.model.BaseResponse;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailPresenter {

    private final DetailView view;

    DetailPresenter(DetailView view){
        this.view = view;
    }

    void getCustomer(String custno){
        view.onLoading();

        ApiInterfance apiInterfance = ApiClient.getApiClient().create(ApiInterfance.class);
        Call<BaseResponse> call = apiInterfance.getCustomer(custno);
        call.enqueue(new Callback<BaseResponse>(){
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                view.onComplete();
                if (response.isSuccessful()){
                    view.onResponseSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                view.onComplete();
                view.onResponseError(t.getLocalizedMessage());
            }
        });
    }

    void setDialog(Activity activity, Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.setOnKeyListener((dialog1, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dialog1.cancel();
                return true;
            }
            return false;
        });
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.color.colorTransparant);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_photo_outlet, null);
        dialog.setContentView(view);
        ListView list = dialog.findViewById(R.id.list_view_dialog);
        String[] items = {"Lihat Foto", "Kamera"};
        list.setAdapter(new ArrayAdapter<>(context, R.layout.view_item_photo_outlet, items));
        list.setOnItemClickListener((parent, view1, position, id) -> {
            dialog.cancel();
            if(position==0)
                this.view.onPreviewPhoto();
            else
                this.view.checkCameraPermission();

        });
        TextView cancelButton = dialog.findViewById(R.id.text_view_cancel);
        cancelButton.setOnClickListener(view12 -> dialog.cancel());
        dialog.show();
    }
}
