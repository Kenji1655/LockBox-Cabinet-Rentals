package br.edu.puccampinas.PI3_ES_2024_Time17

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MenuActivity : AppCompatActivity(), OnMapReadyCallback {

    private var ultimaLocalizacao: Location? = null
    private var polylineAtual: Polyline? = null
    private val requisitarCodigo = 1
    private lateinit var map: GoogleMap
    private lateinit var auth: FirebaseAuth
    private lateinit var nomeUsuario: TextView
    private lateinit var btnSair: Button
    private lateinit var btnAdicionarCartao: Button
    private lateinit var firestore: FirebaseFirestore


    class Desconectar : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val builder = AlertDialog.Builder(requireActivity())
            builder.setMessage("Deseja desconectar?")
                .setPositiveButton("Desconectar") { _, _ ->
                    FirebaseAuth.getInstance().signOut()
                    requireActivity().finish()
                }
                .setNegativeButton("Cancelar") { _, _ ->
                }
            return builder.create()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        btnSair = findViewById(R.id.btnSair)
        btnAdicionarCartao = findViewById(R.id.btnAdicionarCartao)
        nomeUsuario = findViewById(R.id.nomeUsuario)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        val user = auth.currentUser
        user?.let { currentUser ->
            val userId = currentUser.uid
            val userDocumentRef = firestore.collection("Usuario").document(userId)

            userDocumentRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val nome = documentSnapshot.getString("nome")
                    nomeUsuario.text = nome

                } else {
                    Log.d(TAG, "Documento não existe")
                }
            }.addOnFailureListener { exception ->
                Log.e(TAG, "erro ao pegar o documento", exception)
            }
        }

        btnSair.setOnClickListener {
            val dialogo = Desconectar()
            dialogo.show(supportFragmentManager, "DesconectarDialog")
        }

        btnAdicionarCartao.setOnClickListener {
            val iAdicionarCartao = Intent(this,AdicionarCartaoActivity::class.java)
            startActivity(iAdicionarCartao)
        }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {

        map = googleMap

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation()

            val localAtual = LocationServices.getFusedLocationProviderClient(this)
            localAtual.lastLocation
                .addOnSuccessListener { location : Location? ->
                    location?.let {
                        ultimaLocalizacao = it
                    }
                }
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), requisitarCodigo)
        }


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), requisitarCodigo)
        }


        val pucCampinasH15 = LatLng(-22.836043, -47.070497)
        val pucCampinas = LatLng(-22.834051,-47.052900)
        val marcadorPucCampinasH15 = googleMap.addMarker(
            MarkerOptions()
                .position(pucCampinasH15)
                .title("PucCampinasH15")
        )
        val marcadorPucCampinas = googleMap.addMarker(
            MarkerOptions()
                .position(pucCampinas)
                .title("pucCampinas")
        )

        marcadorPucCampinasH15?.tag = 0
        marcadorPucCampinas?.tag = 1
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pucCampinasH15, 15f))

        googleMap.setOnMarkerClickListener { marker ->

            val lugar: String = when (marker.tag) {
                0 -> "PucH15"
                1 -> "PucCampinas"
                else -> ""
            }

            val dialogView = layoutInflater.inflate(R.layout.layout_info_marcador, null)
            val dialogBuilder = AlertDialog.Builder(this)
                .setView(dialogView)

            val alertDialog = dialogBuilder.create()
            alertDialog.show()

            dialogView.findViewById<Button>(R.id.btnAloca).setOnClickListener{
                val iReservarArmario = Intent(this, EscolherArmarioActivity::class.java)
                iReservarArmario.putExtra("lugar", lugar)
                startActivity(iReservarArmario)
                alertDialog.dismiss()
            }

            dialogView.findViewById<Button>(R.id.btnRota).setOnClickListener {
                exibirRota(marker.position,map)
                alertDialog.dismiss()
            }

            false
        }

    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requisitarCodigo) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation()
            } else {
                Toast.makeText(this, "Permissão de localização não concedida", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun exibirRota(destino: LatLng,map: GoogleMap){

        polylineAtual?.remove()

        val origem = "${ultimaLocalizacao!!.latitude},${ultimaLocalizacao!!.longitude}"
        val chave = "AIzaSyAo7fDdu-B0wBUvEhzOvpbUDU5ZyJb8RBY"
        val geoApiContext = GeoApiContext.Builder()
            .apiKey(chave)
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val destination = "${destino.latitude},${destino.longitude}"
                Log.d(TAG, "Destino da rota: $destination")
                val directionsResult: DirectionsResult = DirectionsApi.newRequest(geoApiContext)
                    .mode(TravelMode.DRIVING)
                    .origin(origem)
                    .destination(destination)
                    .await()

                val points = mutableListOf<LatLng>()
                Log.d(TAG, "Direções resultantes obtidas com sucesso")
                val route = directionsResult.routes[0].overviewPolyline.decodePath()

                for (point in route) {
                    points.add(LatLng(point.lat, point.lng))
                }

                val polylineOptions = PolylineOptions()
                    .addAll(points)
                    .width(10f)
                    .color(Color.RED)

                runOnUiThread {
                    polylineAtual = map.addPolyline(polylineOptions)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao obter direções: ${e.message}", e)
            }
        }

    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        map.isMyLocationEnabled = true
    }
}
