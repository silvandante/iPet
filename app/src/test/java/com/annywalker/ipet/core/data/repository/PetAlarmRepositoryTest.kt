package com.annywalker.ipet.core.data.repository

import com.annywalker.ipet.core.data.datasource.local.PetAlarmDataSource
import com.annywalker.ipet.core.domain.model.MedAlarm
import com.annywalker.ipet.core.domain.model.Pet
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class PetAlarmRepositoryTest {

    private val petAlarmDataSource: PetAlarmDataSource = mockk(relaxed = true)
    private lateinit var repository: PetAlarmRepository

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = PetAlarmRepository(petAlarmDataSource)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getAlarmsFlowForPet emits mapped list of MedAlarm`() = runTest {
        val petId = "pet123"
        val medAlarms = listOf(
            MedAlarm(
                id = "alarm1",
                name = "Alarm One",
                time = 1000L,
                pet = Pet(id = petId, name = "Nina")
            ),
            MedAlarm(
                id = "alarm2",
                name = "Alarm Two",
                time = 2000L,
                pet = Pet(id = "petB", name = "Mel")
            )
        )

        every { petAlarmDataSource.getAlarmsFlowForPet(petId) } returns flowOf(medAlarms)

        val result = repository.getAlarmsFlowForPet(petId).first()

        assertEquals(2, result.size)
        assertEquals("Alarm One", result[0].name)
        assertEquals(1000L, result[0].time)
        assertEquals(petId, result[0].pet.id)
    }

    @Test
    fun `addAlarm calls insertAlarm on DAO with correct entity`() = runTest {
        val alarm = MedAlarm(
            id = "a1",
            name = "Pill",
            time = 12345L,
            pet = Pet(id = "petA", name = "Nina")
        )

        coEvery { petAlarmDataSource.addAlarm(any()) } just Runs

        repository.addAlarm(alarm)

        coVerify {
            petAlarmDataSource.addAlarm(
                MedAlarm(
                    id = "a1",
                    name = "Pill",
                    time = 12345L,
                    pet = Pet(id = "petA", name = "Nina")
                )
            )
        }
    }

    @Test
    fun `removeAlarm calls deleteAlarmByFields on DAO with correct values`() = runTest {
        val alarm = MedAlarm(
            id = "a2",
            name = "Vaccine",
            time = 56789L,
            pet = Pet(id = "petB", name = "Tom")
        )

        coEvery { petAlarmDataSource.removeAlarm(any()) } just Runs

        repository.removeAlarm(alarm)

        coVerify {
            petAlarmDataSource.removeAlarm(alarm)
        }
    }

    @Test
    fun `removeAllAlarmsForPet calls DAO with correct petId`() = runTest {
        val petId = "petC"

        coEvery { petAlarmDataSource.removeAllAlarmsForPet(petId) } just Runs

        repository.removeAllAlarmsForPet(petId)

        coVerify { petAlarmDataSource.removeAllAlarmsForPet(petId) }
    }
}
