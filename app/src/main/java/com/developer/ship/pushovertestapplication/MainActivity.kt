package com.developer.ship.pushovertestapplication

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button.setOnClickListener {
            Observable.just("ur7zko984z7b6qkrei2ct5cyjne9j8")
                .subscribeOn(Schedulers.io())
                .subscribe { userToken ->
                    PushoverAPI.instance.sendMessage(userToken = userToken, message = "hello").subscribe({response ->
                        Log.i("MainActivity", response.body().toString())
                    }, {error ->
                        error.printStackTrace()
                    })
                }
        }
    }
}
