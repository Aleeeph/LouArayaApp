package cl.louarayamacrame.app.macrame.ui

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import cl.louarayamacrame.app.macrame.data.ProductEntity
import cl.louarayamacrame.app.macrame.domain.MacrameViewModel
import cl.louarayamacrame.app.macrame.domain.MacrameValidators
import java.text.NumberFormat
import java.util.Locale

private val Terracotta = Color(0xFF9C5944)
private val Cream = Color(0xFFFAF5EC)
private val Ink = Color(0xFF342820)
private val categories = listOf("Todos", "Matrimonios", "Festivales", "Interiores", "Viajes")
private fun money(value: Int) = NumberFormat.getCurrencyInstance(Locale("es", "CL")).format(value)

@Composable
fun MacrameScreen(vm: MacrameViewModel = viewModel()) {
    val context = LocalContext.current
    val products by vm.products.collectAsState()
    val favorites by vm.favorites.collectAsState()
    val requests by vm.requests.collectAsState()
    var tab by remember { mutableStateOf(0) }
    var category by remember { mutableStateOf("Todos") }
    var selected by remember { mutableStateOf<ProductEntity?>(null) }
    var requestProduct by remember { mutableStateOf<ProductEntity?>(null) }
    var photoUri by remember { mutableStateOf<android.net.Uri?>(null) }
    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { photoUri = it }
    val notificationPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted -> if (granted) sendCampaignNotification(context) }

    MaterialTheme(colorScheme = lightColorScheme(primary = Terracotta, onPrimary = Color.White, background = Cream, surface = Color.White, onSurface = Ink, secondary = Color(0xFF6C8065))) {
        Scaffold(containerColor = Cream, bottomBar = {
            NavigationBar(containerColor = Color.White) {
                listOf("Catálogo", "Favoritos", "Solicitudes", "Perfil").forEachIndexed { index, label ->
                    val icon = when(index) { 0 -> Icons.Rounded.Storefront; 1 -> Icons.Rounded.Favorite; 2 -> Icons.Rounded.ReceiptLong; else -> Icons.Rounded.Person }
                    NavigationBarItem(selected = tab == index, onClick = { tab = index; selected = null }, icon = { Icon(icon, null) }, label = { Text(label) })
                }
            }
        }) { padding ->
            AnimatedVisibility(true, enter = fadeIn() + slideInVertically { it / 12 }) {
                Column(Modifier.fillMaxSize().padding(padding)) {
                    if (selected != null) ProductDetail(selected!!, onBack = { selected = null }, onFavorite = { vm.toggleFavorite(selected!!) }, onRequest = { requestProduct = selected })
                    else when(tab) {
                        0 -> Catalog(products, category, { category = it }, { selected = it }, { vm.toggleFavorite(it) }, { requestProduct = it })
                        1 -> Catalog(favorites, category, { category = it }, { selected = it }, { vm.toggleFavorite(it) }, { requestProduct = it }, title = "Tus favoritos")
                        2 -> RequestsScreen(requests.map { "${it.productName} · ${it.eventType} · ${it.status}" })
                        else -> ProfileScreenMacrame(onPhoto = { photoPicker.launch(androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }, photoUri = photoUri, onNotify = {
                            if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS) else sendCampaignNotification(context)
                        })
                    }
                }
            }
            if (requestProduct != null) RequestDialog(requestProduct!!, onDismiss = { requestProduct = null }, onSubmit = { alias, event, notes -> vm.submitRequest(requestProduct!!, alias, event, notes); requestProduct = null })
        }
    }
}

@Composable private fun Catalog(items: List<ProductEntity>, category: String, onCategory: (String) -> Unit, onOpen: (ProductEntity) -> Unit, onFavorite: (ProductEntity) -> Unit, onRequest: (ProductEntity) -> Unit, title: String = "Piezas hechas para inspirar") {
    var search by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(Color(0xFF4B3A30), Terracotta))).padding(horizontal = 22.dp, vertical = 20.dp)) {
            Text("LOU ARAYA  ·  MACRAMÉ", style = MaterialTheme.typography.labelMedium, color = Color(0xFFF0D7C2))
            Spacer(Modifier.height(10.dp)); Text(title, style = MaterialTheme.typography.headlineSmall, color = Color.White)
            Spacer(Modifier.height(5.dp)); Text("Textiles artesanales para momentos especiales", color = Color.White.copy(alpha = .85f))
            Spacer(Modifier.height(14.dp)); OutlinedTextField(search, { search = it }, Modifier.fillMaxWidth(), placeholder = { Text("Buscar una pieza...") }, leadingIcon = { Icon(Icons.Rounded.Search, null) }, singleLine = true, shape = RoundedCornerShape(16.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White))
        }
        Row(Modifier.fillMaxWidth().padding(vertical = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Spacer(Modifier.width(8.dp)); categories.forEach { item -> FilterChip(selected = category == item, onClick = { onCategory(item) }, label = { Text(item) }) }
        }
        val filtered = items.filter { (category == "Todos" || it.category == category) && (it.name.contains(search, true) || it.material.contains(search, true)) }
        LazyColumn(contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            item { Text("${filtered.size} piezas disponibles", color = Color(0xFF78695F), style = MaterialTheme.typography.labelLarge) }
            items(filtered, key = { it.id }) { product -> ProductCard(product, { onOpen(product) }, { onFavorite(product) }, { onRequest(product) }) }
            if (filtered.isEmpty()) item { Text("No encontramos piezas para esta búsqueda.", Modifier.padding(20.dp)) }
        }
    }
}

@Composable private fun ProductCard(p: ProductEntity, onOpen: () -> Unit, onFavorite: () -> Unit, onRequest: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable { onOpen() }, shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Column {
            Box {
                AsyncImage(model = p.imageUrl, contentDescription = p.name, modifier = Modifier.fillMaxWidth().height(185.dp).background(Color(0xFFE9DFD2)), contentScale = androidx.compose.ui.layout.ContentScale.Crop)
                Surface(Modifier.padding(12.dp), color = Color.White.copy(alpha = .92f), shape = RoundedCornerShape(20.dp)) { Text(p.category, Modifier.padding(horizontal = 10.dp, vertical = 5.dp), style = MaterialTheme.typography.labelSmall, color = Terracotta) }
                IconButton(onClick = onFavorite, modifier = Modifier.align(Alignment.TopEnd).padding(5.dp).background(Color.White.copy(alpha = .92f), RoundedCornerShape(50))) { Icon(if (p.favorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder, "Guardar favorito", tint = Terracotta) }
            }
            Column(Modifier.padding(16.dp)) {
                Text(p.name, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(4.dp)); Text("${p.material} · ${p.widthCm} × ${p.heightCm} cm", color = Color(0xFF76685F), style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(10.dp)); Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(money(p.price), style = MaterialTheme.typography.titleMedium, color = Terracotta)
                    Spacer(Modifier.weight(1f)); TextButton(onClick = onRequest) { Text("Solicitar", fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}

@Composable private fun ProductDetail(p: ProductEntity, onBack: () -> Unit, onFavorite: () -> Unit, onRequest: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = onBack) { Icon(Icons.Rounded.ArrowBack, "Volver") }; Text("Detalle de la pieza", style = MaterialTheme.typography.titleLarge) }
        LazyColumn(Modifier.weight(1f)) {
            item { AsyncImage(p.imageUrl, p.name, Modifier.fillMaxWidth().height(300.dp).background(Color(0xFFE9DFD2)), contentScale = androidx.compose.ui.layout.ContentScale.Crop) }
            item { Column(Modifier.padding(22.dp)) {
                Text(p.category.uppercase(), color = Terracotta, style = MaterialTheme.typography.labelLarge); Spacer(Modifier.height(8.dp)); Text(p.name, style = MaterialTheme.typography.headlineSmall); Spacer(Modifier.height(10.dp)); Text(p.description); Spacer(Modifier.height(18.dp))
                Text("Detalles", style = MaterialTheme.typography.titleMedium); Text("Material: ${p.material}\nDimensiones: ${p.widthCm} cm de ancho × ${p.heightCm} cm de alto\nCódigo: LA-${p.id.toString().padStart(4, '0')}", Modifier.padding(top = 8.dp)); Spacer(Modifier.height(16.dp)); Text(money(p.price), style = MaterialTheme.typography.headlineSmall, color = Terracotta)
            } }
        }
        Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) { OutlinedButton(onClick = onFavorite, modifier = Modifier.weight(1f)) { Icon(Icons.Rounded.FavoriteBorder, null); Text(" Favorito") }; Button(onClick = onRequest, modifier = Modifier.weight(1f)) { Text("Solicitar pieza") } }
    }
}

@Composable private fun RequestDialog(product: ProductEntity, onDismiss: () -> Unit, onSubmit: (String, String, String) -> Unit) {
    var alias by remember { mutableStateOf("") }; var event by remember { mutableStateOf("Matrimonios") }; var notes by remember { mutableStateOf("") }; var attempted by remember { mutableStateOf(false) }
    val aliasError = MacrameValidators.validateAlias(alias)
    val notesError = MacrameValidators.validateNotes(notes)
    val valid = aliasError == null && notesError == null
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Solicitar cotización") }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(product.name, color = Terracotta, fontWeight = FontWeight.SemiBold)
            OutlinedTextField(alias, { alias = it; attempted = false }, label = { Text("Nombre o alias") }, singleLine = true, isError = attempted && !valid, supportingText = { if (attempted && aliasError != null) Text(aliasError) }, leadingIcon = { Icon(Icons.Rounded.Person, null) })
            Text("Tipo de evento")
            var expanded by remember { mutableStateOf(false) }
            Box { OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) { Text(event); Spacer(Modifier.weight(1f)); Icon(Icons.Rounded.ArrowDropDown, null) }; DropdownMenu(expanded, { expanded = false }) { categories.drop(1).forEach { DropdownMenuItem(text = { Text(it) }, onClick = { event = it; expanded = false }) } } }
            OutlinedTextField(notes, { notes = it }, label = { Text("Medidas o detalles adicionales (opcional)") }, minLines = 2, isError = attempted && notesError != null, supportingText = { if (attempted && notesError != null) Text(notesError) })
            Text("Se guardará localmente como solicitud Pendiente. Datos ficticios: no ingreses información sensible.", style = MaterialTheme.typography.bodySmall, color = Color(0xFF76685F))
        }
    }, confirmButton = { Button(onClick = { attempted = true; if (valid) onSubmit(alias.trim(), event, notes.trim()) }) { Text("Guardar solicitud") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } })
}

@Composable private fun RequestsScreen(lines: List<String>) {
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("Mis solicitudes", style = MaterialTheme.typography.headlineSmall); Spacer(Modifier.height(8.dp)); Text("Registro local de tus intenciones de compra", color = Color(0xFF76685F)); Spacer(Modifier.height(16.dp))
        if (lines.isEmpty()) Text("Todavía no tienes solicitudes. Explora el catálogo para cotizar una pieza.", Modifier.padding(top = 24.dp))
        else LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) { items(lines) { line -> Card(colors = CardDefaults.cardColors(containerColor = Color.White)) { Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Rounded.ReceiptLong, null, tint = Terracotta); Spacer(Modifier.width(12.dp)); Text(line) } } } }
    }
}

@Composable private fun ProfileScreenMacrame(onPhoto: () -> Unit, photoUri: android.net.Uri?, onNotify: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(22.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Tu espacio", style = MaterialTheme.typography.headlineSmall); Text("Lou Araya Macramé", style = MaterialTheme.typography.titleLarge); Text("Diseño textil artesanal para celebraciones y espacios con identidad.")
        if (photoUri != null) AsyncImage(photoUri, "Imagen seleccionada", Modifier.fillMaxWidth().height(190.dp).clip(RoundedCornerShape(18.dp)), contentScale = androidx.compose.ui.layout.ContentScale.Crop)
        OutlinedButton(onClick = onPhoto, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Rounded.AddAPhoto, null); Text("  Elegir imagen desde el dispositivo") }
        Button(onClick = onNotify, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Rounded.NotificationsActive, null); Text("  Probar notificación de campaña") }
        Card(colors = CardDefaults.cardColors(containerColor = Color.White)) { Column(Modifier.padding(16.dp)) { Text("Versión académica MVP", fontWeight = FontWeight.Bold); Text("El catálogo, favoritos y solicitudes se guardan en Room/SQLite. La conexión remota queda preparada para los endpoints oficiales de la cátedra.") } }
    }
}

private fun sendCampaignNotification(context: Context) {
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val channelId = "lou_campaigns"
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) manager.createNotificationChannel(NotificationChannel(channelId, "Campañas y colecciones", NotificationManager.IMPORTANCE_DEFAULT).apply { description = "Novedades y campañas estacionales de macramé" })
    val notification = NotificationCompat.Builder(context, channelId).setSmallIcon(android.R.drawable.ic_dialog_info).setContentTitle("Lou Araya Macramé").setContentText("Descubre la colección artesanal para tu próximo evento.").setPriority(NotificationCompat.PRIORITY_DEFAULT).build()
    if (Build.VERSION.SDK_INT < 33 || ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) NotificationManagerCompat.from(context).notify(2026, notification)
}
