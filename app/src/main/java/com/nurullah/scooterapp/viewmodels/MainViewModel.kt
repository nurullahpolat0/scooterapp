package com.nurullah.scooterapp.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xwray.groupie.Item
import com.xwray.groupie.OnItemClickListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import com.nurullah.scooterapp.api.VehiclesRepository
import com.nurullah.scooterapp.helpers.plusAssign
import com.nurullah.scooterapp.items.ScooterItem
import com.nurullah.scooterapp.models.Scooter.Companion.POOLING
import com.nurullah.scooterapp.models.Scooter.Companion.TAXI

class MainViewModel : ViewModel(), OnItemClickListener {

    private val repository by lazy { VehiclesRepository() }
    private val compositeDisposable by lazy { CompositeDisposable() }

    private val vehicles by lazy { HashMap<String, List<ScooterItem>>() }
    private val groupedVehicles by lazy { MutableLiveData<Map<String, List<ScooterItem>>>() }
    private val vehicleUpdates by lazy { MutableLiveData<Triple<ScooterItem, List<ScooterItem>, List<ScooterItem>>>() }

    fun loadData() {
        compositeDisposable += repository.getVehicles()
            .map { data -> data.scooters.map { ScooterItem(it) }.groupBy { it.scooter.fleetType } }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { data ->
                vehicles.clear()
                vehicles.putAll(data)
                groupedVehicles.value = data
            }
    }

    fun initialData(): LiveData<Map<String, List<ScooterItem>>> = groupedVehicles

    fun vehicleUpdates(): MutableLiveData<Triple<ScooterItem, List<ScooterItem>, List<ScooterItem>>> = vehicleUpdates

    override fun onItemClick(item: Item<*>, view: View) {
        if (item is ScooterItem) {
            val toggledItem = item.copy(isSelected = !item.isSelected)
            val taxis =
                vehicles[TAXI]?.map { vehicle -> vehicle.copy(isSelected = (vehicle == item) && toggledItem.isSelected) }
            val pooling =
                vehicles[POOLING]?.map { vehicle -> vehicle.copy(isSelected = (vehicle == item) && toggledItem.isSelected) }
            vehicleUpdates.value = Triple(toggledItem, taxis.orEmpty(), pooling.orEmpty())
        }
    }
}