package com.example.minidouyinapp.Bean;
import com.google.gson.annotations.SerializedName;

public class PostVideoResponse {
    @SerializedName("success") private Boolean success;
    @SerializedName("feeds") private Item feeds;
    public Boolean getSuccess(){
        return success;
    }
    public Item getFeeds(){
        return feeds;
    }
    public class Item{
        @SerializedName("student_id") private String studentId;
        @SerializedName("username") private String username;
        @SerializedName("image_url") private String  imageUrl;
        @SerializedName("video_url") private String  videoUrl;
        public String getStudentId(){
            return studentId;
        }
        public String getUsername(){
            return username;
        }
        public String getImageUrl(){
            return imageUrl;
        }
        public String getVideoUrl(){
            return videoUrl;
        }
    }
}
