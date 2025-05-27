import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import com.example.frontendapp.data.model.Categoria
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.data.remote.source.CategoriaRemoteDataSource
import com.example.frontendapp.data.remote.reponses.Resource
import com.google.gson.Gson
import org.json.JSONObject

class CategoriaRemoteDataSourceTest {

    // Instancia usando Retrofit real
    private val categoriaRemoteDataSource = CategoriaRemoteDataSource(RetrofitInstance.categoriaApi)

    @Test
    fun `get categorias successfully`() = runBlocking {
        val result = categoriaRemoteDataSource.getCategorias()

        when(result) {
            is Resource.Success -> {
                println("✅ Categorías obtenidas: ${result.data?.size ?: 0}")
                result.data?.forEach { categoria ->
                    println("Categoria: ${categoria.nombre}")
                }
            }
            is Resource.Error -> {
                println("❌ Error al obtener categorías: ${result.message}")
                // Puedes agregar detalles extras si el servidor responde JSON de error
                val rawError = result.validationResponse?.let {
                    Gson().toJson(it)
                } ?: "No detalles"
                println("Detalles: $rawError")
            }
            else -> {}
        }

        assertTrue(result is Resource.Success && !result.data.isNullOrEmpty())
    }
}
