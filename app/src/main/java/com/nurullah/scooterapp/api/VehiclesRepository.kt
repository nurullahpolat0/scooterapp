package com.nurullah.scooterapp.api

import io.reactivex.Observable
import com.nurullah.scooterapp.models.ScootersResponse

class VehiclesRepository {

    fun getVehicles(): Observable<ScootersResponse> {
        val service = RetrofitBuilder.createService(ScooterApi::class.java)
        return service.getCars()
    }
}