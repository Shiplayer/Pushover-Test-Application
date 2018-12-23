package com.developer.ship.pushovertestapplication.model

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.developer.ship.pushovertestapplication.PushoverAPI
import com.developer.ship.pushovertestapplication.entity.PushoverMessage
import com.developer.ship.pushovertestapplication.entity.PushoverResponse
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

/**
 * Created by Shiplayer on 23.12.18.
 */

class PushoverViewModel : ViewModel() {
    private val messageSubject = PublishSubject.create<PushoverMessage>()
    private val responseSubject = PublishSubject.create<PushoverResponse>()



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
    }

    public fun receiveResponse(): Observable<PushoverResponse>{
        return responseSubject
    }

}