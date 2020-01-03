package com.example.mastercustomer.view.detail_owner;

import com.example.mastercustomer.repository.model.BaseResponse;

public interface DetailOwnerView {

    void onLoading();
    void onComplete();
    void onResponseSuccess(BaseResponse baseResponse, String type);
    void onResponseError(String message);
}
