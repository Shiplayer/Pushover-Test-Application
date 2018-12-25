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
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * Created by Shiplayer on 23.12.18.
 */

class PushoverViewModel(app:Application) : AndroidViewModel(app), CoroutineScope{
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + Job()
    private val messageSubject = PublishSubject.create<PushoverMessage>()
    private lateinit var messageObservable: Observable<PushoverMessage>
    private val responseSubject = PublishSubject.create<PushoverResponse>()
    private val repository = MessageRepository(app)
    private val disposable = CompositeDisposable()
    private val allMessages = repository


    @SuppressLint("CheckResult")
    public fun pushMessage(message: PushoverMessage){
        val errorHandler = CoroutineExceptionHandler{_, exception ->
            launch {
                Log.i("PushoverViewModel", exception.message)
            }
        }
        launch(errorHandler) {
            val response = PushoverAPI.instance.sendMessage(userToken = message.userToken, message = message.message).await()
            if(response.status == 1)
                repository.insert(message)
        }
        /*Single.just(message).subscribeOn(Schedulers.io())
            .subscribe({m ->
                PushoverAPI.instance.sendMessage(userToken = m.userToken, message = m.message).subscribe ({ response ->
                    Log.i("PushoverViewModel", response.toString())
                    responseSubject.onNext(response)
                }, {error ->
                    Log.i("PushoverViewModel", error.message)
                })
            }, {error ->
                error.printStackTrace()
            })
        messageSubject.onNext(message)*/
    }

    override fun onCleared() {
        super.onCleared()
        coroutineContext.cancel()
    }

    fun getListMessages() : Observable<List<PushoverMessage>>{
        return repository.getAllMessages()
    }

}