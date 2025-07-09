package com.annywalker.ipet.core.data.repository

import com.annywalker.ipet.core.data.datasource.remote.PetDataSource
import com.annywalker.ipet.core.domain.model.Pet
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class PetRepositoryTest {

    private val petDataSource: PetDataSource = mockk()
    private lateinit var repository: PetRepository

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = PetRepository(petDataSource)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getPets returns list of pets`() = runTest {
        val mockPets = listOf(
            Pet(id = "1", name = "Luna", diseases = listOf(), userId = "user123"),
            Pet(id = "2", name = "Bidu", diseases = listOf("Allergy"), userId = "user123")
        )

        coEvery { petDataSource.getPets() } returns mockPets

        val result = repository.getPets()

        assertEquals(2, result.size)
        assertEquals("Luna", result[0].name)
        assertEquals("Bidu", result[1].name)
    }

    @Test
    fun `addPet calls data source with correct pet`() = runTest {
        val pet = Pet(id = "3", name = "Max", diseases = emptyList(), userId = "userX")

        coEvery { petDataSource.addPet(pet) } just Runs

        repository.addPet(pet)

        coVerify { petDataSource.addPet(pet) }
    }

    @Test
    fun `deletePet calls data source with correct pet`() = runTest {
        val pet = Pet(id = "4", name = "Nina", diseases = emptyList(), userId = "userY")

        coEvery { petDataSource.deletePet(pet) } just Runs

        repository.deletePet(pet)

        coVerify { petDataSource.deletePet(pet) }
    }

    @Test
    fun `addDiseaseToPet calls data source and returns updated pet`() = runTest {
        val petId = "5"
        val newDisease = "Arthritis"
        val updatedPet =
            Pet(id = petId, name = "Rex", diseases = listOf(newDisease), userId = "userZ")

        coEvery { petDataSource.addDiseaseToPet(newDisease, petId) } returns updatedPet

        val result = repository.addDiseaseToPet(newDisease, petId)

        assertNotNull(result)
        assertEquals("Rex", result?.name)
        assertTrue(result?.diseases?.contains("Arthritis") == true)
    }
}
