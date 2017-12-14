package com.vutkathings.increment

import android.content.Context
import android.content.SharedPreferences
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ArrayAdapter
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.*
import com.vutkathings.increment.databinding.ActivityMainBinding

import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    /**
     * The entry point to Google Play Services.
     */
    private var mGoogleApiClient: GoogleApiClient? = null

    /**
     * A [MessageListener] for processing messages from nearby devices.
     */
    private var messageListener: MessageListener? = null

    /**
     * Adapter for working with messages from nearby publishers.
     */
    private var nearbyDeviceArrayAdapter: ArrayAdapter<String>? = null


    /**
     * One minutes.
     */
    private val ttlInSeconds = 60 * 3
    /**
     * Sets the time in seconds for a published message or a subscription to live. Set to three
     * minutes in this sample.
     */
    private val publishSubscriptionStrategy = Strategy.Builder().setTtlSeconds(ttlInSeconds).build()


    private val savedInstance = Calculate::class.java.simpleName

    private val TAG = MainActivity::class.java.simpleName

    private val keyUUID: String = "UUID_KEY"

    private lateinit var calculation: Calculate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        calculation = savedInstanceState?.get(savedInstance) as Calculate? ?:
                Calculate.builder(getUUID(getSharedPreferences(applicationContext.packageName, Context.MODE_PRIVATE)),
                        "0", "0", Operator.OPERATOR_PLUS)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding.calculate = calculation

        setOperatorDrawable(calculation.operator)

        buildGoogleApiClient()


        messageListener = object : MessageListener() {
            override fun onFound(message: Message) {
                nearbyDeviceArrayAdapter?.let {

                    it.add(Calculate.fromMessage(message).toString())

                    resultTv.text = calculation.result
                }
            }

            override fun onLost(message: Message) {
                nearbyDeviceArrayAdapter?.let {
                    it.remove(Calculate.fromMessage(message).toString())
                }
            }
        }

        if (mGoogleApiClient != null && mGoogleApiClient!!.isConnected)
            subscriberForMessage()


        fab_plus.setOnClickListener {
            publishMessage()
            calculation.operator = "Plus"
            setOperatorDrawable(calculation.operator)
        }

        fab_minus.setOnClickListener {
            Log.i(TAG, "minus clicked")
            publishMessage()
            calculation.operator = "Minus"
            setOperatorDrawable(calculation.operator)
        }

        fab_multiply.setOnClickListener {
            publishMessage()
            calculation.operator = "Multiply"
            setOperatorDrawable(calculation.operator)
        }

        fab_divide.setOnClickListener {

            if (calculation.operandTwo.equals("0")) {
                Snackbar.make(resultTv, "Divide by zero exception", Snackbar.LENGTH_SHORT).show()

                return@setOnClickListener
            }
            publishMessage()
            calculation.operator = "Divide"
            setOperatorDrawable(calculation.operator)
        }


    }


    private fun setOperatorDrawable(operator: String?) {

        when (operator) {
            "Plus" -> operatorIV.setImageResource(android.R.drawable.ic_input_add)
            "Minus" -> operatorIV.setImageResource(R.drawable.ic_minus_symbol)
            "Multiply" -> operatorIV.setImageResource(R.drawable.ic_multiply)
            else -> operatorIV.setImageResource(R.drawable.ic_divide)
        }
    }


    private fun getUUID(sharedPreferences: SharedPreferences): String {
        var uuid = sharedPreferences.getString(keyUUID, "")

        if (uuid.isEmpty()) {
            uuid.apply {
                uuid = UUID.randomUUID().toString()
                sharedPreferences.edit().putString(keyUUID, this).apply()
            }
        }

        return uuid
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.putSerializable(savedInstance, calculation)
    }

    private fun buildGoogleApiClient() {

        if (mGoogleApiClient != null)
            return


        mGoogleApiClient = GoogleApiClient.Builder(this@MainActivity)
                .addApi(Nearby.MESSAGES_API)
                .addConnectionCallbacks(this@MainActivity)
                .enableAutoManage(this@MainActivity, this@MainActivity)
                .build()
    }


    /**
     * Publishes a message to nearby devices and updates the UI if the publication either fails or
     * TTLs.
     */
    private fun publishMessage() {
        Log.i(TAG, "publishing msg")


        val option: PublishOptions = PublishOptions.Builder().setStrategy(publishSubscriptionStrategy)
                .setCallback(object : PublishCallback() {
                    override fun onExpired() {
                        super.onExpired()

                        Log.i(TAG, "Message Publish expired")
                        runOnUiThread { Snackbar.make(resultTv, "Publish expired", Snackbar.LENGTH_SHORT).show() }
                    }
                }).build()

        Nearby.Messages.publish(mGoogleApiClient, calculation.toMessage(), option)
                .setResultCallback { status ->
                    if (status.isSuccess) {
                        Log.i(TAG, "publishing was successful")
                        Snackbar.make(resultTv, "publishing successful", Snackbar.LENGTH_SHORT).show()
                    } else {
                        Snackbar.make(resultTv, "Couldn't publish " + status, Snackbar.LENGTH_SHORT).show()
                    }

                }

    }

    /**
     * Subscribes to messages from nearby devices and updates the UI if the subscription either
     * fails or TTLs.
     */
    private fun subscriberForMessage() {
        Log.i(TAG, "Subscribing")
        nearbyDeviceArrayAdapter!!.clear()

        val option: SubscribeOptions = SubscribeOptions.Builder().setStrategy(publishSubscriptionStrategy)
                .setCallback(object : SubscribeCallback() {
                    override fun onExpired() {
                        super.onExpired()

                        Log.i(TAG, "Subscription expired")
                        runOnUiThread { Snackbar.make(resultTv, "Subscription expired", Snackbar.LENGTH_SHORT).show() }
                    }
                }).build()

        Nearby.Messages.subscribe(mGoogleApiClient, messageListener, option)
                .setResultCallback { status ->
                    if (status.isSuccess) {
                        Log.i(TAG, "subscription was successful")
                        Snackbar.make(resultTv, "Subscription successful", Snackbar.LENGTH_SHORT).show()
                    } else {
                        Snackbar.make(resultTv, "Couldn't subscribe " + status, Snackbar.LENGTH_SHORT).show()
                    }

                }

    }

    /**
     * Stops publishing message to nearby devices.
     */
    private fun unpublish() {
        Log.i(TAG, "Unpublishing.")
        Nearby.Messages.unpublish(mGoogleApiClient, calculation.toMessage())
    }


    override fun onConnected(bundle: Bundle?) {
        Log.i(TAG, "GoogleApiClient connected")
        Snackbar.make(resultTv, "GoogleApiClient connected", Snackbar.LENGTH_SHORT).show()
    }

    override fun onConnectionSuspended(i: Int) {
        Snackbar.make(resultTv, "Google Api connection Suspended", Snackbar.LENGTH_SHORT).show()
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Snackbar.make(resultTv, "connection Failed" + connectionResult.errorMessage, Snackbar.LENGTH_SHORT).show()
    }


}
