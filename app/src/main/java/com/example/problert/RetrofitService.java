package com.example.problert;

import java.util.HashMap;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RetrofitService {

    String URL = "http://donote.co:3000/api/v1/issue";

    /**
     * POST 방식, 주소는 위들과 같음.
     * @FieldMap HashMap<String, Object> param :
     * Field 형식을 통해 넘겨주는 값들이 여러 개일 때 FieldMap을 사용함.
     * Retrofit에서는 Map 보다는 HashMap 권장.
     * @FormUrlEncoded Field 형식 사용 시 Form이 Encoding 되어야 하기 때문에 사용하는 어노테이션
     * Field 형식은 POST 방식에서만 사용가능.
     * @param param 요청에 필요한 값들.
     * @return Data 객체를 JSON 형태로 반환.
     */
    @FormUrlEncoded
    @POST("/")
    Call<Data> postData(@FieldMap HashMap<String, Object> param);

    @Multipart
    @POST("/images/upload")
    Call<Data> uploadImage(@Part MultipartBody.Part image);

}
