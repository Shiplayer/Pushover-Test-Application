package com.developer.ship.pushovertestapplication.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Shiplayer on 23.12.18.
 */

@Entity(tableName = "message_history")
data class PushoverMessage(
    val message: String,
    val title: String?,
    val date: Long,
    val userToken: String
) : Parcelable{
    @PrimaryKey(autoGenerate = true)
    public var id:Int? = null

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readString()
    ) {
        id = parcel.readValue(Int::class.java.classLoader) as? Int
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(message)
        parcel.writeString(title)
        parcel.writeLong(date)
        parcel.writeString(userToken)
        parcel.writeValue(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PushoverMessage> {
        override fun createFromParcel(parcel: Parcel): PushoverMessage {
            return PushoverMessage(parcel)
        }

        override fun newArray(size: Int): Array<PushoverMessage?> {
            return arrayOfNulls(size)
        }
    }
}