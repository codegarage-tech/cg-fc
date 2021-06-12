package com.rc.facecase.retrofit;

import com.rc.facecase.model.AppUser;
import com.rc.facecase.model.Category;
import com.rc.facecase.model.ParamsAppUser;
import com.rc.facecase.model.ParamsUpdateUserHistory;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;


/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public interface APIInterface {

    @POST("addUser")
    Call<APIResponse<List<AppUser>>> apiAddUser(@Body ParamsAppUser paramToken);


    @GET("additionaLists/{user_id}")
    Call<APIResponse<List<Category>>> apiGetAdditionalCategoryList(@Path("user_id") String user_id);

    @GET("lists/{user_id}")
    Call<APIResponse<List<Category>>> apiGetCategoryList(@Path("user_id") String user_id);

    @POST("updateUserBrowsingHistory")
    Call<APIResponse> apiUpdateUserBrowsingHistory(@Body ParamsUpdateUserHistory updateUserHistory);

}