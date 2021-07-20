package com.techdev.dcart.APICalls;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Api {

    //the base URL for our API
    //make sure you are not using localhost
    //find the ip use this --> ipconfig command
   // String BASE_URL = "http://192.168.43.109/d_cart_app/";
    //String BASE_URL = "http://192.168.43.109/e_commerce_app/";
    public static String BASE_URL = "http://growtechnologies.net/vamajobs/mobapp/";

    //this is our multipart request
    //we have two parameters on is name and other one is description

    @Multipart
    @POST("ImageUpload.php")
    Call<Respond> uploadImage(@Part MultipartBody.Part file, @Part("id_user") RequestBody idUser);

//    @Multipart
//    @POST("Check_image.php")
//    Call<Respond> uploadImage(@Part MultipartBody.Part file, @Part("id_user") RequestBody idUser);

    //@Multipart
    //    @POST("ImageUpload.php")
    //    Call<Respond> uploadImage(@Part MultipartBody.Part file, @Part("id_user") RequestBody idUser);


    ////////////////06-07-2021:
    //this is our multipart request
    //we have two parameters on is name and other one is description
    @Multipart
    @POST("Api.php?apicall=upload")
    Call<ImageResponse> todayImage(@Part("image\"; filename=\"myfile.jpg\" ") RequestBody file, @Part("desc") RequestBody desc);

    //////////////////

}
