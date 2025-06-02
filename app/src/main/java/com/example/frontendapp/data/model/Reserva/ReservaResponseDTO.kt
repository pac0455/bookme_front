import com.example.frontendapp.data.model.Reserva.EstadoReserva
import com.example.frontendapp.data.model.pago.EstadoPago

data class ReservaResponseDTO(
    val id: Int,
    val negocioId: Int,
    val usuarioId: String,
    val fecha: String, // "2024-01-15"
    val horaInicio: String, // "14:30:00"
    val horaFin: String, // "15:30:00"
    val estado: EstadoReserva,
    val fechaCreacion: String?, // "2024-01-10T10:00:00"
    val servicioId: Int,
    val servicio: ServicioDTO,
    val pago: PagoDTO?
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
    val creado: String
)


