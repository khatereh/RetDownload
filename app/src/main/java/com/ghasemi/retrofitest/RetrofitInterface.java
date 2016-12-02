package com.ghasemi.retrofitest;
 
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
 
public interface RetrofitInterface {
//    for downloading we use GET method. For downloading large files we need to
//    add @Streaming annotation to Retrofit Interface so that it does not load the
//    complete file into memory. The endpoint is files/Node-Android-Chat.zip.
    @GET("files/Node-Android-Chat.zip")
    @Streaming
    Call<ResponseBody> downloadFile();
}