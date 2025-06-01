import com.example.frontendapp.data.model.Reserva.EstadoReserva
import com.example.frontendapp.data.model.pago.EstadoPago

data class ReservaResponseDTO(
    val id: Int,
    val negocioId: Int,
    val usuarioId: String,
    val fecha: String,          // Cambiado a String
    val horaInicio: String,     // Cambiado a String
    val horaFin: String,        // Cambiado a String
    val estado: EstadoReserva,
    val fechaCreacion: String?, // Cambiado a String
    val servicioId: Int,
    val servicio: ServicioDTO,
    val pagos: List<PagoDTO> = emptyList()
)

data class ServicioDTO(
    val id: Int,
    val nombre: String
)

data class PagoDTO(
    val id: Int,
    val monto: Double,
    val estadoPago: EstadoPago,
    val metodoPago: String,
    val creado: String           // Cambiado a String
)
