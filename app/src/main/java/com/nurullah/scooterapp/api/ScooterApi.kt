package com.nurullah.scooterapp.api

import com.google.android.gms.maps.model.LatLng
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import com.nurullah.scooterapp.models.ScootersResponse

interface ScooterApi {

    @GET("/")
    fun getCars(
        @Query(P1_LAT) p1Lat: Double = HAMBURG_1.latitude,
        @Query(P1_LON) p1Lon: Double = HAMBURG_1.longitude,
        @Query(P2_LAT) p2Lat: Double = HAMBURG_2.latitude,
        @Query(P2_LON) p2Lon: Double = HAMBURG_2.longitude
    ): Observable<ScootersResponse>

    companion object {
        const val P1_LAT = "p1Lat"
        const val P1_LON = "p1Lon"
        const val P2_LAT = "p2Lat"
        const val P2_LON = "p2Lon"
        val HAMBURG_1 = LatLng(39.9777282,32.6735791)
        val HAMBURG_2 = LatLng(39.8787961,32.8714483)
    }
}