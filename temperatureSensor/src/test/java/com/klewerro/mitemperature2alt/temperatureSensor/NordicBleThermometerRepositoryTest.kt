package com.klewerro.mitemperature2alt.temperatureSensor

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotEqualTo
import assertk.assertions.isTrue
import com.klewerro.mitemperature2alt.coreTest.fake.FakeThermometerDevicesBleScanner
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class NordicBleThermometerRepositoryTest {

    private lateinit var fakeThermometerDevicesBleScanner: FakeThermometerDevicesBleScanner
    private lateinit var nordicBleThermometerRepository: NordicBleThermometerRepository

    private val mac1 by lazy { fakeThermometerDevicesBleScanner.mac1 }
    private val mac2 by lazy { fakeThermometerDevicesBleScanner.mac2 }

    @BeforeEach
    fun setUp() {
        fakeThermometerDevicesBleScanner = FakeThermometerDevicesBleScanner()
        nordicBleThermometerRepository = NordicBleThermometerRepository(
            fakeThermometerDevicesBleScanner
        )
    }

    // region scanForDevices
    @Test
    fun `scanForDevices is working only as long, as job (coroutineContext) is not cancelled`() {
        runTest {
            val testJob = launch {
                nordicBleThermometerRepository.scannedDevices.test {
                    val empty = awaitItem()
                    nordicBleThermometerRepository.scanForDevices(this)

                    val item1 = awaitItem()
                    val item2 = awaitItem()
                    awaitComplete()

                    assertThat(item1.size).isEqualTo(1)
                    assertThat(item2.size).isEqualTo(2)
                }
            }
            delay(2000)
            testJob.cancel()
        }
    }

    @Test
    fun `scanForDevices is working only as long, as job (coroutineContext) is not cancelled (longer)`() {
        runTest {
            val testJob = launch {
                nordicBleThermometerRepository.scannedDevices.test {
                    val empty = awaitItem()
                    nordicBleThermometerRepository.scanForDevices(this)

                    val item1 = awaitItem()
                    val item2 = awaitItem()
                    val item3 = awaitItem()
                    val item4 = awaitItem()

                    assertThat(item1.size).isEqualTo(1)
                    assertThat(item2.size).isEqualTo(2)
                    assertThat(item3.size).isEqualTo(2)
                    assertThat(item4.size).isEqualTo(2)

                    assertThat(item2[0].rssi).isNotEqualTo(item4[0].rssi)
                    assertThat(item2[1].rssi).isNotEqualTo(item4[1].rssi)

                    awaitComplete()
                }
            }
            delay(5200)
            testJob.cancel()
        }
    }

    @Test
    fun `scanForDevices updating isScanningForDevices stateFlow`() {
        runTest {
            val testJob = launch {
                nordicBleThermometerRepository.isScanningForDevices.test {
                    val item1 = awaitItem()
                    nordicBleThermometerRepository.scanForDevices(this)
                    val item2 = awaitItem()
                    val item3 = awaitItem()
                    fakeThermometerDevicesBleScanner.isScanCancelledFlag = true

                    assertThat(item1).isFalse()
                    assertThat(item2).isTrue()
                    assertThat(item3).isFalse()
                    awaitComplete()
                }
            }
            delay(2100)
            testJob.cancel()
        }
    }

    @Test
    fun `scanForDevices when job is cancelled before 2nd emission, return only 1 device in scannedDevices flow`() {
        runTest {
            val testJob = launch {
                nordicBleThermometerRepository.scannedDevices.test {
                    awaitItem()
                    nordicBleThermometerRepository.scanForDevices(this)
                    val item1 = awaitItem()
                    awaitComplete()
                    assertThat(item1.size).isEqualTo(1)
                }
            }
            delay(1200)
            testJob.cancel()
        }
    }
    // endregion

    // region connectToDevice
    @Test
    fun `connectToDevice when already connecting to other device throws exception`() {
        runTest {
            launch { nordicBleThermometerRepository.connectToDevice(this, mac1) }

            launch {
                val thrownIllegalStateException = assertThrows<IllegalStateException> {
                    nordicBleThermometerRepository.connectToDevice(this, mac2)
                }
                assertThat(
                    thrownIllegalStateException.message
                ).isEqualTo("Already connecting to other device!")
            }
            // }
        }
    }

    @Test
    fun `connectToDevice when already connecting to the same device throws exception`() {
        runTest {
            launch { nordicBleThermometerRepository.connectToDevice(this, mac1) }

            launch {
                val thrownIllegalStateException = assertThrows<IllegalStateException> {
                    nordicBleThermometerRepository.connectToDevice(this, mac1)
                }
                assertThat(
                    thrownIllegalStateException.message
                ).isEqualTo("Already connecting to other device!")
            }
        }
    }

    @Test
    fun `connectToDevice updates connectingToDeviceAddress stateFlow during connecting process`() =
        runTest {
            nordicBleThermometerRepository.connectingToDeviceAddress.test {
                val initialEmission = awaitItem()
                nordicBleThermometerRepository.connectToDevice(this, mac1)
                val addressChangedEmission = awaitItem()
                val addressClearedEmission = awaitItem()

                assertThat(addressChangedEmission).isEqualTo(mac1)
                assertThat(addressClearedEmission).isEqualTo("")
            }
        }

    @Test
    fun `connectToDevice when already connecting to the same device don't set 2nd device mac address to the connectingToDeviceAddress stateFlow`() {
        runTest {
            nordicBleThermometerRepository.connectingToDeviceAddress.test {
                val initialEmission = awaitItem()
                launch { nordicBleThermometerRepository.connectToDevice(this, mac1) }
                val addressChangedEmission = awaitItem()
                launch {
                    try {
                        nordicBleThermometerRepository.connectToDevice(this, mac2)
                    } catch (_: java.lang.IllegalStateException) {
                    }
                }
                val addressClearedEmission = awaitItem()
                assertThat(addressChangedEmission).isEqualTo(mac1)
                assertThat(addressClearedEmission).isEqualTo("")
            }
        }
    }

    @Test
    fun `connectToDevice after connection when readThermometerStatus is success, status is added to stateFlow map under address key`() {
        runTest {
            nordicBleThermometerRepository.connectedDevicesStatuses.test {
                val initialEmission = awaitItem()
                nordicBleThermometerRepository.connectToDevice(this, mac1)
                val statusItem = awaitItem()

                val expectedResult = mapOf(
                    mac1 to fakeThermometerDevicesBleScanner.readThermometerStatus
                )
                assertThat(statusItem).isEqualTo(expectedResult)
            }
        }
    }
    // endregion
}
