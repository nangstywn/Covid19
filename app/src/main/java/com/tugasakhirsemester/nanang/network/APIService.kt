package com.tugasakhirsemester.nanang.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface APIService {
    @GET("summary") // GET data dari directory summary
    fun getAllCountries(): Call<AllCountries> // membuat metode guna menentukan struktur JSON yang ingin diajak bertransaksi
}

interface InfoService{
    @GET // Karena directory data yg di GET dinamis maka tidak disebutkan namanya
    fun getInfoService(@Url url: String?): Call<List<InfoCountry>> // Pada constructor metode, dibuatkan variabel dengan
    // anotasi @Url untuk directory dinamisnya
}