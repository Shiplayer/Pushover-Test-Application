package com.developer.ship.pushovertestapplication.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by Shiplayer on 23.12.18.
 */

@Entity(tableName = "message_history")
data class PushoverMessage(
    val message: String,
    val date: Long,
    val userToken: String
){
    @PrimaryKey(autoGenerate = true)
    public var id:Int? = null
}