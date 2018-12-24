package com.developer.ship.pushovertestapplication.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.developer.ship.pushovertestapplication.entity.PushoverMessage
import com.developer.ship.pushovertestapplication.entity.PushoverMessageDAO


@Database(entities = [PushoverMessage::class], version = 1)
abstract class MessageDatabase : RoomDatabase(){
    abstract fun messageDAO(): PushoverMessageDAO

    companion object {
        private var INSTANCE: MessageDatabase? = null

        fun getInstance(context: Context): MessageDatabase? {
            if (INSTANCE == null) {
                synchronized(MessageDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        MessageDatabase::class.java, "message.db")
                        .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}