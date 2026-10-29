package cl.louarayamacrame.app.macrame.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body

/** Contratos de integración iniciales. Ajustar rutas y modelos al contrato real entregado por la cátedra. */
data class ApiProductDto(
    val id: Long,
    val name: String,
    val description: String,
    val category: String,
    val material: String,
    val widthCm: Int,
    val heightCm: Int,
    val price: Int,
    val imageUrl: String
)

data class ApiRequestDto(val productId: Long, val customerAlias: String, val eventType: String, val notes: String)
data class ApiRequestResponse(val id: Long, val status: String)

interface MacrameApi {
    @GET("products") suspend fun getProducts(): List<ApiProductDto>
    @POST("requests") suspend fun createRequest(@Body request: ApiRequestDto): ApiRequestResponse
}

/** No se instancia sin una URL base real. No usar una URL ficticia para simular sincronización. */
object MacrameApiFactory {
    fun create(baseUrl: String): MacrameApi {
        require(baseUrl.startsWith("https://") || baseUrl.startsWith("http://10.0.2.2")) {
            "Configura la URL oficial del backend antes de crear el cliente API."
        }
        require(baseUrl.endsWith("/")) { "La URL base debe terminar en /." }
        return Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build().create(MacrameApi::class.java)
    }
}
