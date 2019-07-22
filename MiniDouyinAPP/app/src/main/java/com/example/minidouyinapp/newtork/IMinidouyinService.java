package com.example.minidouyinapp.newtork;

import com.example.minidouyinapp.Bean.FeedResponse;
import com.example.minidouyinapp.Bean.PostVideoResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;


public interface IMinidouyinService {
    @Multipart
    @POST("/mini_douyin/invoke/video")
    Call<PostVideoResponse> createVideo(
            @Query("student_id") String studentId,
            @Query("user_name") String userName,
            @Part MultipartBody.Part image,
            @Part MultipartBody.Part video);
    @GET("/mini_douyin/invoke/video")
    Call<FeedResponse> fetchFeed();
}
