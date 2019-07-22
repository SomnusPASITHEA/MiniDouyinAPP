package com.example.minidouyinapp.Bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FeedResponse {

    @SerializedName("success") private Boolean success;
    @SerializedName("feeds") private List<Feed> feeds;
    public Boolean getSuccess(){
        return success;
    }
    public List<Feed> getFeeds(){
        return feeds;
    }
}
