package com.smackmap.smackmapandroid.ui.utils

import com.smackmap.smackmapandroid.config.AppContext

fun pixelToDp(pixel: Float): Float
    = pixel / AppContext.INSTANCE.displayDensity

fun dpToPixel(dp: Float): Float
    = dp * AppContext.INSTANCE.displayDensity