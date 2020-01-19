package com.example.mastercustomer.view.home;

import com.example.mastercustomer.repository.ApiClient;
import com.example.mastercustomer.repository.ApiInterface;
import com.example.mastercustomer.repository.model.BaseResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomePresenter {

    private final HomeView view;

    HomePresenter(HomeView view){
        this.view = view;
    }

    void getListCustomers(String username, String keyword, String typePage, int page){
        if (typePage.equals("firstPage")) view.onLoading();

        ApiInterface apiInterfance = ApiClient.getApiClient().create(ApiInterface.class);
        Call<BaseResponse> call = apiInterfance.getListCustomers(username, keyword, page);
        call.enqueue(new Callback<BaseResponse>(){
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                view.onComplete();
                if (response.isSuccessful()){
                    if (response.body().getTotalPage() == 0){
                        view.onDataNotFound(keyword.equals("")? "all" : "search");
                    } else {
                        view.onResponseSuccess(response.body(), typePage);
                    }
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
