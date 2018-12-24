package com.developer.ship.pushovertestapplication.repository

import android.app.Application
import com.developer.ship.pushovertestapplication.database.MessageDatabase
import com.developer.ship.pushovertestapplication.entity.PushoverMessage
import com.developer.ship.pushovertestapplication.entity.PushoverMessageDAO
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class MessageRepository(app: Application) {
    private var mMessageDAO: PushoverMessageDAO
    private var mAllMessages: Observable<List<PushoverMessage>>
    init {
        val db : MessageDatabase = MessageDatabase.getInstance(app)!!
        mMessageDAO = db.messageDAO()
        mAllMessages = mMessageDAO.getAllMessages().toObservable()
    }

    fun getAllMessages() : Observable<List<PushoverMessage>>{
        return mAllMessages
    }

    public fun insert(message: PushoverMessage){
        Observable.just(message).subscribeOn(Schedulers.io()).subscribe { mMessageDAO.insert(it) }
    }


}