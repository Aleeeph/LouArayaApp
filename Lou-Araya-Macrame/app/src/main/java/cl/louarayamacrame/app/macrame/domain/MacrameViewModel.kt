package cl.louarayamacrame.app.macrame.domain

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.louarayamacrame.app.macrame.data.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MacrameViewModel(app: Application) : AndroidViewModel(app) {
    private val repository = MacrameRepository(MacrameDatabase.get(app).dao())
    val products = repository.products.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val favorites = repository.favorites.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val requests = repository.requests.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    init { viewModelScope.launch { repository.seedIfEmpty() } }
    fun toggleFavorite(product: ProductEntity) = viewModelScope.launch { repository.favorite(product.id, !product.favorite) }
    fun submitRequest(product: ProductEntity, alias: String, event: String, notes: String) = viewModelScope.launch { repository.request(product, alias, event, notes) }
}
