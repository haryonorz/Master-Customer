package com.example.mastercustomer.repository;

import com.example.mastercustomer.repository.model.BaseResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {

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
            @Field("keyword") String keyword,
            @Field("page") int page
    );

    @FormUrlEncoded
    @POST("customer.php")
    Call<BaseResponse> getCustomer(
            @Field("custno") String custno
    );

    @Multipart
    @POST("uploadImage.php")
    Call<BaseResponse> uploadImage(
            @Part MultipartBody.Part file,
            @Part("name") RequestBody name
    );

    @FormUrlEncoded
    @POST("doUpdateCustomer.php")
    Call<BaseResponse> doUpdateCustomer(
            @Field("custno") String custno,
            @Field("CUSTADD1") String CUSTADD1,
            @Field("KELURAHAN") String KELURAHAN,
            @Field("KECAMATAN") String KECAMATAN,
            @Field("KABUPATEN") String KABUPATEN,
            @Field("PROVINSI") String PROVINSI,
            @Field("OUTLET_EPM") String OUTLET_EPM,
            @Field("LA") String LA,
            @Field("LG") String LG,
            @Field("NOTE") String NOTE
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
