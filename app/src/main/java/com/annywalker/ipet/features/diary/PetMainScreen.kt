package com.annywalker.ipet.features.diary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.annywalker.ipet.core.designsystem.base.IPetBaseScreen
import com.annywalker.ipet.core.designsystem.components.IPetEmptyErrorBox
import com.annywalker.ipet.core.designsystem.components.IPetOptionChip
import com.annywalker.ipet.core.designsystem.components.IPetWeekWeekSelector
import com.annywalker.ipet.core.designsystem.dialogs.IPetDatePickerDialog
import com.annywalker.ipet.core.designsystem.dimensions.AppDimens
import com.annywalker.ipet.core.designsystem.typography.AppTypography
import com.annywalker.ipet.core.domain.model.SymptomDefinition

@Composable
fun PetSymptomScreen(viewModel: PetMainViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedPet by viewModel.selectedPet.collectAsState()
    val petList by viewModel.availablePets.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }

    IPetBaseScreen(
        title = "${selectedPet?.name} | Diário",
        selectedPet = selectedPet,
        availablePets = petList,
        onPetSelected = { viewModel.selectPetId(it.id) },
        floatingActionButton = null,
        content = { padding ->
            PetMainScreenContent(
                uiState = uiState,
                padding = padding,
                changeDates = viewModel::changeDate,
                onOptionSelected = viewModel::onOptionSelected,
                save = viewModel::save
            )
        },
        dialogContent = {
            if (showDatePicker && uiState is PetMainState.Success) {
                val successState = uiState as PetMainState.Success
                IPetDatePickerDialog(
                    initialDate = successState.selectedDate,
                    onDismissRequest = { showDatePicker = false },
                    onDateSelected = { viewModel.changeSpecificDate(it) }
                )
            }
        }
    )
}

@Composable
fun PetMainScreenContent(
    uiState: PetMainState,
    padding: PaddingValues,
    changeDates: (Long) -> Unit,
    onOptionSelected: (symptomId: String, optionId: String) -> Unit,
    save: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (uiState) {
            is PetMainState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(AppDimens.spacingLarge)
                        .align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            is PetMainState.Error -> {
                IPetEmptyErrorBox(messageText = uiState.message)
            }

            is PetMainState.Success -> {
                val successState = uiState as PetMainState.Success

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            horizontal = AppDimens.spacingLarge,
                            vertical = AppDimens.spacingMedium
                        )
                ) {
                    IPetWeekWeekSelector(
                        date = successState.selectedDate,
                        onPrevious = { changeDates(-1) },
                        onNext = { changeDates(1) }
                    )

                    Spacer(modifier = Modifier.height(AppDimens.spacingLarge))

                    SymptomForm(
                        symptomDefinitions = successState.symptomDefinitions,
                        selectedOptions = successState.selectedOptions,
                        onOptionSelected = { symptomId, optionId ->
                            onOptionSelected(symptomId, optionId)
                        }
                    )
                }

                FloatingActionButton(
                    onClick = { save() },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(AppDimens.spacingLarge),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = "Salvar",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun SymptomForm(
    symptomDefinitions: List<SymptomDefinition>,
    selectedOptions: Map<String?, String>,
    onOptionSelected: (symptomId: String, optionId: String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        symptomDefinitions.forEach { symptom ->
            Text(
                text = symptom.label,
                style = AppTypography.titleMedium,
                modifier = Modifier.padding(
                    vertical = AppDimens.spacingMedium,
                    horizontal = AppDimens.spacingSmall
                )
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .padding(bottom = AppDimens.spacingMedium),
                horizontalArrangement = Arrangement.spacedBy(AppDimens.spacingMedium),
                verticalArrangement = Arrangement.spacedBy(AppDimens.spacingMedium),
                userScrollEnabled = false
            ) {
                items(symptom.options, key = { it.id }) { option ->
                    val selected = selectedOptions[symptom.id] == option.id

                    IPetOptionChip(
                        optionLabel = option.label,
                        isSelected = selected,
                        onClick = { onOptionSelected(symptom.id, option.id) }
                    )
                }
            }
        }
    }
}