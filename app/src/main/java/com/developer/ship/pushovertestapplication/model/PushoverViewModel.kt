package com.developer.ship.pushovertestapplication.model

import android.annotation.SuppressLint
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.util.Log
import com.developer.ship.pushovertestapplication.PushoverAPI
import com.developer.ship.pushovertestapplication.entity.PushoverMessage
import com.developer.ship.pushovertestapplication.repository.MessageRepository
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * Created by Shiplayer on 23.12.18.
 */

class PushoverViewModel(app:Application) : AndroidViewModel(app), CoroutineScope{
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + Job()
    private val repository = MessageRepository(app)
    private val errorPublisher = PublishSubject.create<String>()


    @SuppressLint("CheckResult")
    public fun pushMessage(message: PushoverMessage){
        val errorHandler = CoroutineExceptionHandler{_, exception ->
            launch {
                errorPublisher.onNext(exception.message!!)
                //Log.i("PushoverViewModel", exception.message)
            }
        }
        launch(errorHandler) {
            val response = PushoverAPI.instance.sendMessage(
                userToken = message.userToken,
                message = message.message,
                title = message.title
            ).await()
            Log.i("PushoverViewModel", response.status.toString())
            if(response.status == 1) {
                repository.insert(message)
            }
        }
    }

    fun getErrorObservanle() : Observable<String> = errorPublisher

    override fun onCleared() {
        super.onCleared()
        coroutineContext.cancel()
    }

    fun getListMessages() : Observable<List<PushoverMessage>>{

        return repository.getAllMessages().cache()
    }

}