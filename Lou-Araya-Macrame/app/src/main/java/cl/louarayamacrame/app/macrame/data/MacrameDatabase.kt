package cl.louarayamacrame.app.macrame.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val description: String,
    val category: String,
    val material: String,
    val widthCm: Int,
    val heightCm: Int,
    val price: Int,
    val imageUrl: String,
    val favorite: Boolean = false
)

@Entity(tableName = "requests")
data class RequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: Long,
    val productName: String,
    val customerAlias: String,
    val eventType: String,
    val notes: String,
    val createdAt: Long = System.currentTimeMillis(),
    val status: String = "Pendiente"
)

@Dao
interface MacrameDao {
    @Query("SELECT * FROM products ORDER BY id") fun observeProducts(): Flow<List<ProductEntity>>
    @Query("SELECT * FROM products WHERE favorite = 1 ORDER BY name") fun observeFavorites(): Flow<List<ProductEntity>>
    @Query("SELECT COUNT(*) FROM products") suspend fun productCount(): Int
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertProducts(items: List<ProductEntity>)
    @Query("UPDATE products SET favorite = :favorite WHERE id = :id") suspend fun setFavorite(id: Long, favorite: Boolean)
    @Insert suspend fun insertRequest(request: RequestEntity)
    @Query("SELECT * FROM requests ORDER BY createdAt DESC") fun observeRequests(): Flow<List<RequestEntity>>
}

@Database(entities = [ProductEntity::class, RequestEntity::class], version = 1, exportSchema = false)
abstract class MacrameDatabase : RoomDatabase() {
    abstract fun dao(): MacrameDao
    companion object {
        @Volatile private var instance: MacrameDatabase? = null
        fun get(context: Context): MacrameDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(context.applicationContext, MacrameDatabase::class.java, "lou_araya_macrame.db").build().also { instance = it }
        }
    }
}
