package com.example.mastercustomer.view.sign_in;

import com.example.mastercustomer.repository.model.BaseResponse;

public interface SignInView {

    void onResponseSuccess(BaseResponse baseResponse);
    void onResponseError(String message);
}
