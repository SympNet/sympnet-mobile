package com.sympnet.app.api;

import com.sympnet.app.api.models.AuthResponse;
import com.sympnet.app.api.models.LoginRequest;
import com.sympnet.app.api.models.RegisterPatientRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApiService {
    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("auth/register/patient")
    Call<AuthResponse> registerPatient(@Body RegisterPatientRequest request);
}