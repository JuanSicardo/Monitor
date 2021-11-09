package com.juansicardo.monitor.model

import androidx.annotation.DrawableRes

data class ListItem(
    @DrawableRes val imageResourceId: Int,
    val text: String
)