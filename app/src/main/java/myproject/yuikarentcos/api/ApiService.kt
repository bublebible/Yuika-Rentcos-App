package myproject.yuikarentcos.api


import retrofit2.Call
import retrofit2.http.GET

interface ApiService {// Contoh endpoint dummy
    @GET("test")
    fun testConnection(): Call<Void>
}