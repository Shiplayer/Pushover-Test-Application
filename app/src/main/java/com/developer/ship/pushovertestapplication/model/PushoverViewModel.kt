package com.developer.ship.pushovertestapplication.model

import android.annotation.SuppressLint
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.util.Log
import com.developer.ship.pushovertestapplication.PushoverAPI
import com.developer.ship.pushovertestapplication.entity.PushoverMessage
import com.developer.ship.pushovertestapplication.entity.PushoverResponse
import com.developer.ship.pushovertestapplication.repository.MessageRepository
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

/**
 * Created by Shiplayer on 23.12.18.
 */

class PushoverViewModel(app:Application) : AndroidViewModel(app) {
    private val messageSubject = PublishSubject.create<PushoverMessage>()
    private val responseSubject = PublishSubject.create<PushoverResponse>()
    private val repository = MessageRepository(app)
    private val allMessages = repository


    @SuppressLint("CheckResult")
    public fun pushMessage(message: PushoverMessage){
        Single.just(message).subscribeOn(Schedulers.io())
            .subscribe({m ->
                PushoverAPI.instance.sendMessage(userToken = m.userToken, message = m.message).subscribe { response ->
                    Log.i("PushoverViewModel", response.toString())
                    responseSubject.onNext(response)
                }
            }, {error ->
                error.printStackTrace()
            })
        messageSubject.onNext(message)
        repository.insert(message)
    }

    public fun receiveResponse(): Observable<PushoverResponse>{
        return responseSubject
    }

    fun getListMessages() : Observable<List<PushoverMessage>>{
        return repository.getAllMessages()
    }

}