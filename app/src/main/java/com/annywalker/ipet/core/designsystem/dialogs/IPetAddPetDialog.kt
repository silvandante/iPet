package com.annywalker.ipet.core.designsystem.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import com.annywalker.ipet.R
import com.annywalker.ipet.core.designsystem.dimensions.AppDimens
import com.annywalker.ipet.core.designsystem.shapes.AppShapes
import com.annywalker.ipet.core.designsystem.typography.AppTypography
import com.annywalker.ipet.core.domain.model.Pet
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

@Composable
fun IPetAddPetDialog(
    onDismiss: () -> Unit,
    onAdd: (Pet) -> Unit,
    errorDialog: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var birthday by remember { mutableStateOf<LocalDate?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var diseasesText by remember { mutableStateOf("") }

    val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

    if (showDatePicker) {
        IPetDatePickerDialog(
            initialDate = birthday ?: LocalDate.now(),
            onDismissRequest = { showDatePicker = false },
            onDateSelected = {
                birthday = it
                showDatePicker = false
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Adicionar novo pet",
                style = AppTypography.titleMedium,
                modifier = Modifier.padding(bottom = AppDimens.spacingSmall)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(AppDimens.spacingMedium)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.pet_name)) },
                    leadingIcon = { Icon(Icons.Default.Pets, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = birthday?.format(dateFormatter) ?: "",
                        onValueChange = {},
                        label = { Text(stringResource(R.string.birthday_date)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null
                            )
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.EditCalendar,
                                contentDescription = stringResource(R.string.select_date)
                            )
                        },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .pointerInput(Unit) {}
                    )

                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { showDatePicker = true }
                    )
                }

                OutlinedTextField(
                    value = diseasesText,
                    onValueChange = { diseasesText = it },
                    label = { Text(stringResource(R.string.diseasis_comma_separate)) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.MedicalServices,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    try {
                        val diseases = diseasesText.split(",")
                            .map { it.trim() }
                            .filter { it.isNotEmpty() }

                        val pet = Pet(
                            id = UUID.randomUUID().toString(),
                            name = name,
                            birthday = birthday?.toString(),
                            diseases = diseases
                        )
                        onAdd(pet)
                        onDismiss()
                    } catch (e: Exception) {
                        errorDialog()
                    }
                },
                enabled = name.isNotBlank()
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        shape = AppShapes.small
    )
}