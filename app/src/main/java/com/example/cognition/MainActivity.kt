package com.example.cognition

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.location.Location
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.pfinal.server.Endpoint
import com.example.pfinal.server.Ocorrencia
import com.example.pfinal.server.RetrofitClient
import com.google.android.gms.location.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity(),   EasyPermissions.PermissionCallbacks,

                                            EasyPermissions.RationaleCallbacks {
   //var servidor
    private val newOcorrencia = Ocorrencia()
    //VAR para o acelerometro
    private val LOCATION_PERM = 124
    private var speedUpStartT = 0L
    private var speedDownStartT = 0L
    private var speedUpEndT = 0L
    private var speedDownEndT = 0L

    //3-declaracao das variaveis necessarias para a localizacao
    private var mLocation: Location? = null
    private val RequestPermissionCode = 1

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private var isDone: Boolean by Delegates.observable(false) { _, _, newValue ->
        if (newValue) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val bt = findViewById<Button>(R.id.button)


        //findViewById<Button>(R.id.button).setOnClickListener {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            askForLocationPermission()

            createLocationRequest()
            bt.setOnClickListener {
                //4- chamar a funcao que retirar a localizacao
                gpsStart()
                //sendPost()
            }
            accelStar()
    }


    private fun accelStar(){
        val txt1 = findViewById<TextView>(R.id.velocidade)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                if (!isDone) {
                    val speedToInt = locationResult.lastLocation.speed.toInt()
                    calcSpeed(speedToInt)
                    txt1.text = speedToInt.toString()
                }
            }
        }
    }
    /**
     * 4 - Funcao que ativa o gps
     * Retorna as coordenadas geograficas, longitude e latitude
     */
    private fun gpsStart() {
        //retorna o valor da lat e lon
        val gps  = fusedLocationProviderClient.lastLocation

        //permissoes para o acesso do gps
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED)
        {
            //PEDIR PERMISSAO AO USER
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), RequestPermissionCode)
        }
        else
        {    //se o gps ja tiver ativo
            gps.addOnSuccessListener {
                // it (location)= 1 -> retorna os valores da lat e long
                mLocation = it

                if (it!=null)
                {
                    val latitude = findViewById<TextView>(R.id.latitude)
                    val longitude = findViewById<TextView>(R.id.longitude)
                    val time = findViewById<TextView>(R.id.time)
                    val date = findViewById<TextView>(R.id.date)
                    latitude.text= it.latitude.toString()
                    longitude.text= it.longitude.toString()
                    time.text = android.text.format.DateFormat.getTimeFormat(applicationContext).format(it.time)
                    date.text = android.text.format.DateFormat.getDateFormat(applicationContext).format(it.time)
                    newOcorrencia.latitude = it.latitude.toString()
                    newOcorrencia.longitude = it.longitude.toString()
                    newOcorrencia.time = android.text.format.DateFormat.getTimeFormat(applicationContext).format(it.time)
                    newOcorrencia.latitude = android.text.format.DateFormat.getDateFormat(applicationContext).format(it.time)
                }
            }
        }

    }


    private fun calcSpeed(speed: Int) {
        if(speed >= 10 ){
            speedUpStartT = System.currentTimeMillis()
            speedDownEndT = System.currentTimeMillis()

            if(speedDownStartT != 0L){
                val speedDowntime = speedDownEndT - speedDownStartT
                val txt2 = findViewById<TextView>(R.id.thirtytoTen)
                txt2.text =(speedDowntime/1000).toString()
                speedDownStartT= 0L
            }
        }
        else if (speed>=30){
            if (speedUpStartT != 0L){
                speedUpEndT = System.currentTimeMillis()
                val speedUpTime = speedUpEndT- speedUpStartT
                val txt3 = findViewById<TextView>(R.id.tentothirty)
                txt3.text =(speedUpTime/1000).toString()
                speedUpStartT = 0L
            }
            speedDownStartT = System.currentTimeMillis()
        }
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }


    fun createLocationRequest() {
         locationRequest = LocationRequest.create().apply {
             interval = 1000
             priority = LocationRequest.PRIORITY_HIGH_ACCURACY
         }
     }
     fun askForLocationPermission() {
            if (hasLocationPermissions()) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                fusedLocationProviderClient.lastLocation
                    .addOnSuccessListener { _: Location? ->

                    }
            }
                else
                {
                    EasyPermissions.requestPermissions(
                        this,
                        "Need permissions",
                        LOCATION_PERM,
                        android.Manifest.permission.ACCESS_FINE_LOCATION

                    )
                }
            }/*
    /****************************************** Post ***********************************/
    /**
     * Envio de uma ocorrencia ao servidor
     */

    private fun sendPost() {
        val destinationService: Endpoint = RetrofitClient.buildService(Endpoint::class.javaObjectType)
        val requestCall: Call<Ocorrencia> = destinationService.addOcorrencia(newOcorrencia)
        requestCall.enqueue(object : Callback<Ocorrencia> {
            //caso tudo corra bem a ocorrencia será enviada com sucesso
            override fun onResponse(call: Call<Ocorrencia>, response: Response<Ocorrencia>) {

            }
            // caso ocorra um erro é enviada uma mensagem de erro ao utilizador
            override fun onFailure(call: Call<Ocorrencia>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Ocorreu um erro", Toast.LENGTH_LONG).show()
            }
        })

    }*/

    private fun hasLocationPermissions(): Boolean {
        return EasyPermissions.hasPermissions(this,android.Manifest.permission.ACCESS_FINE_LOCATION)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        TODO("Not yet implemented")
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onRationaleAccepted(requestCode: Int) {
        TODO("Not yet implemented")
    }

    override fun onRationaleDenied(requestCode: Int) {
        TODO("Not yet implemented")
    }
}


