package com.nurullah.scooterapp.models

import com.google.gson.annotations.SerializedName

class ScootersResponse(@SerializedName("poiList") val scooters: List<Scooter>)