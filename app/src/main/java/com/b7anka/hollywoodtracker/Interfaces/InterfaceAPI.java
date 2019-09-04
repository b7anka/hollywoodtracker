package com.b7anka.hollywoodtracker.Interfaces;

import com.b7anka.hollywoodtracker.Model.APIResponse;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface InterfaceAPI
{

    @POST("deletedata.php")
    Call<APIResponse> deleteUserData(@Body RequestBody params);

    @POST("savedata.php")
    Call<APIResponse> saveUserData(@Body RequestBody params);

    @GET("getdata.php")
    Call<APIResponse> getUserData(@Query("idUser") int id, @Query("type") String type, @Query("lang") String lang, @Query("time") long timeStamp);

    @GET("rewardedvideos.php")
    Call<APIResponse> getUserRewardedVideos(@Query("idUser") int id, @Query("isSaving") boolean isSaving, @Query("value") int value, @Query("lang") String lang);

    @GET("checkuser.php")
    Call<APIResponse> checkUser(@Query("username") String username, @Query("lang") String lang);

    @POST("updatedata.php")
    Call<APIResponse> updateUserData(@Body RequestBody params);

    @POST("iforgot.php")
    Call<APIResponse> recoverUserPassword(@Body RequestBody params);

    @POST("login.php")
    Call<APIResponse> postUserLogin(@Body RequestBody params);

    @POST("loginwithgoogle.php")
    Call<APIResponse> postUserLoginWithGoogle(@Body RequestBody params);

    @POST("managegoogleaccount.php")
    Call<APIResponse> updateGoogleAccountState(@Body RequestBody params);

    @POST("register.php")
    Call<APIResponse> postUserRegister(@Body RequestBody params);

    @POST("registerwithgoogle.php")
    Call<APIResponse> postUserRegisterWithGoogle(@Body RequestBody params);

    @POST("deleteuser.php")
    Call<APIResponse> deleteUserAccount(@Body RequestBody params);

    @POST("savethumbnail.php")
    Call<APIResponse> postThumbnail(@Body RequestBody params);

    @POST("bugreport.php")
    Call<APIResponse> postBugReport(@Body RequestBody params);

    @POST("premium.php")
    Call<APIResponse> postPremium(@Body RequestBody params);
}
