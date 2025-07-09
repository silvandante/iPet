package com.annywalker.ipet.features.alarms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.annywalker.ipet.R
import com.annywalker.ipet.core.designsystem.base.IPetBaseScreen
import com.annywalker.ipet.core.designsystem.components.IPetCard
import com.annywalker.ipet.core.designsystem.components.IPetEmptyErrorBox
import com.annywalker.ipet.core.designsystem.dialogs.IPetAddAlarmDialog
import com.annywalker.ipet.core.designsystem.dimensions.AppDimens
import com.annywalker.ipet.core.domain.model.MedAlarm
import com.annywalker.ipet.core.util.formatTime
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun PetAlarmsScreen(viewModel: PetMedsViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val availablePets by viewModel.availablePets.collectAsState()
    val selectedPet by viewModel.selectedPet.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    var showAddAlarmDialog by remember { mutableStateOf(false) }

    IPetBaseScreen(
        title = "${selectedPet?.name} | Alarmes",
        selectedPet = selectedPet,
        availablePets = availablePets,
        onPetSelected = { pet -> viewModel.selectPetById(pet.id) },
        floatingActionButton = {
            selectedPet?.let {
                FloatingActionButton(
                    onClick = { showAddAlarmDialog = true },
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Medication Alarm",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        content = { padding ->
            PetAlarmsContent(
                uiState = uiState,
                padding = padding,
                deleteAlarm = viewModel::deleteAlarm
            )
        },
        dialogContent = {
            if (showAddAlarmDialog) {
                IPetAddAlarmDialog(
                    onDismiss = { showAddAlarmDialog = false },
                    onConfirm = { name, triggerTimeMillis ->
                        coroutineScope.launch {
                            val alarm = MedAlarm(
                                name = name,
                                time = triggerTimeMillis,
                                pet = selectedPet ?: return@launch,
                                id = UUID.randomUUID().toString()
                            )
                            viewModel.addAlarm(alarm)
                        }
                        showAddAlarmDialog = false
                    }
                )
            }
        }
    )
}

@Composable
fun PetAlarmsContent(
    uiState: PetAlarmUiState,
    padding: PaddingValues,
    deleteAlarm: (MedAlarm) -> Unit
) {
    when (uiState) {
        is PetAlarmUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is PetAlarmUiState.Error -> {
            val message = (uiState).message
            IPetEmptyErrorBox(messageText = message)
        }

        is PetAlarmUiState.Success -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(AppDimens.spacingMedium)
            ) {
                if (uiState.alarms.isEmpty()) {
                    IPetEmptyErrorBox(R.string.no_alarms_tap_to_add)
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(uiState.alarms, key = { it.id }) { alarm ->
                            IPetCard(
                                alarm.name,
                                alarm.time.formatTime(),
                                alarm,
                                removeCard = { deleteAlarm(it) }
                            )
                        }
                    }
                }
            }
        }
    }
}

