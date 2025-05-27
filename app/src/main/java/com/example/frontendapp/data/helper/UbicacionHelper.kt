package com.example.frontendapp.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LifecycleOwner
import com.example.frontendapp.data.model.Negocio.Ubicacion
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

object UbicacionHelper {

    private const val CODIGO_SOLICITUD = 1001
    private var onPermisoConcedido: (() -> Unit)? = null
    private var onPermisoRechazado: (() -> Unit)? = null
    private var permisoYaConcedido: Boolean? = null

    /**
     * Solicita permiso para acceder a la ubicación. Llama al callback correspondiente.
     */
    fun solicitarPermisoUbicacionDesde(
        activity: Activity,
    ) {
        val permiso = Manifest.permission.ACCESS_FINE_LOCATION

        //  Si ya lo habíamos concedido antes, no lo pedimos otra vez
        if (permisoYaConcedido==true) {
            return
        }

        val yaTienePermiso = ActivityCompat.checkSelfPermission(activity, permiso) == PackageManager.PERMISSION_GRANTED

        if (yaTienePermiso) {
            permisoYaConcedido = true
        } else {
            onPermisoConcedido = {
                permisoYaConcedido = true
            }
            onPermisoRechazado = {
                permisoYaConcedido = false
            }
            ActivityCompat.requestPermissions(activity, arrayOf(permiso), CODIGO_SOLICITUD)
        }
    }
    fun forzarSolicitudPermisoUbicacionDesde(
        activity: Activity,
        onConcedido: () -> Unit,
        onRechazado: () -> Unit
    ) {
        val permiso = Manifest.permission.ACCESS_FINE_LOCATION

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val yaTienePermiso = ActivityCompat.checkSelfPermission(activity, permiso) == PackageManager.PERMISSION_GRANTED

            if (yaTienePermiso) {
                // Aunque lo tenga, forzamos la lógica para que el usuario lo vea si es necesario
                onPermisoConcedido = {
                    permisoYaConcedido = true
                    onConcedido()
                }
                onPermisoRechazado = {
                    permisoYaConcedido = false
                    onRechazado()
                }
                ActivityCompat.requestPermissions(activity, arrayOf(permiso), CODIGO_SOLICITUD)
            } else {
                onPermisoConcedido = {
                    permisoYaConcedido = true
                    onConcedido()
                }
                onPermisoRechazado = {
                    permisoYaConcedido = false
                    onRechazado()
                }
                ActivityCompat.requestPermissions(activity, arrayOf(permiso), CODIGO_SOLICITUD)
            }
        } else {
            permisoYaConcedido = true
            onConcedido()
        }
    }

    //https://devexpert.io/localizacion-amplia-jetpack-compose/
    @OptIn(ExperimentalCoroutinesApi::class)
    @Suppress("DEPRECATION")
    suspend fun Geocoder.getFromLocationCompat(
        @FloatRange(from = -90.0, to = 90.0) latitude: Double,
        @FloatRange(from = -180.0, to = 180.0) longitude: Double,
        @IntRange maxResults: Int
    ): List<Address> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        suspendCancellableCoroutine { continuation ->
            getFromLocation(latitude, longitude, maxResults) {
                continuation.resume(it)
            }
        }} else {
        withContext(Dispatchers.IO) {
            getFromLocation(latitude, longitude, maxResults) ?: emptyList()
        }
    }
    /**
     * Devuelve la ubicación actual del usuario si está activada y hay permisos.
     */
    suspend fun obtenerUbicacionActual(context: Context): Ubicacion {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gpsActivo = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val redActiva = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (!gpsActivo && !redActiva) return Ubicacion(null, null)

        val tienePermiso = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!tienePermiso){
            Log.d("MapaScreen", "No se puede obtener la ubicacion actual, revise si tiene activada la opción")
            Toast.makeText(context, "Ubicación no disponible", Toast.LENGTH_SHORT).show()
            return Ubicacion(null, null)
        }
        if(isGpsEnabled.not()){
            Log.e("UbicacionHelper", "Intentando obtener la ubicacion que gps estaba desactivado")
            Toast.makeText(context, "Sistema GPS desactivado, activalo para poder picar negocios mas cercanos", Toast.LENGTH_SHORT).show()
            return Ubicacion(null, null)
        }
        return try {
            val fused = LocationServices.getFusedLocationProviderClient(context)
            val location = fused.lastLocation.await()

            if (location != null) {
                Log.d("UbicacionHelper", "Ubicación obtenida con éxito: ${location.latitude}, ${location.longitude}")
                Ubicacion(location.latitude, location.longitude)
            } else {
                Log.w("UbicacionHelper", "lastLocation devolvió null, considera usar requestLocationUpdates.")
                Ubicacion(null, null)
            }
        } catch (e: Exception) {
            Log.e("UbicacionHelper", "Error al obtener ubicación: ${e.message}")
            Ubicacion(null, null)
        }
    }

}
