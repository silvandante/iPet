package com.annywalker.ipet.core.designsystem.base

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.annywalker.ipet.core.designsystem.topbar.PetHealthTopBar
import com.annywalker.ipet.core.domain.model.Pet

@Composable
fun IPetBaseScreen(
    title: String?,
    modifier: Modifier = Modifier,
    selectedPet: Pet?,
    availablePets: List<Pet>,
    onPetSelected: (Pet) -> Unit,
    floatingActionButton: @Composable (() -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit,
    dialogContent: @Composable (() -> Unit)? = null,
    addPet: ((Pet) -> Unit)? = null,
    deletePet: ((Pet) -> Unit)? = null,
    isAddingPet: Boolean = false,
    snackbarHost: @Composable (() -> Unit)? = null,
    logout: (() -> Unit)? = null,
    errorDialog: (() -> Unit)? = null
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            PetHealthTopBar(
                currentPet = selectedPet,
                availablePets = availablePets,
                onPetSelected = onPetSelected,
                addPet = addPet,
                deletePet = deletePet,
                isAddingPet = isAddingPet,
                title = title,
                logout = logout,
                errorDialog = errorDialog
            )
        },
        floatingActionButton = floatingActionButton ?: {},
        snackbarHost = snackbarHost ?: {}
    ) { padding ->
        content(padding)

        dialogContent?.invoke()
    }
}