package com.example.mastercustomer.view.home;

import com.example.mastercustomer.repository.model.BaseResponse;

public interface HomeView {

    void onLoading();
    void onComplete();
    void onDataNotFound(String type);
    void onResponseSuccess(BaseResponse baseResponse, String type);
    void onResponseError(String message);
}
