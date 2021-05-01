# ESP32 Streamer

This project is a very simple proof of concept that shows how to stream images to an ESP32. 
It uses basic socket (port 9600) functionallity and a buffered image on java side that will be converted to RGB565 and transfered.
On ESP32 side its a SSD1331 Display with the Adafruit library to show the images.

Possible use cases:
- Show Gifs
- Show static images
- Show video streams
- Show screen share (more or less its just an 96x64 display :D)

I use it to show the current corona statistics for my region and add a Gif in behind the information.

How to setup the display you can read in the Adafruit tutorials.
How to programm the ESP32 you can use the PIO environment and tutorials.
How to run the Java program it uses maven...

Sorry for this short and cheap readme. 
My motivation is gone for this project.
 
Have fun to adapt to your problems :)
