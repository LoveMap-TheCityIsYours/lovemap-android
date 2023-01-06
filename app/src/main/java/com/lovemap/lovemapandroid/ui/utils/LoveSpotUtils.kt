package com.lovemap.lovemapandroid.ui.utils

import android.content.Context
import android.content.Intent
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.javadocmd.simplelatlng.LatLng
import com.javadocmd.simplelatlng.LatLngTool
import com.javadocmd.simplelatlng.util.LengthUnit
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lovespot.Availability
import com.lovemap.lovemapandroid.api.lovespot.ListOrdering
import com.lovemap.lovemapandroid.api.lovespot.LoveSpotType
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.config.MapContext
import com.lovemap.lovemapandroid.data.lovespot.LoveSpot
import com.lovemap.lovemapandroid.ui.events.ShowOnMapClickedEvent
import com.lovemap.lovemapandroid.ui.main.love.RecordLoveActivity
import com.lovemap.lovemapandroid.ui.main.lovespot.AddLoveSpotActivity
import com.lovemap.lovemapandroid.ui.main.lovespot.report.ReportLoveSpotActivity
import com.lovemap.lovemapandroid.utils.canEditLoveSpot
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

object LoveSpotUtils {

    private const val SHOW_SPOT_ON_MAP_MENU_ID = 10
    private const val EDIT_LOVE_SPOT_MENU_ID = 11
    private const val WISHLIST_LOVE_SPOT_MENU_ID = 12
    private const val MAKE_LOVE_LOVE_SPOT_MENU_ID = 13
    private const val REPORT_LOVE_SPOT_MENU_ID = 14

    fun setRating(
        rating: Double?,
        ratingBar: RatingBar
    ) {
        ratingBar.rating = rating?.toFloat() ?: 0f
    }

    fun setAvailability(
        availability: Availability,
        availabilityView: TextView
    ) {
        val context = AppContext.INSTANCE.applicationContext
        availabilityView.text =
            if (availability == Availability.ALL_DAY) {
                context.getString(R.string.available_all_day)
            } else {
                context.getString(R.string.available_night_only)
            }
    }

    fun setType(
        type: LoveSpotType,
        typeView: TextView
    ) {
        val context = AppContext.INSTANCE.applicationContext
        typeView.text = when (type) {
            LoveSpotType.PUBLIC_SPACE -> context.getString(R.string.type_public_space)
            LoveSpotType.SWINGER_CLUB -> context.getString(R.string.type_swinger_club)
            LoveSpotType.CRUISING_SPOT -> context.getString(R.string.type_cruising_spot)
            LoveSpotType.SEX_BOOTH -> context.getString(R.string.type_sex_booth)
            LoveSpotType.NIGHT_CLUB -> context.getString(R.string.type_night_club)
            LoveSpotType.OTHER_VENUE -> context.getString(R.string.type_other_venue)
        }
    }

    fun setRisk(
        averageDanger: Double?,
        risk: TextView
    ) {
        val context = AppContext.INSTANCE.applicationContext
        if (averageDanger != null) {
            risk.text = when {
                averageDanger < 1.5 -> {
                    context.getString(R.string.risk_safe)
                }
                averageDanger < 2.5 -> {
                    context.getString(R.string.risk_little_risky)
                }
                else -> {
                    context.getString(R.string.risk_very_risky)
                }
            }
        } else {
            risk.text = context.getString(R.string.risk_unknown)
        }
    }

    fun setCustomAvailability(
        loveSpot: LoveSpot,
        customAvText: TextView,
        customAv: TextView
    ) {
        val context = AppContext.INSTANCE.applicationContext
        if (loveSpot.customAvailability != null) {
            customAv.text = loveSpot.customAvailability
            customAvText.visibility = View.VISIBLE
            customAv.visibility = View.VISIBLE
        } else {
            customAvText.visibility = View.GONE
            customAv.visibility = View.GONE
        }
        customAvText.text = context.getString(R.string.custom_availability_text)
    }

    fun positionToAvailability(position: Int) =
        if (position == 0) {
            Availability.ALL_DAY
        } else {
            Availability.NIGHT_ONLY
        }

    fun availabilityToPosition(availability: Availability): Int {
        return when (availability) {
            Availability.ALL_DAY -> 0
            Availability.NIGHT_ONLY -> 1
        }
    }

    fun positionToType(position: Int): LoveSpotType = when (position) {
        1 -> LoveSpotType.PUBLIC_SPACE
        2 -> LoveSpotType.SWINGER_CLUB
        3 -> LoveSpotType.CRUISING_SPOT
        4 -> LoveSpotType.SEX_BOOTH
        5 -> LoveSpotType.NIGHT_CLUB
        else -> LoveSpotType.OTHER_VENUE
    }

    fun typeToPosition(type: LoveSpotType): Int = when (type) {
        LoveSpotType.PUBLIC_SPACE -> 1
        LoveSpotType.SWINGER_CLUB -> 2
        LoveSpotType.CRUISING_SPOT -> 3
        LoveSpotType.SEX_BOOTH -> 4
        LoveSpotType.NIGHT_CLUB -> 5
        LoveSpotType.OTHER_VENUE -> 6
    }

    fun positionToOrdering(position: Int): ListOrdering = when (position) {
        0 -> ListOrdering.TOP_RATED
        1 -> ListOrdering.CLOSEST
        2 -> ListOrdering.RECENTLY_ACTIVE
        3 -> ListOrdering.POPULAR
        4 -> ListOrdering.RECENT_PHOTOS
        else -> ListOrdering.NEWEST
    }

    fun orderingToPosition(ordering: ListOrdering): Int = when (ordering) {
        ListOrdering.TOP_RATED -> 0
        ListOrdering.CLOSEST -> 1
        ListOrdering.RECENTLY_ACTIVE -> 2
        ListOrdering.POPULAR -> 3
        ListOrdering.RECENT_PHOTOS -> 4
        ListOrdering.NEWEST -> 5
    }

    fun getTypeImageResource(type: LoveSpotType): Int {
        return when (type) {
            LoveSpotType.PUBLIC_SPACE -> R.drawable.ic_public_space_3
            LoveSpotType.SWINGER_CLUB -> R.drawable.ic_swinger_club_2
            LoveSpotType.CRUISING_SPOT -> R.drawable.ic_cruising_spot_1
            LoveSpotType.SEX_BOOTH -> R.drawable.ic_sex_booth_1
            LoveSpotType.NIGHT_CLUB -> R.drawable.ic_night_club_2
            LoveSpotType.OTHER_VENUE -> R.drawable.ic_other_venue_1
        }
    }

    fun setTypeImage(type: LoveSpotType, imageView: ImageView) {
        imageView.setImageResource(getTypeImageResource(type))
    }

    fun setDistance(distanceKm: Double?, loveSpotItemDistance: TextView) {
        distanceKm?.let {
            setDistanceText(loveSpotItemDistance, it)
        } ?: run {
            loveSpotItemDistance.visibility = View.GONE
        }
    }

    fun setDistance(loveSpot: LoveSpot, loveSpotItemDistance: TextView) {
        if (MapContext.lastLocation != null) {
            val distanceKm = getDistance(loveSpot)
            setDistanceText(loveSpotItemDistance, distanceKm)
        } else {
            loveSpotItemDistance.visibility = View.GONE
        }
    }

    fun calculateDistance(loveSpot: LoveSpot): Double? {
        return MapContext.lastLocation?.let {
            getDistance(loveSpot)
        }
    }

    private fun getDistance(loveSpot: LoveSpot) =
        LatLngTool.distance(
            MapContext.lastLocation,
            LatLng(loveSpot.latitude, loveSpot.longitude),
            LengthUnit.KILOMETER
        )

    private fun setDistanceText(loveSpotItemDistance: TextView, distanceKm: Double) {
        loveSpotItemDistance.visibility = View.VISIBLE
        if (distanceKm < 1.0) {
            loveSpotItemDistance.text = (distanceKm * 1000).toInt().toString() + " m"
        } else {
            loveSpotItemDistance.text = String.format("%.1f", distanceKm) + " km"
        }
    }

    class ContextMenuIds {
        companion object {
            var showOnMapCounter: Int = 5000
            var editCounter: Int = 6000
            var wishlistCounter: Int = 7000
            var makeLoveCounter: Int = 8000
            var reportCounter: Int = 9000
        }

        val showOnMapId: Int = nextShowOnMapId()
        val editId: Int = nextEditId()
        val wishlistId: Int = nextWishlistId()
        val makeLoveId: Int = nextMaveLoveId()
        val reportId: Int = nextReportId()

        fun nextShowOnMapId(): Int {
            if (showOnMapCounter == 5999) {
                showOnMapCounter = 5000
            }
            return showOnMapCounter++
        }

        fun nextEditId(): Int {
            if (editCounter == 6999) {
                editCounter = 6000
            }
            return editCounter++
        }

        fun nextWishlistId(): Int {
            if (wishlistCounter == 7999) {
                wishlistCounter = 7000
            }
            return wishlistCounter++
        }

        fun nextMaveLoveId(): Int {
            if (makeLoveCounter == 8999) {
                makeLoveCounter = 8000
            }
            return makeLoveCounter++
        }

        fun nextReportId(): Int {
            if (reportCounter == 9999) {
                reportCounter = 9000
            }
            return reportCounter++
        }
    }

    fun onContextItemSelected(
        item: MenuItem,
        contextMenuIds: ContextMenuIds,
        spotId: Long,
        context: Context
    ) {
        when (item.itemId) {
            contextMenuIds.showOnMapId -> {
                EventBus.getDefault().post(ShowOnMapClickedEvent(spotId))
            }
            contextMenuIds.editId -> {
                val intent = Intent(context, AddLoveSpotActivity::class.java)
                intent.putExtra(AddLoveSpotActivity.EDIT, spotId)
                context.startActivity(intent)
            }
            contextMenuIds.wishlistId -> {
                MainScope().launch {
                    AppContext.INSTANCE.wishlistService.addToWishlist(spotId)
                }
            }
            contextMenuIds.makeLoveId -> {
                AppContext.INSTANCE.selectedLoveSpotId = spotId
                val intent = Intent(context, RecordLoveActivity::class.java)
                context.startActivity(intent)
            }
            contextMenuIds.reportId -> {
                AppContext.INSTANCE.selectedLoveSpotId = spotId
                val intent = Intent(context, ReportLoveSpotActivity::class.java)
                context.startActivity(intent)
            }
        }
    }

    fun onCreateContextMenu(
        menu: ContextMenu,
        contextMenuIds: ContextMenuIds,
        name: String,
        addedBy: Long
    ) {
        menu.setHeaderTitle(name)
        menu.add(
            Menu.NONE,
            contextMenuIds.showOnMapId,
            Menu.NONE,
            R.string.show_on_map
        )
        if (canEditLoveSpot(addedBy)) {
            menu.add(
                Menu.NONE,
                contextMenuIds.editId,
                Menu.NONE,
                R.string.edit
            )
        }
        menu.add(
            Menu.NONE,
            contextMenuIds.wishlistId,
            Menu.NONE,
            R.string.to_wishlist
        )
        menu.add(
            Menu.NONE,
            contextMenuIds.makeLoveId,
            Menu.NONE,
            R.string.make_love
        )
        menu.add(
            Menu.NONE,
            contextMenuIds.reportId,
            Menu.NONE,
            R.string.report_spot
        )
    }

}

