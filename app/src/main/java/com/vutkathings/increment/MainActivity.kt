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
import com.google.android.gms.nearby.messages.MessageListener
import com.vutkathings.increment.databinding.ActivityMainBinding

import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() , GoogleApiClient.ConnectionCallbacks , GoogleApiClient.OnConnectionFailedListener {



    /**
     * The entry point to Google Play Services.
     */
    private var mGoogleApiClient : GoogleApiClient? = null

    /**
     * A [MessageListener] for processing messages from nearby devices.
     */
    private var messageListener : MessageListener ? = null

    /**
     * Adapter for working with messages from nearby publishers.
     */
    private var nearbyDeviceArrayAdapter : ArrayAdapter<String> ? = null


    private val savedInstance = Calculate::class.java.simpleName

    private val TAG = MainActivity::class.java.simpleName

    private val keyUUID: String = "UUID_KEY"

    private lateinit var calculation : Calculate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        calculation = savedInstanceState?.get(savedInstance) as Calculate? ?:
                        Calculate.builder(getUUID(getSharedPreferences(applicationContext.packageName , Context.MODE_PRIVATE)) ,
                                "0" , "0" , Operator.OPERATOR_PLUS)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this , R.layout.activity_main)
        binding.calculate = calculation

        setOperatorDrawable(calculation.operator)

        buildGoogleApiClient()

    }

    private fun setOperatorDrawable(operator: String?) {

        when(operator){
            "Plus" -> operatorIV.setImageResource(android.R.drawable.ic_input_add)
            "Minus" -> operatorIV.setImageResource(R.drawable.ic_minus_symbol)
            "Multiply" -> operatorIV.setImageResource(R.drawable.ic_multiply)
            else -> operatorIV.setImageResource(R.drawable.ic_divide)
        }
    }


    private fun getUUID(sharedPreferences: SharedPreferences): String {
        var uuid = sharedPreferences.getString(keyUUID , "")

        if(uuid.isEmpty()){
            uuid.apply {
                UUID.randomUUID().toString()
                sharedPreferences.edit().putString(keyUUID , this).apply()
            }
        }

        return uuid
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.putSerializable(savedInstance , calculation)
    }

    private fun buildGoogleApiClient() {

        if (mGoogleApiClient != null)
            return

        mGoogleApiClient.apply {
            GoogleApiClient.Builder(this@MainActivity)
                    .addApi(Nearby.MESSAGES_API)
                    .addConnectionCallbacks(this@MainActivity)
                    .enableAutoManage(this@MainActivity,this@MainActivity)
                    .build()
        }
    }



    override fun onConnected(bundle: Bundle?) {
        Log.i(TAG, "GoogleApiClient connected")
        Snackbar.make(resultTv , "GoogleApiClient connected" , Snackbar.LENGTH_SHORT).show()
    }

    override fun onConnectionSuspended(i: Int) {
        Snackbar.make(resultTv , "Google Api connection Suspended" , Snackbar.LENGTH_SHORT).show()
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Snackbar.make(resultTv , "connection Failed" +connectionResult.errorMessage , Snackbar.LENGTH_SHORT).show()
    }



}
