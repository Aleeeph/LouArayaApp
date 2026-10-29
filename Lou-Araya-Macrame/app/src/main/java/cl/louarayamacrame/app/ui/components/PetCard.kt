@file:OptIn(ExperimentalMaterial3Api::class)

package cl.louarayamacrame.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cl.louarayamacrame.app.data.local.CareTaskEntity
import cl.louarayamacrame.app.data.local.PetEntity
import cl.louarayamacrame.app.util.Formatters

@Composable
fun PetCard(
    pet: PetEntity,
    nextTask: CareTaskEntity?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            PetAvatar(photoPath = pet.photoPath, size = 72.dp)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(pet.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                val breed = if (pet.breed.isNotBlank()) " · ${pet.breed}" else ""
                Text(
                    "${pet.species}$breed · ${Formatters.ageText(pet.birthDateMillis)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(8.dp))
                val overdue = nextTask != null && nextTask.dueMillis < System.currentTimeMillis()
                AssistChip(
                    onClick = onClick,
                    colors = when {
                        overdue -> AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            labelColor = MaterialTheme.colorScheme.onErrorContainer,
                            leadingIconContentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                        nextTask != null -> AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            labelColor = MaterialTheme.colorScheme.onTertiaryContainer,
                            leadingIconContentColor = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        else -> AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            labelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            leadingIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    },
                    border = null,
                    label = {
                        Text(
                            text = if (nextTask != null) {
                                "${nextTask.type} · ${Formatters.formatDateTime(nextTask.dueMillis)}"
                            } else {
                                "Sin cuidados pendientes"
                            },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = if (nextTask != null) Icons.Rounded.Event else Icons.Rounded.CheckCircle,
                            contentDescription = null
                        )
                    }
                )
            }
        }
    }
}
