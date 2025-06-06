package com.example.frontendapp.utils

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.core.app.ActivityCompat
import com.example.frontendapp.data.model.Negocio.Ubicacion
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

object UbicacionHelper {

    private const val CODIGO_SOLICITUD = 1001
    var onPermisoConcedido: (() -> Unit)? = null
    var onPermisoRechazado: (() -> Unit)? = null
    var permisoYaConcedido: Boolean? = null
    lateinit var permisoLauncher: ActivityResultLauncher<String>

    fun solicitarPermisos(
        activity: Activity,
        permisoLauncher: ActivityResultLauncher<String>,
        onConcedido: () -> Unit,
        onRechazado: () -> Unit
    ) {
        val permiso = Manifest.permission.ACCESS_FINE_LOCATION

        val yaTienePermiso = ActivityCompat.checkSelfPermission(
            activity,
            permiso
        ) == PackageManager.PERMISSION_GRANTED

        if (yaTienePermiso) {
            onConcedido()
        } else {
            permisoLauncher.launch(permiso)
        }
    }


    //Si tiene permisos mandarlo a los ajustes del sistema con la app
    fun openAppSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }



    @Suppress("DEPRECATION")
    suspend fun Geocoder.getFromLocationCompat(
        latitude: Double,
        longitude: Double,
        maxResults: Int
    ): List<Address> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        suspendCancellableCoroutine { continuation ->
            getFromLocation(latitude, longitude, maxResults) {
                continuation.resume(it)
            }
        }
    } else {
        withContext(Dispatchers.IO) {
            getFromLocation(latitude, longitude, maxResults) ?: emptyList()
        }
    }

    suspend fun obtenerUbicacionActual(context: Context): Ubicacion {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gpsActivo = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val redActiva = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!gpsActivo && !redActiva) return Ubicacion(null, null)

        val tienePermiso = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!tienePermiso) {
            Log.d("MapaScreen", "No se puede obtener la ubicación actual, revise si tiene activada la opción")
            Toast.makeText(context, "Ubicación no disponible", Toast.LENGTH_SHORT).show()
            return Ubicacion(null, null)
        }

        if (!gpsActivo) {
            Log.e("UbicacionHelper", "GPS está desactivado")
            Toast.makeText(context, "Sistema GPS desactivado, activalo para picar negocios más cercanos", Toast.LENGTH_SHORT).show()
            return Ubicacion(null, null)
        }

        return try {
            val fused = LocationServices.getFusedLocationProviderClient(context)
            val location = fused.lastLocation.await()

            if (location != null) {
                Log.d("UbicacionHelper", "Ubicación obtenida: ${location.latitude}, ${location.longitude}")
                Ubicacion(location.latitude, location.longitude)
            } else {
                Log.w("UbicacionHelper", "lastLocation devolvió null")
                Ubicacion(null, null)
            }
        } catch (e: Exception) {
            Log.e("UbicacionHelper", "Error al obtener ubicación: ${e.message}")
            Ubicacion(null, null)
        }
    }
}
