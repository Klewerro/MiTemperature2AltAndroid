# MiTemperature2Alt
An alternative application for reading data from the Mi Temeprature & Humidity Monitor 2 and its configuration.

<img src="./files/%2Fthermometer_image.webp" align="right" height="220" width="210"> 

### Libraries used:
- Jetpack Compose
- Dagger hilt
- [Nordic Kotlin BLE library (scanner and client)](https://github.com/NordicSemiconductor/Kotlin-BLE-Library)

|![Screenshot1](files/screenshots/1.png)|![Screenshot2](files/screenshots/2.png)|![Screenshot3](files/screenshots/3.png)|
|---|---|---|
|![Screenshot4](files/screenshots/4.png)|![Screenshot5](files/screenshots/5.png)|![Screenshot6](files/screenshots/6.png)|
|![Screenshot7](files/screenshots/7.png)|![Screenshot8](files/screenshots/8.png)|![Screenshot9](files/screenshots/9.png)|





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