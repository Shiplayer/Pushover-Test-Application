package com.developer.ship.pushovertestapplication

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.developer.ship.pushovertestapplication.entity.PushoverMessage
import com.developer.ship.pushovertestapplication.model.PushoverViewModel
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private val userToken = "ur7zko984z7b6qkrei2ct5cyjne9j8"

    private lateinit var model: PushoverViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        model = ViewModelProviders.of(this).get(PushoverViewModel::class.java)
        btn_send_message.setOnClickListener {
            Observable.create<String> {emitter ->
                if(et_message.text.isNotEmpty())
                    emitter.onNext(et_message.text.toString())
                else
                    Toast.makeText(this, "Message block is empty", Toast.LENGTH_LONG).show()
            }
                .subscribeOn(Schedulers.io())
                .subscribe { userMessage ->
                    model.pushMessage(PushoverMessage(
                        userMessage,
                        Date(),
                        userToken
                    ))
                    PushoverAPI.instance.sendMessage(userToken = userToken, message = "hello").subscribe({response ->
                        Log.i("MainActivity", response.toString())
                    }, {error ->
                        error.printStackTrace()
                    })
                }
        }
    }
}
