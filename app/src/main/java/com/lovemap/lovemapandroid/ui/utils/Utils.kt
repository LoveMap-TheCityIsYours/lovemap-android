package com.lovemap.lovemapandroid.ui.utils

import com.lovemap.lovemapandroid.config.AppContext

fun pixelToDp(pixel: Float): Float
    = pixel / AppContext.INSTANCE.displayDensity

fun dpToPixel(dp: Float): Float
    = dp * AppContext.INSTANCE.displayDensity