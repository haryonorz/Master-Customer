package com.example.mastercustomer.view.home;

import com.example.mastercustomer.repository.ApiClient;
import com.example.mastercustomer.repository.ApiInterfance;
import com.example.mastercustomer.repository.model.BaseResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomePresenter {

    private final HomeView view;

    HomePresenter(HomeView view){
        this.view = view;
    }

    void getListCustomers(String username, String keyword){
        view.onLoading();

        ApiInterfance apiInterfance = ApiClient.getApiClient().create(ApiInterfance.class);
        Call<BaseResponse> call = apiInterfance.getListCustomers(username, keyword);
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
}
