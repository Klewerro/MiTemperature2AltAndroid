# MiTemperature2Alt
An alternative application for reading data from the Mi Temeprature & Humidity Monitor 2 and its configuration.

<img src="./files/%2Fthermometer_image.webp" align="right" height="220" width="210"> 

### Libraries used:
- Jetpack Compose
- Dagger hilt
- [Nordic Kotlin BLE library (scanner and client)](https://github.com/NordicSemiconductor/Kotlin-BLE-Library)

|![Screenshot1](files%2Fscreenshots%2FScreenshot1.png)|![Screenshot2](files%2Fscreenshots%2FScreenshot2.png)|![Screenshot3](files%2Fscreenshots%2FScreenshot3.png)|
|---|---|---|
|![Screenshot4](files%2Fscreenshots%2FScreenshot4.png)|![Screenshot5](files%2Fscreenshots%2FScreenshot5.png)|   |

### Todo:
- [ ] Saving thermometer and automatic reconnect after application start
- [ ] MVI
- [ ] Getting hourly saved data

### Currently implemented features:
- [x] Scanning for BLE thermometers
- [x] Connecting to BLE thermometer
- [x] Reading current device status (temperature, humidity, voltage)
- [x] Subscribing for current device status updates
- [x] Multi-module project structure