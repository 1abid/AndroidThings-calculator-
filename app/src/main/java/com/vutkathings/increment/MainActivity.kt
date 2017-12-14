package com.vutkathings.increment

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.nearby.messages.MessageListener
import com.vutkathings.increment.databinding.ActivityMainBinding

import kotlinx.android.synthetic.main.activity_main.*

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

    private lateinit var calculation : Calculate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this , R.layout.activity_main)
        binding.calculate = calculation

        calculation = savedInstanceState?.get(savedInstance) as Calculate

        buildGoogleApiClient()

    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.putSerializable(savedInstance , calculation)
    }

    private fun buildGoogleApiClient() {

    }



    override fun onConnected(bundle: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConnectionSuspended(i: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }



}
