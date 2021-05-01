//
// Created by kani on 10.04.21.
//
#include <WiFi.h>
#include "wlan.h"
#include "colors.h"

const char* hostname = "remote-display";
const char* ssid = "MyWlan";
const char* password = "ChangeMe";

WiFiClient espClient;

void setup_wlan(Adafruit_SSD1331* display) {
    delay(10);
    display->fillScreen(BLACK);
    display->setCursor(20,5);
    display->setTextColor(WHITE);
    display->setTextSize(1);

    WiFiClass::mode(WIFI_STA);
    WiFi.setHostname(hostname);
    WiFi.begin(ssid, password);

    display->print("Connect");

    while (WiFiClass::status() != WL_CONNECTED) {
        display->setCursor(60,5);
        display->print(".");
        delay(300);
        display->print(".");
        delay(300);
        display->print(".");
    }

    display->fillScreen(BLACK);
    display->setTextColor(GREEN);
    display->setCursor(25,25);
    display->print("Connected");
    delay(300);
}