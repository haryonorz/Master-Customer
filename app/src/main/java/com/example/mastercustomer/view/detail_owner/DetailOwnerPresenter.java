package com.example.mastercustomer.view.detail_owner;

import com.example.mastercustomer.repository.ApiClient;
import com.example.mastercustomer.repository.ApiInterface;
import com.example.mastercustomer.repository.model.BaseResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailOwnerPresenter {

    private final DetailOwnerView view;

    DetailOwnerPresenter(DetailOwnerView view){
        this.view = view;
    }

    void getOwner(String custno){
        view.onLoading();

        ApiInterface apiInterfance = ApiClient.getApiClient().create(ApiInterface.class);
        Call<BaseResponse> call = apiInterfance.getOwner(custno);
        call.enqueue(new Callback<BaseResponse>(){
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                view.onComplete();
                if (response.isSuccessful()){
                    view.onResponseSuccess(response.body(), "get");
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                view.onComplete();
                view.onResponseError(t.getLocalizedMessage());
            }
        });
    }

    void doUpdateOwner(String custno, String CCONTACT, String JENIS_KELAMIN, String CCITY, String TTL,
                     String AGAMA, String EMAIL, String HP1, String FAX){
        view.onLoading();

        ApiInterface apiInterfance = ApiClient.getApiClient().create(ApiInterface.class);
        Call<BaseResponse> call = apiInterfance.doUpdateOwner(custno, CCONTACT, JENIS_KELAMIN, CCITY,
                TTL, AGAMA, EMAIL, HP1, FAX);
        call.enqueue(new Callback<BaseResponse>(){
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                view.onComplete();
                if (response.isSuccessful()){
                    view.onResponseSuccess(response.body(), "update");
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                view.onComplete();
                view.onResponseError(t.getLocalizedMessage());
            }
        });
    }
}
