package com.example.mastercustomer.repository;

import com.example.mastercustomer.repository.model.BaseResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterfance {

    @FormUrlEncoded
    @POST("doLogin.php")
    Call<BaseResponse> doLogin(
            @Field("username") String username,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("listCustomers.php")
    Call<BaseResponse> getListCustomers(
            @Field("username") String username,
            @Field("keyword") String keyword
    );

    @FormUrlEncoded
    @POST("customer.php")
    Call<BaseResponse> getCustomer(
            @Field("custno") String custno
    );

    @FormUrlEncoded
    @POST("owner.php")
    Call<BaseResponse> getOwner(
            @Field("custno") String custno
    );

    @FormUrlEncoded
    @POST("doUpdateOwner.php")
    Call<BaseResponse> doUpdateOwner(
            @Field("custno") String custno,
            @Field("CCONTACT") String CCONTACT,
            @Field("JENIS_KELAMIN") String JENIS_KELAMIN,
            @Field("CCITY") String CCITY,
            @Field("TTL") String TTL,
            @Field("AGAMA") String AGAMA,
            @Field("EMAIL") String EMAIL,
            @Field("HP1") String HP1,
            @Field("FAX") String FAX
    );
}
