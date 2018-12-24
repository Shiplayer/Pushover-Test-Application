package com.developer.ship.pushovertestapplication

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import com.developer.ship.pushovertestapplication.adapter.MessageAdapter
import com.developer.ship.pushovertestapplication.entity.PushoverMessage
import com.developer.ship.pushovertestapplication.model.PushoverViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private val userToken = "ur7zko984z7b6qkrei2ct5cyjne9j8"

    private lateinit var model: PushoverViewModel
    private lateinit var adapter: MessageAdapter
    private var disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        model = ViewModelProviders.of(this).get(PushoverViewModel::class.java)
        adapter = MessageAdapter()
        rv_messages.layoutManager = LinearLayoutManager(baseContext)
        rv_messages.adapter = adapter
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
                        Date().time,
                        userToken
                    ))
                    PushoverAPI.instance.sendMessage(userToken = userToken, message = "hello").subscribe({response ->
                        Log.i("MainActivity", response.toString())
                    }, {error ->
                        error.printStackTrace()
                    })
                }
        }

        disposable.add(model.getListMessages().observeOn(AndroidSchedulers.mainThread()).subscribe {
            adapter.setData(it)
        })
    }

    override fun onStop() {
        super.onStop()
        disposable.clear()
    }
}
