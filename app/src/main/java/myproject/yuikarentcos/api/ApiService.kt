package myproject.yuikarentcos.api

import myproject.yuikarentcos.model.*
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("login")
    fun login(@Body user: User): Call<ApiResponse<User>>

    @GET("kostum")
    fun getKostum(): Call<ApiResponse<List<Kostum>>>
}