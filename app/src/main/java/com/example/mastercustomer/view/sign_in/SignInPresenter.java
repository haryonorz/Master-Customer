package com.example.mastercustomer.view.sign_in;

import com.example.mastercustomer.repository.ApiClient;
import com.example.mastercustomer.repository.ApiInterfance;
import com.example.mastercustomer.repository.model.BaseResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInPresenter {

    private final SignInView view;

    SignInPresenter(SignInView view){
        this.view = view;
    }

    void doLogin(String username, String password){
        ApiInterfance apiInterfance = ApiClient.getApiClient().create(ApiInterfance.class);
        Call<BaseResponse> call = apiInterfance.doLogin(username, password);
        call.enqueue(new Callback<BaseResponse>(){
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful()){
                    view.onResponseSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                view.onResponseError(t.getLocalizedMessage());
            }
        });
    }
}
