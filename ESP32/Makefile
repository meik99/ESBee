build:
	echo "#define WIFI_SSID \"${WIFI_SSID}\"" > include/wifi_credentials.h
	echo "#define WIFI_PASSWORD \"${WIFI_PASSWORD}\"" >> include/wifi_credentials.h
	pio run -e esp32dev

upload: build
	pio run --target upload -e esp32dev

clean:
	pio run --target clean -e esp32dev

monitor:
	 pio device monitor -e esp32dev
 
pio-test:
	pio test -v
