package com.annywalker.ipet.core.designsystem.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.annywalker.ipet.R
import com.annywalker.ipet.core.designsystem.dialogs.IPetAddPetDialog
import com.annywalker.ipet.core.domain.model.Pet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetHealthTopBar(
    currentPet: Pet?,
    availablePets: List<Pet>,
    onPetSelected: (Pet) -> Unit,
    onCalendarClick: (() -> Unit)? = null,
    addPet: ((Pet) -> Unit)? = null,
    deletePet: ((Pet) -> Unit)? = null,
    isAddingPet: Boolean = false,
    title: String?,
    logout: (() -> Unit)? = null,
    errorDialog: (() -> Unit)? = null
) {
    var showAddPetDialog by remember { mutableStateOf(false) }

    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(isAddingPet) {
        if (!isAddingPet && showAddPetDialog) {
            showAddPetDialog = false
        }
    }

    TopAppBar(
        title = {
            val screenTitle =
                currentPet?.name?.let { title } ?: stringResource(R.string.add_pet_title)
            Text(screenTitle)
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        actions = {
            if (availablePets.isNotEmpty()) {
                Box {
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowDropDown,
                            contentDescription = "Select Pet"
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        availablePets.forEach { pet ->
                            DropdownMenuItem(
                                text = { Text(pet.name) },
                                onClick = {
                                    expanded = false
                                    onPetSelected(pet)
                                }
                            )
                        }
                    }
                }
            }

            onCalendarClick?.let {
                IconButton(onClick = onCalendarClick) {
                    Icon(imageVector = Icons.Default.DateRange, contentDescription = "Select Date")
                }
            }

            addPet?.let {
                IconButton(onClick = { showAddPetDialog = true }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "add pet")
                }
            }
            deletePet?.let {
                if (availablePets.isNotEmpty()) {
                    IconButton(onClick = { currentPet?.let { deletePet(currentPet) } }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "delete pet")
                    }
                }
            }

            logout?.let {
                IconButton(onClick = { logout() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "delete pet"
                    )
                }
            }
        }
    )

    if (showAddPetDialog) {
        IPetAddPetDialog(
            onDismiss = { showAddPetDialog = false },
            onAdd = { newPet ->
                if (addPet != null) {
                    addPet(newPet)
                }
            },
            errorDialog = errorDialog ?: {}
        )
    }

    if (isAddingPet) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x80000000))
                .clickable(enabled = false) {},
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}