build:
	echo "#define WIFI_SSID \"${WIFI_SSID}\"" > include/wifi_credentials.h
	echo "#define WIFI_PASSWORD \"${WIFI_PASSWORD}\"" >> include/wifi_credentials.h
	pio run

upload: build
	pio run --target upload

clean:
	pio run --target clean

monitor:
	 pio device monitor