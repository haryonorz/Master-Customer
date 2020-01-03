package com.example.mastercustomer.view.home;

import com.example.mastercustomer.repository.model.BaseResponse;

public interface HomeView {

    void onLoading();
    void onComplete();
    void onResponseSuccess(BaseResponse baseResponse);
    void onResponseError(String message);
}
