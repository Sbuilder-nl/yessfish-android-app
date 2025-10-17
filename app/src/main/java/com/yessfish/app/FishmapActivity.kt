package com.yessfish.app

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL

/**
 * Native Google Maps Activity for YessFish Fishmap
 * Shows fishing spots from waterapi.nl database
 */
class FishmapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private val markers = mutableListOf<Marker>()
    private var currentMapType = GoogleMap.MAP_TYPE_NORMAL
    private val LOCATION_PERMISSION_REQUEST = 100

    // UI Components
    private lateinit var btnBack: ImageButton
    private lateinit var btnRefresh: ImageButton
    private lateinit var btnMyLocation: FloatingActionButton
    private lateinit var btnMapType: FloatingActionButton
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fishmap)


        // Initialize UI components
        btnBack = findViewById(R.id.btnBack)
        btnRefresh = findViewById(R.id.btnRefresh)
        btnMyLocation = findViewById(R.id.btnMyLocation)
        btnMapType = findViewById(R.id.btnMapType)
        progressBar = findViewById(R.id.progressBar)

        // Setup map
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Button listeners
        btnBack.setOnClickListener {
            finish()
        }

        btnRefresh.setOnClickListener {
            loadFishingSpots()
        }

        btnMyLocation.setOnClickListener {
            centerOnMyLocation()
        }

        btnMapType.setOnClickListener {
            toggleMapType()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap


        // Configure map
        map.apply {
            mapType = GoogleMap.MAP_TYPE_NORMAL
            uiSettings.apply {
                isZoomControlsEnabled = true
                isCompassEnabled = true
                isMyLocationButtonEnabled = false // We use our own button
            }

            // Set custom info window adapter
            setInfoWindowAdapter(CustomInfoWindowAdapter())

            // Center on Netherlands
            val netherlands = LatLng(52.3676, 4.9041)
            moveCamera(CameraUpdateFactory.newLatLngZoom(netherlands, 7f))

            // Map styling
            try {
                val styleOptions = MapStyleOptions(
                    """
                    [
                        {
                            "featureType": "water",
                            "elementType": "geometry",
                            "stylers": [{"color": "#4fc3f7"}]
                        }
                    ]
                    """.trimIndent()
                )
                setMapStyle(styleOptions)
            } catch (e: Exception) {
            }
        }

        // Enable my location if permission granted
        enableMyLocation()

        // Load fishing spots
        loadFishingSpots()
    }

    private fun loadFishingSpots() {
        progressBar.visibility = View.VISIBLE

        // Use coroutines for API call
        CoroutineScope(Dispatchers.IO).launch {
            try {

                val url = "${BuildConfig.BASE_URL}/api/fishing-spots.php"
                val response = URL(url).readText()
                val json = JSONObject(response)

                if (json.getBoolean("success")) {
                    val spots = json.getJSONArray("spots")

                    withContext(Dispatchers.Main) {
                        // Clear existing markers
                        markers.forEach { it.remove() }
                        markers.clear()

                        // Add new markers
                        for (i in 0 until spots.length()) {
                            val spot = spots.getJSONObject(i)
                            addMarker(spot)
                        }

                        progressBar.visibility = View.GONE
                        Toast.makeText(
                            this@FishmapActivity,
                            "✅ ${spots.length()} visplekken geladen",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    throw Exception("API returned success=false")
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@FishmapActivity,
                        "❌ Kon visplekken niet laden",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun addMarker(spotJson: JSONObject) {
        try {
            val lat = spotJson.getDouble("latitude")
            val lng = spotJson.getDouble("longitude")
            val name = spotJson.getString("name")
            val description = spotJson.optString("description", "")
            val fishSpecies = spotJson.optString("fish_species", "Onbekend")
            val rating = spotJson.optDouble("rating", 0.0)

            val position = LatLng(lat, lng)

            // Custom marker color based on rating
            val markerColor = when {
                rating >= 4.5 -> BitmapDescriptorFactory.HUE_GREEN
                rating >= 4.0 -> BitmapDescriptorFactory.HUE_BLUE
                rating >= 3.5 -> BitmapDescriptorFactory.HUE_CYAN
                else -> BitmapDescriptorFactory.HUE_ORANGE
            }

            val marker = map.addMarker(
                MarkerOptions()
                    .position(position)
                    .title("🎣 $name")
                    .snippet("$fishSpecies\n⭐ ${String.format("%.1f", rating)}")
                    .icon(BitmapDescriptorFactory.defaultMarker(markerColor))
            )

            marker?.tag = spotJson
            markers.add(marker!!)

        } catch (e: Exception) {
        }
    }

    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            try {
                map.isMyLocationEnabled = true
            } catch (e: SecurityException) {
            }
        } else {
            // Request permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST
            )
        }
    }

    private fun centerOnMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            try {
                map.isMyLocationEnabled = true
                // Note: To actually get the location and center, we'd need FusedLocationProviderClient
                // For now, just enable the location layer
                Toast.makeText(this, "📍 Locatie ingeschakeld", Toast.LENGTH_SHORT).show()
            } catch (e: SecurityException) {
                Toast.makeText(this, "❌ Locatie toegang geweigerd", Toast.LENGTH_SHORT).show()
            }
        } else {
            enableMyLocation()
        }
    }

    private fun toggleMapType() {
        currentMapType = when (currentMapType) {
            GoogleMap.MAP_TYPE_NORMAL -> {
                Toast.makeText(this, "🛰️ Satelliet", Toast.LENGTH_SHORT).show()
                GoogleMap.MAP_TYPE_SATELLITE
            }
            GoogleMap.MAP_TYPE_SATELLITE -> {
                Toast.makeText(this, "🏔️ Terrein", Toast.LENGTH_SHORT).show()
                GoogleMap.MAP_TYPE_TERRAIN
            }
            else -> {
                Toast.makeText(this, "🗺️ Kaart", Toast.LENGTH_SHORT).show()
                GoogleMap.MAP_TYPE_NORMAL
            }
        }
        map.mapType = currentMapType
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            LOCATION_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocation()
                } else {
                    Toast.makeText(this, "Locatie toegang geweigerd", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Custom Info Window Adapter - Shows detailed fishing spot information
     */
    inner class CustomInfoWindowAdapter : GoogleMap.InfoWindowAdapter {

        override fun getInfoWindow(marker: Marker): View? {
            // Return null to use default info window frame
            return null
        }

        override fun getInfoContents(marker: Marker): View? {
            val view = layoutInflater.inflate(R.layout.custom_info_window, null)
            val spotData = marker.tag as? JSONObject

            if (spotData != null) {
                try {
                    // Title
                    view.findViewById<android.widget.TextView>(R.id.infoTitle).text =
                        "🎣 ${spotData.getString("name")}"

                    // Description
                    val description = spotData.optString("description", "")
                    if (description.isNotEmpty()) {
                        view.findViewById<android.widget.TextView>(R.id.infoDescription).apply {
                            text = description
                            visibility = View.VISIBLE
                        }
                    } else {
                        view.findViewById<android.widget.TextView>(R.id.infoDescription).visibility = View.GONE
                    }

                    // Fish Species
                    view.findViewById<android.widget.TextView>(R.id.infoFishSpecies).text =
                        spotData.optString("fish_species", "Onbekend")

                    // Water Type
                    val waterType = spotData.optString("water_type", "")
                    if (waterType.isNotEmpty()) {
                        view.findViewById<android.widget.TextView>(R.id.infoWaterType).text =
                            when (waterType) {
                                "lake" -> "Meer"
                                "river" -> "Rivier"
                                "canal" -> "Kanaal"
                                "pond" -> "Vijver"
                                else -> waterType.capitalize()
                            }
                        view.findViewById<View>(R.id.waterTypeRow).visibility = View.VISIBLE
                    } else {
                        view.findViewById<View>(R.id.waterTypeRow).visibility = View.GONE
                    }

                    // Depth
                    val depth = spotData.optDouble("depth", 0.0)
                    if (depth > 0) {
                        view.findViewById<android.widget.TextView>(R.id.infoDepth).text =
                            "${String.format("%.1f", depth)} m"
                        view.findViewById<View>(R.id.depthRow).visibility = View.VISIBLE
                    } else {
                        view.findViewById<View>(R.id.depthRow).visibility = View.GONE
                    }

                    // Municipality
                    val municipality = spotData.optString("municipality", "")
                    val region = spotData.optString("region", "")
                    if (municipality.isNotEmpty() || region.isNotEmpty()) {
                        view.findViewById<android.widget.TextView>(R.id.infoMunicipality).text =
                            if (municipality.isNotEmpty() && region.isNotEmpty()) {
                                "$municipality, $region"
                            } else {
                                municipality.ifEmpty { region }
                            }
                        view.findViewById<View>(R.id.municipalityRow).visibility = View.VISIBLE
                    } else {
                        view.findViewById<View>(R.id.municipalityRow).visibility = View.GONE
                    }

                    // Rating
                    val rating = spotData.optDouble("rating", 0.0)
                    view.findViewById<android.widget.TextView>(R.id.infoRating).text =
                        "⭐ ${String.format("%.1f", rating)}"

                    // Reviews
                    val reviewCount = spotData.optInt("review_count", 0)
                    view.findViewById<android.widget.TextView>(R.id.infoReviews).text =
                        "💬 $reviewCount reviews"

                    // Catches
                    val catchCount = spotData.optInt("catch_count", 0)
                    view.findViewById<android.widget.TextView>(R.id.infoCatches).text =
                        "🐟 $catchCount vangsten"

                    // Crowd Level (Drukte)
                    val crowdLevel = spotData.optString("crowd_level", "low")
                    val crowdLabel = spotData.optString("crowd_label", "Rustig")
                    val activeUsers = spotData.optInt("active_users", 0)

                    val crowdTextView = view.findViewById<android.widget.TextView>(R.id.infoCrowdLevel)
                    val crowdRow = view.findViewById<View>(R.id.crowdRow)

                    // Set text with user count
                    crowdTextView.text = if (activeUsers > 0) {
                        "$crowdLabel ($activeUsers)"
                    } else {
                        crowdLabel
                    }

                    // Set color based on crowd level
                    val crowdColor = when (crowdLevel) {
                        "very_high" -> Color.parseColor("#dc2626") // Red
                        "high" -> Color.parseColor("#f59e0b") // Orange
                        "medium" -> Color.parseColor("#eab308") // Yellow
                        else -> Color.parseColor("#10b981") // Green (low/rustig)
                    }
                    crowdTextView.setTextColor(crowdColor)

                    // Optional: Set background color (very light version)
                    val backgroundColor = when (crowdLevel) {
                        "very_high" -> Color.parseColor("#fee2e2") // Light red
                        "high" -> Color.parseColor("#fef3c7") // Light orange
                        "medium" -> Color.parseColor("#fef9c3") // Light yellow
                        else -> Color.parseColor("#d1fae5") // Light green
                    }
                    crowdRow.setBackgroundColor(backgroundColor)

                } catch (e: Exception) {
                    Log.e("YessFish", "Error creating info window: ${e.message}")
                }
            }

            return view
        }
    }
}
