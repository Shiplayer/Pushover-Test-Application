package com.developer.ship.pushovertestapplication

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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
    private lateinit var userToken:String

    companion object {
        private val USER_TOKEN_KEY = "user_token_key"
        private val SETTING_PREFERENCE_KEY = "setting_preference_key"
        private val customAction = "com.developer.ship.pushovertestapplication.DEFERRED_MESSAGE"
        private val INDEX_DELAY_MESSAGE = "index_delay_message"
        private val QR_CORE_ACTIVITY_RESULT = 1001
        public val MESSAGE_KEY = "message_key"
        public val WRAPPER_BUNDLE_KEY = "message_bundle_key"
    }

    private lateinit var model: PushoverViewModel
    private lateinit var adapter: MessageAdapter
    private lateinit var alarmManager: AlarmManager
    private var disposable = CompositeDisposable()
    private var time: Calendar? = null
    private var indexDeferredMessage = 0
    private lateinit var stringTextDeferredMessage: List<String>
    private lateinit var delayDeferredMessage: List<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        handler()
        Log.i("MainActivity", "onCreate is invoke")
    }

    override fun onResume() {
        super.onResume()
        disposable.add(model.getListMessages()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                adapter.setData(it)
            })
        disposable.add(model.getErrorObservanle()
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Snackbar.make(rv_messages, it, Snackbar.LENGTH_LONG).show()
            })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item!!.itemId){
            R.id.qr_code -> {
                invokeQrCode()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun invokeQrCode(){
        try {
            val intent = Intent("com.google.zxing.client.android.SCAN")
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE")

            startActivityForResult(intent, QR_CORE_ACTIVITY_RESULT)
        } catch (e: Exception){
            val marketUri = Uri.parse("market://details?id=com.google.zxing.client.android")
            val marketIntent = Intent(Intent.ACTION_VIEW, marketUri)
            startActivity(marketIntent)
        }
    }

    private fun init(){
        if(et_user_token.text.isEmpty()) {
            userToken = getSharedPreferences(SETTING_PREFERENCE_KEY, Context.MODE_PRIVATE).getString(USER_TOKEN_KEY, "")
            et_user_token.setText(userToken)
        }
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        model = ViewModelProviders.of(this).get(PushoverViewModel::class.java)
        adapter = MessageAdapter()
        rv_messages.layoutManager = LinearLayoutManager(baseContext)
        rv_messages.adapter = adapter
        stringTextDeferredMessage = resources.getStringArray(R.array.deferred_message_time).asList()
        tv_deferred_message.text = stringTextDeferredMessage[indexDeferredMessage]
        delayDeferredMessage = resources.getIntArray(R.array.deferred_message_time_int).asList()

    }

    @SuppressLint("CheckResult")
    private fun handler(){
        tv_deferred_message.setOnClickListener {
            indexDeferredMessage = (indexDeferredMessage + 1) % stringTextDeferredMessage.size
            tv_deferred_message.text = stringTextDeferredMessage[indexDeferredMessage]
        }

        btn_send_message.setOnClickListener {
            Observable.create<String> { emitter ->
                checkToken()
                if(userToken.isNotEmpty())
                    if(et_message.text.isNotEmpty())
                        emitter.onNext(et_message.text.toString())
                    else
                        Toast.makeText(this, "Message block is empty", Toast.LENGTH_LONG).show()
                else
                    Toast.makeText(this, "User token not set up", Toast.LENGTH_LONG).show()
            }
                .subscribeOn(Schedulers.io())
                .subscribe { userMessage ->
                    val title = if(et_title_message.text.isEmpty()) null else et_title_message.text.toString()
                    val message = PushoverMessage(
                        userMessage,
                        title,
                        Date().time,
                        userToken
                    )
                    if(indexDeferredMessage != 0){
                        sendDeferredMessage(
                            message
                        )
                    } else{
                        model.pushMessage(
                            message
                        )
                    }
                }
        }

    }

    private fun sendDeferredMessage(message: PushoverMessage){
        val intent = Intent(this, DeferredMessage::class.java)
        intent.action = customAction
        val bundle = Bundle()
        bundle.putParcelable(MESSAGE_KEY, message)
        intent.putExtra(WRAPPER_BUNDLE_KEY, bundle)
        val pending = PendingIntent.getBroadcast(this, 0, intent, 0)
        Log.i("MainActivity", "send pending intent")
        alarmManager.set(
            AlarmManager.RTC,
            Calendar.getInstance().timeInMillis + delayDeferredMessage[indexDeferredMessage] * 60 * 1000,
            pending
        )
    }

    private fun checkToken(){
        val token = getSharedPreferences(SETTING_PREFERENCE_KEY, Context.MODE_PRIVATE).getString(USER_TOKEN_KEY, "")
        if(token.isEmpty()){
            if(et_user_token.text.isNotEmpty()){
                getSharedPreferences(SETTING_PREFERENCE_KEY, Context.MODE_PRIVATE).edit()
                    .putString(USER_TOKEN_KEY, et_user_token.text.toString()).apply()
                userToken = et_user_token.text.toString()
            }
        } else if(et_user_token.text.toString() != token && et_user_token.text.isNotEmpty()) {
            getSharedPreferences(SETTING_PREFERENCE_KEY, Context.MODE_PRIVATE).edit()
                .putString(USER_TOKEN_KEY, et_user_token.text.toString()).apply()
            userToken = et_user_token.text.toString()
        } else{
            userToken = token
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if(savedInstanceState!!.containsKey(USER_TOKEN_KEY)){
            et_user_token.setText(savedInstanceState.getString(USER_TOKEN_KEY))
        }
        indexDeferredMessage = savedInstanceState.getInt(INDEX_DELAY_MESSAGE, 0)
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        if(et_user_token.text.isNotEmpty()){
            outState!!.putString(USER_TOKEN_KEY,et_user_token.text.toString())
        }
        outState!!.putInt(INDEX_DELAY_MESSAGE, indexDeferredMessage)
    }

    override fun onStop() {
        super.onStop()
        Log.i("MainActivity", "Activity stopped")
        disposable.clear()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == QR_CORE_ACTIVITY_RESULT)
            if(resultCode == Activity.RESULT_OK){
                et_message.setText(data!!.getStringExtra("SCAN_RESULT"))
            } else{
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show()
            }
    }
}
