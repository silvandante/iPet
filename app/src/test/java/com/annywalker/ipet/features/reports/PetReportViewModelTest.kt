package com.annywalker.ipet.features.reports

import com.annywalker.ipet.core.data.repository.SymptomRepository
import com.annywalker.ipet.core.domain.model.Pet
import com.annywalker.ipet.core.domain.model.SymptomEntry
import com.annywalker.ipet.managers.FirebaseLoginManager
import com.annywalker.ipet.managers.PdfGeneratorManager
import com.annywalker.ipet.managers.PetSelectionManager
import com.google.firebase.auth.FirebaseUser
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class PetReportViewModelTest {

    private val repository: SymptomRepository = mockk(relaxed = true)
    private val petSelectionManager: PetSelectionManager = mockk(relaxed = true)
    private val pdfGeneratorManager: PdfGeneratorManager = mockk(relaxed = true)
    private val firebaseLoginManager: FirebaseLoginManager = mockk(relaxed = true)

    private lateinit var viewModel: PetReportViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { petSelectionManager.pets } returns MutableStateFlow(emptyList())
        every { petSelectionManager.selectedPet } returns MutableStateFlow(null)
        viewModel = PetReportViewModel(
            repository,
            petSelectionManager,
            pdfGeneratorManager,
            firebaseLoginManager
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init sets uiState to Loading`() = runTest {
        assertEquals(PetReportUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `addPet updates selectedPet and sets editing to false`() = runTest {
        val testPet =
            Pet(id = "1", name = "Luna", diseases = listOf(), birthday = "2020-01-01", userId = "")
        val user = mockk<FirebaseUser>()
        every { user.uid } returns "user123"
        every { firebaseLoginManager.getCurrentUser() } returns user
        coEvery { petSelectionManager.addPet(any()) } just Runs

        viewModel.addPet(testPet)
        advanceUntilIdle()

        assertFalse(viewModel.isEditingPet.value)
        assertEquals(testPet, viewModel.selectedPet.value)
    }

    @Test
    fun `deletePet triggers editing flag and calls delete`() = runTest {
        val testPet = Pet(
            id = "2",
            name = "Max",
            diseases = listOf(),
            birthday = "2019-05-10",
            userId = "user123"
        )
        coEvery { petSelectionManager.deletePet(testPet) } just Runs

        viewModel.deletePet(testPet)
        advanceUntilIdle()

        assertFalse(viewModel.isEditingPet.value)
        coVerify { petSelectionManager.deletePet(testPet) }
    }

    @Test
    fun `generatePdfReport calls generateInBackground with correct data`() = runTest {
        val testPet = Pet(
            id = "3",
            name = "Bidu",
            diseases = listOf("Allergy"),
            birthday = LocalDate.of(2018, 6, 15).toString(),
            userId = "user123"
        )
        val entries = listOf(
            SymptomEntry(
                petId = testPet.id,
                date = LocalDate.now().toString(),
                symptoms = mapOf(Pair("vomit", "yes"))
            )
        )

        every { petSelectionManager.selectedPet } returns MutableStateFlow(testPet)
        coEvery { repository.getSymptomsEntryForPetAllTime(testPet.id) } returns entries
        coEvery {
            pdfGeneratorManager.generateInBackground(
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } just Runs

        // Recreate viewModel to apply updated selectedPet
        viewModel = PetReportViewModel(
            repository,
            petSelectionManager,
            pdfGeneratorManager,
            firebaseLoginManager
        )

        viewModel.generatePdfReport()
        advanceUntilIdle()

        coVerify {
            pdfGeneratorManager.generateInBackground(
                entries = entries,
                petName = "Bidu",
                petAge = any(), // Could be calculated
                petBirthday = testPet.birthday,
                petDiseases = "[Allergy]"
            )
        }
    }

    @Test
    fun `addDiseaseToPet adds disease to pet and updates state`() = runTest {
        val pet = Pet(
            id = "4",
            name = "Rex",
            diseases = listOf(),
            birthday = "2021-04-12",
            userId = "userX"
        )
        val updatedPet = pet.copy(diseases = listOf("NewDisease"))

        every { petSelectionManager.selectedPet } returns MutableStateFlow(pet)
        coEvery { petSelectionManager.addDiseaseToPet("NewDisease", pet) } returns updatedPet

        viewModel = PetReportViewModel(
            repository,
            petSelectionManager,
            pdfGeneratorManager,
            firebaseLoginManager
        )

        viewModel.addDiseaseToPet("NewDisease")
        advanceUntilIdle()

        assertEquals(updatedPet, viewModel.selectedPet.value)
    }
}
