package com.annywalker.ipet.features.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.annywalker.ipet.R
import com.annywalker.ipet.core.designsystem.base.IPetBaseScreen
import com.annywalker.ipet.core.designsystem.components.IPetEmptyErrorBox
import com.annywalker.ipet.core.designsystem.dimensions.AppDimens
import com.annywalker.ipet.core.designsystem.typography.AppTypography
import com.annywalker.ipet.core.domain.model.Pet
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun PetReportScreen(viewModel: PetReportViewModel, logout: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedPet by viewModel.selectedPet.collectAsState()
    val availablePets by viewModel.availablePets.collectAsState()
    val isAddingPet by viewModel.isEditingPet.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    IPetBaseScreen(
        title = selectedPet?.name,
        selectedPet = selectedPet,
        availablePets = availablePets,
        addPet = viewModel::addPet,
        isAddingPet = isAddingPet,
        deletePet = viewModel::deletePet,
        onPetSelected = { pet -> viewModel.selectPetId(pet.id) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        logout = logout,
        errorDialog = {
            scope.launch {
                snackbarHostState.showSnackbar("Por favor, adicione doenças separadas por virgula")
            }
        },
        content = { padding ->
            when (uiState) {
                is PetReportUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is PetReportUiState.Error -> {
                    IPetEmptyErrorBox(
                        messageText = (uiState as PetReportUiState.Error).message
                    )
                }

                is PetReportUiState.Success -> {
                    PetReportContent(
                        selectedPet = selectedPet,
                        onGeneratePdf = {
                            scope.launch {
                                snackbarHostState.showSnackbar("Gerando pdf!")
                            }
                            viewModel.generatePdfReport()
                        },
                        addDiseaseToPet = viewModel::addDiseaseToPet,
                        padding = padding
                    )
                }
            }
        }
    )
}

@Composable
fun PetReportContent(
    selectedPet: Pet?,
    onGeneratePdf: () -> Unit = {},
    addDiseaseToPet: (String) -> Unit,
    padding: PaddingValues
) {
    Column(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .padding(AppDimens.spacingLarge),
        verticalArrangement = Arrangement.spacedBy(AppDimens.spacingLarge)
    ) {
        if (selectedPet == null) {
            IPetEmptyErrorBox(R.string.select_pet_to_details)
            return
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(AppDimens.elevationMedium)
        ) {
            Column(modifier = Modifier.padding(AppDimens.spacingLarge)) {
                val ageYears = selectedPet.birthday?.let { birthday ->
                    val now = LocalDate.now()
                    val dateBirthday = LocalDate.parse(birthday)
                    val years = now.year - dateBirthday.year -
                            if (now.dayOfYear < dateBirthday.dayOfYear) 1 else 0
                    years
                }

                InfoRow(
                    label = stringResource(R.string.age_label),
                    value = stringResource(
                        R.string.years,
                        ageYears ?: stringResource(R.string.unknown_age)
                    )
                )

                selectedPet.birthday?.let {
                    InfoRow(
                        label = stringResource(R.string.birthday_label),
                        value = it.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
                    )
                }
            }
        }

        PetDiseasesCard(
            selectedPet = selectedPet,
            onAddDisease = { disease ->
                addDiseaseToPet(disease)
            }
        )

        OutlinedButton(
            onClick = onGeneratePdf,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = AppDimens.spacingLarge),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(stringResource(R.string.generate_pdf))
        }
    }
}

@Composable
fun PetDiseasesCard(
    selectedPet: Pet,
    onAddDisease: (String) -> Unit
) {
    var newDisease by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(AppDimens.elevationMedium)
    ) {
        Column(modifier = Modifier.padding(AppDimens.spacingLarge)) {
            Text(
                text = stringResource(R.string.diaseasis_history),
                style = AppTypography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = AppDimens.spacingMedium)
            )

            if (selectedPet.diseases.isNullOrEmpty()) {
                Text(
                    text = stringResource(R.string.no_diseasis),
                    style = AppTypography.bodyMedium,
                    modifier = Modifier.padding(bottom = AppDimens.spacingMedium)
                )
            } else {
                selectedPet.diseases.forEach { disease ->
                    Text("• $disease", style = AppTypography.bodyMedium)
                }
                Spacer(Modifier.height(AppDimens.spacingMedium))
            }

            OutlinedTextField(
                value = newDisease,
                onValueChange = { newDisease = it },
                label = { Text("Nova doença") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (newDisease.isNotBlank()) {
                                onAddDisease(newDisease.trim())
                                newDisease = ""
                            }
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Adicionar doença")
                    }
                }
            )
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = AppDimens.spacingSmall)) {
        Text(
            text = label,
            style = AppTypography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = AppTypography.bodyLarge
        )
    }
}

