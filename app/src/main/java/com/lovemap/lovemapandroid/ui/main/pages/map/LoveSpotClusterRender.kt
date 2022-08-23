package com.lovemap.lovemapandroid.ui.main.pages.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lovespot.Availability
import com.lovemap.lovemapandroid.data.lovespot.LoveSpot

class LoveSpotClusterRender(
    private val context: Context,
    map: GoogleMap,
    clusterManager: ClusterManager<LoveSpotClusterItem>
) : DefaultClusterRenderer<LoveSpotClusterItem>(context, map, clusterManager) {

    private val dayBitmap: BitmapDescriptor
    private val nightBitmap: BitmapDescriptor
    private val loveBitmap: BitmapDescriptor

    var drawLoveMakings = false

    init {
        dayBitmap = getIconBitmap(R.drawable.ic_marker_sun)
        nightBitmap = getIconBitmap(R.drawable.ic_marker_moon)
        loveBitmap = getIconBitmap(R.drawable.ic_marker_heart)
    }

    override fun onBeforeClusterItemRendered(
        item: LoveSpotClusterItem,
        markerOptions: MarkerOptions
    ) {
        if (drawLoveMakings) {
            loveMakingToMarkerOptions(markerOptions, item.loveSpot)
        } else {
            loveSpotToMarkerOptions(markerOptions, item.loveSpot)
        }
    }

    private fun loveSpotToMarkerOptions(markerOptions: MarkerOptions, loveSpot: LoveSpot): MarkerOptions {
        val icon = if (loveSpot.availability == Availability.ALL_DAY) {
            dayBitmap
        } else {
            nightBitmap
        }
        return loveItemToMarkerOptions(markerOptions, loveSpot, icon)
    }

    private fun loveMakingToMarkerOptions(
        markerOptions: MarkerOptions,
        loveSpot: LoveSpot
    ): MarkerOptions {
        return loveItemToMarkerOptions(markerOptions, loveSpot, loveBitmap)
    }

    private fun loveItemToMarkerOptions(
        markerOptions: MarkerOptions,
        loveSpot: LoveSpot,
        icon: BitmapDescriptor
    ): MarkerOptions {
        val position = LatLng(loveSpot.latitude, loveSpot.longitude)
        markerOptions
            .icon(icon)
            .position(position)
            .snippet(loveSpot.id.toString())
            .title(loveSpot.name)
        return markerOptions
    }

    private fun getIconBitmap(drawableId: Int): BitmapDescriptor {
        val drawable: Drawable = ContextCompat.getDrawable(context, drawableId)!!
        val width = drawable.intrinsicWidth / 12
        val height = drawable.intrinsicHeight / 12
        drawable.setBounds(
            0,
            0,
            width,
            height
        )
        val bitmap = Bitmap.createBitmap(
            width,
            height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

}