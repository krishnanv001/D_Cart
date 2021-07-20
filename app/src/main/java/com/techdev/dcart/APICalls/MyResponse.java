package com.techdev.dcart.APICalls;

import com.google.gson.annotations.SerializedName;

public class MyResponse {

    // variable name should be same as in the json response from php
    @SerializedName("success")
    private  boolean success;
    @SerializedName("message")
    private String message;

   public String getMessage() {
        return message;
    }

   public  boolean getSuccess() {
        return success;
    }

}
