package com.developer.ship.pushovertestapplication.entity

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import io.reactivex.Flowable

@Dao
public interface PushoverMessageDAO {
    @Insert
    fun insert(message: PushoverMessage)

    @Query("SELECT * FROM message_history order by id desc")
    fun getAllMessages(): Flowable<List<PushoverMessage>>

}