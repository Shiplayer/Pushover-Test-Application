package com.developer.ship.pushovertestapplication

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.developer.ship.pushovertestapplication.entity.PushoverMessage
import com.developer.ship.pushovertestapplication.repository.MessageRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Created by Shiplayer on 26.12.18.
 */

class DeferredMessage : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        if(context != null && intent != null && intent.extras.containsKey(MainActivity.WRAPPER_BUNDLE_KEY)) {
            val message = intent
                .extras
                .getBundle(MainActivity.WRAPPER_BUNDLE_KEY)
                .getParcelable<PushoverMessage>(MainActivity.MESSAGE_KEY)
            val repository = MessageRepository(context.applicationContext as Application)
            val errorHandler = CoroutineExceptionHandler{_, exception ->
                Toast.makeText(context, "Message was not send", Toast.LENGTH_LONG).show()
            }
            GlobalScope.launch(errorHandler) {
                Log.i("DeferredMessage", "sending")
                val response = PushoverAPI
                    .instance
                    .sendMessage(userToken = message.userToken, message = message.message, title = message.title)
                    .await()
                if(response.status == 1) {
                    repository.insert(message)
                }
            }
        }
    }

}