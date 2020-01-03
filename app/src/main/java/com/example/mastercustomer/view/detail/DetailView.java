package com.example.mastercustomer.view.detail;

import com.example.mastercustomer.repository.model.BaseResponse;

public interface DetailView {

    void onLoading();
    void onComplete();
    void onPreviewPhoto();
    void checkCameraPermission();
    void onResponseSuccess(BaseResponse baseResponse);
    void onResponseError(String message);
}
