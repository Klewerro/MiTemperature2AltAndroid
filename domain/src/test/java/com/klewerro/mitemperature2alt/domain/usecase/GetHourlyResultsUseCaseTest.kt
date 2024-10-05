package com.klewerro.mitemperature2alt.domain.usecase

import com.klewerro.mitemperature2alt.coreTest.fake.FakeThermometerRepository
import com.klewerro.mitemperature2alt.domain.repository.HourlyRecordRepository
import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetHourlyResultsUseCaseTest {

    @RelaxedMockK
    private lateinit var hourlyRecordRepositoryMock: HourlyRecordRepository
    private lateinit var fakeThermometerRepository: FakeThermometerRepository
    private lateinit var getHourlyResultsUseCase: GetHourlyResultsUseCase

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        fakeThermometerRepository = FakeThermometerRepository()
        getHourlyResultsUseCase = GetHourlyResultsUseCase(
            thermometerRepository = fakeThermometerRepository,
            hourlyRecordRepository = hourlyRecordRepositoryMock
        )
    }

    @Test
    fun `invoke when thermometerRepository returns no object don't calling save database`() {
        fakeThermometerRepository.hourlyRecords = null
        runTest {
            this@GetHourlyResultsUseCaseTest.getHourlyResultsUseCase(this, "") {
                // Not used
            }
            coVerify(exactly = 0) { hourlyRecordRepositoryMock.saveHourRecords(any(), any()) }
        }
    }

    @Test
    fun `invoke when thermometerRepository returns list of hourlyRecords call saveHourRecords once`() {
        val insertedRecords = fakeThermometerRepository.hourlyRecords!!
        runTest {
            this@GetHourlyResultsUseCaseTest.getHourlyResultsUseCase(this, "") {
                // Not used
            }

            coVerify(exactly = 1) {
                hourlyRecordRepositoryMock.saveHourRecords(any(), insertedRecords)
            }
        }
    }
}
