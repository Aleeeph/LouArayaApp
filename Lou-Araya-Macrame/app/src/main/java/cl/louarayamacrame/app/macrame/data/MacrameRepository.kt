package cl.louarayamacrame.app.macrame.data

import kotlinx.coroutines.flow.Flow

class MacrameRepository(private val dao: MacrameDao) {
    val products: Flow<List<ProductEntity>> = dao.observeProducts()
    val favorites: Flow<List<ProductEntity>> = dao.observeFavorites()
    val requests: Flow<List<RequestEntity>> = dao.observeRequests()

    suspend fun seedIfEmpty() {
        if (dao.productCount() == 0) dao.insertProducts(listOf(
            ProductEntity(1, "Manto Aurora", "Tapiz artesanal de textura orgánica y caída elegante.", "Interiores", "Algodón", 60, 90, 45990, "https://images.unsplash.com/photo-1616486338812-3dadae4b4ace?w=900"),
            ProductEntity(2, "Arco Nupcial", "Decoración textil para ceremonias y matrimonios.", "Matrimonios", "Algodón", 120, 180, 129990, "https://images.unsplash.com/photo-1519741497674-611481863552?w=900"),
            ProductEntity(3, "Brisa Natural", "Pieza liviana tejida a mano en fibras naturales.", "Interiores", "Cáñamo", 35, 55, 24990, "https://images.unsplash.com/photo-1600210492486-724fe5c67fb0?w=900"),
            ProductEntity(4, "Festival Tierra", "Colgante decorativo para escenarios y espacios creativos.", "Festivales", "Algodón", 80, 110, 69990, "https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?w=900"),
            ProductEntity(5, "Nómada", "Detalle textil compacto para ambientaciones y viajes.", "Viajes", "Cáñamo", 25, 35, 15990, "https://images.unsplash.com/photo-1600566753086-00f18fb6b3ea?w=900"),
            ProductEntity(6, "Luna Bohemia", "Tapiz de nudos decorativos con terminaciones artesanales.", "Matrimonios", "Algodón", 70, 100, 58990, "https://images.unsplash.com/photo-1618221195710-dd6b41faaea6?w=900")
        ))
    }
    suspend fun favorite(id: Long, value: Boolean) = dao.setFavorite(id, value)
    suspend fun request(product: ProductEntity, alias: String, event: String, notes: String) = dao.insertRequest(RequestEntity(productId = product.id, productName = product.name, customerAlias = alias, eventType = event, notes = notes))
}
