package com.developer.ship.pushovertestapplication.entity

import java.util.*

/**
 * Created by Shiplayer on 23.12.18.
 */

data class PushoverMessage(
    val message: String,
    val date: Date,
    val userToken: String
)