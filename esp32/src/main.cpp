#include <Adafruit_GFX.h>
#include <Adafruit_SSD1331.h>
#include <wlan.h>
#include <sys/socket.h>
#include <lwip/netdb.h>
#include "colors.h"

#define sclk GPIO_NUM_18
#define mosi GPIO_NUM_23
#define cs   GPIO_NUM_32
#define rst  GPIO_NUM_27
#define dc   GPIO_NUM_25

// 96 x 64
#define PIXEL_AMOUNT 12288
Adafruit_SSD1331 display = Adafruit_SSD1331(cs, dc, mosi, sclk, rst);
int con;

int create_ipv4_socket()
{
    struct addrinfo hints {};
    struct addrinfo *res;
    struct in_addr *addr;

    hints.ai_family = AF_INET;
    hints.ai_socktype = SOCK_STREAM;

    int err = getaddrinfo("<domain of the java server>", "9800", &hints, &res);

    if(err != 0 || res == nullptr) {
        return -1;
    }

    /* Code to print the resolved IP.
        Note: inet_ntoa is non-reentrant, look at ipaddr_ntoa_r for "real" code */
    addr = &((struct sockaddr_in *)res->ai_addr)->sin_addr;
    printf("DNS lookup succeeded. IP=%s\n", inet_ntoa(*addr));

    int l_sock = socket(res->ai_family, res->ai_socktype, 0);
    if(l_sock < 0) {
        freeaddrinfo(res);
        return -1;
    }

    struct timeval to {};
    to.tv_sec = 2;
    to.tv_usec = 0;
    setsockopt(l_sock, SOL_SOCKET, SO_SNDTIMEO, &to, sizeof(to));

    if(connect(l_sock, res->ai_addr, res->ai_addrlen) != 0) {
        close(l_sock);
        freeaddrinfo(res);
        return -1;
    }

    freeaddrinfo(res);
    return l_sock;
}

uint8_t buffer[PIXEL_AMOUNT];

void setup() {
    display.begin();
    display.fillScreen(BLACK);
    display.setCursor(20,5);
    display.setTextColor(WHITE);
    display.setTextSize(1);

    setup_wlan(&display);

    con = create_ipv4_socket();
    delay(1000);
}

void show(const char* title, int value) {
    display.fillScreen(BLACK);
    display.setCursor(0,5);

    display.setTextColor(WHITE);
    display.setTextSize(1);
    display.write(title);

    display.setTextColor(RED);
    display.setTextSize(3);
    auto val = String(value);
    auto x = (int16_t)((display.width() - 16 * val.length()) / 2);
    display.setCursor(x,20);
    display.write(val.c_str());
}

void loop() {
    int currentIndex = 0;
    int dataToRead = PIXEL_AMOUNT;
    int dataLeft = 0;
    while(dataToRead > 0) {
        dataLeft = recv(con, &buffer[currentIndex], dataToRead, 0);
        dataToRead -= dataLeft;
        currentIndex += dataLeft;
        if(errno != 0) {
            break;
        }
    }

    if(errno == 0) {
        display.startWrite();
        display.setAddrWindow(0, 0, 96, 64);
        display.writePixels(reinterpret_cast<uint16_t *>(buffer), PIXEL_AMOUNT / 2);
        display.endWrite();
    } else {
        show("No connection", errno);
        close(con);
        sleep(1);
        errno = 0;
        con = create_ipv4_socket();
    }
}