package com.nurullah.scooterapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import com.nurullah.scooterapp.api.ScooterApi.Companion.HAMBURG_1
import com.nurullah.scooterapp.api.ScooterApi.Companion.HAMBURG_2
import com.nurullah.scooterapp.databinding.ActivityMainBinding
import com.nurullah.scooterapp.items.ExpandableHeaderItem
import com.nurullah.scooterapp.items.ScooterItem
import com.nurullah.scooterapp.items.ScooterItem.Companion.SPANS
import com.nurullah.scooterapp.models.Scooter.Companion.POOLING
import com.nurullah.scooterapp.models.Scooter.Companion.TAXI
import com.nurullah.scooterapp.viewmodels.MainViewModel
import com.google.android.gms.maps.model.LatLng

import com.google.android.gms.maps.model.Marker

import android.content.Intent
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private val width by lazy { resources.displayMetrics.widthPixels }
    private val height by lazy { resources.displayMetrics.heightPixels }
    private val mapPadding by lazy { (width * 0.12).toInt() }
    private val markerIcon by lazy { BitmapDescriptorFactory.fromResource(com.nurullah.scooterapp.R.drawable.taxi_marker) }
    private val markerIconBin by lazy { BitmapDescriptorFactory.fromResource(com.nurullah.scooterapp.R.drawable.recycle_bin) }

    private val binding by lazy { DataBindingUtil.setContentView<ActivityMainBinding>(this, com.nurullah.scooterapp.R.layout.activity_main) }
    private lateinit var viewModel: MainViewModel
    private val adapter = GroupAdapter<ViewHolder>().apply { spanCount = SPANS }
    private val taxiSection by lazy { Section() }
    private val poolingSection by lazy { Section() }
    private val bottomSheetBehavior by lazy { BottomSheetBehavior.from(binding.bottomSheet.container) }
    private val hamburgBounds by lazy {
        LatLngBounds.Builder().include(HAMBURG_1)
                .include(HAMBURG_2)
                .build()
    }
    private var auth: FirebaseAuth?= FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        binding.viewModel = viewModel

        initRecyclerView()
        subscribeToChanges()

        binding.profile.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.qrCode.setOnClickListener {
            if (auth!!.currentUser !=null) {
                val intent = Intent(this, QRCodeActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this,"Lütfen Hesabınıza Giriş Yapın...", Toast.LENGTH_SHORT).show()
            }
        }

        val mapFragment = supportFragmentManager.findFragmentById(com.nurullah.scooterapp.R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun subscribeToChanges() {
        viewModel.initialData().observe(this, Observer { vehicles -> showData(vehicles) })
        viewModel.vehicleUpdates().observe(this, Observer { taxis -> onVehicleSelection(taxis) })
    }

    private fun initRecyclerView() {
        val layoutManager = GridLayoutManager(this, adapter.spanCount)
        layoutManager.spanSizeLookup = adapter.spanSizeLookup

        with(binding.bottomSheet.recyclerView) {
            this.layoutManager = layoutManager
            adapter = this@MainActivity.adapter
        }
        adapter.setOnItemClickListener(viewModel)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isMapToolbarEnabled = false

        // Move the camera to Hamburg, Germany
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(hamburgBounds, width, height, mapPadding))
        viewModel.loadData()
    }

    private fun showData(vehiclesMap: Map<String, List<ScooterItem>>) {
        vehiclesMap[TAXI]?.let { taxis ->
            val header = ExpandableHeaderItem("Scooter", taxis.size)
            val expandableGroup = ExpandableGroup(header, true)
            taxiSection.update(taxis)
            expandableGroup.add(taxiSection)
            adapter.add(expandableGroup)
        }

        vehiclesMap[POOLING]?.let { pool ->
            val header = ExpandableHeaderItem("Akıllı Çöp", pool.size)
            val expandableGroup = ExpandableGroup(header, true)
            poolingSection.update(pool)
            expandableGroup.add(poolingSection)
            adapter.add(expandableGroup)
        }

        addMarkers(vehiclesMap.values.flatten())
    }

    private fun onVehicleSelection(data: Triple<ScooterItem, List<ScooterItem>, List<ScooterItem>>) {
        val (toggledItem, taxis, pooling) = data
        taxiSection.update(taxis)
        poolingSection.update(pooling)
        addMarkers(taxis + pooling)

        if (toggledItem.isSelected) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(toggledItem.scooter.coordinate, 12f))
        } else {
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(hamburgBounds, width, height, mapPadding))
        }
    }

    private fun addMarkers(pooling: List<ScooterItem>) {
        map.clear()
        pooling.asSequence()
                .map { item ->
                    val rnds = "%"+(0..100).random()+" Şarj"
                    val rnds1 = ((0..100).random()).toString()+" Km"
                    if (item.scooter.fleetType.equals("TAXI")){
                        MarkerOptions()
                            .position(item.scooter.coordinate)
                            .title("SCOOTER")
                            .snippet(rnds)
                            .icon(markerIcon)
                            .zIndex(0.0f)
                    } else {
                        MarkerOptions()
                            .position(item.scooter.coordinate)
                            .title("Akıllı Çöp")
                            .snippet(rnds1)
                            .icon(markerIconBin)
                            .zIndex(0.0f)
                    }

                }
                .forEach { map.addMarker(it) }
        UpdateCurrentLocation()
    }
    private fun UpdateCurrentLocation() {
        val latLng = LatLng(39.9777282,32.6735791)
        val marker: Marker = map.addMarker(
            MarkerOptions()
                .position(latLng)
                .title("Konumunuz")
                .snippet("Çöpleri Toplamaya Başla")
                .icon(
                    BitmapDescriptorFactory
                        .fromResource(com.nurullah.scooterapp.R.drawable.location)
                )
        )
        marker.showInfoWindow()
    }
}
