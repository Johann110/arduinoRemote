![Static Badge](https://img.shields.io/badge/Android_version-8+-blue) &nbsp; ![Static Badge](https://img.shields.io/badge/License-MIT_license-80f75c)

<h1>Content</h1>

1. [Introduction](#introduction)
2. [Wi-Fi example](#wifi-example)
3. [Bluetooth example](#bluetooth-example)
5. [Notes](#notes)
6. [Troubleshooting](#troubleshooting)

<a id="introduction"></a><br>
# Introduction
With this application you can control microcontrollers (Arduino and the like) via Wi-Fi or Bluetooth.
You will no longer need to solder buttons, switches, potentiometers and LCD displays, thereby occupying pins on the controller.
In the application, you can create a remote control for your project and add controls there. While the application is running, each button/switch, etc. when clicked, sends its unique code.
All that remains is to program the reaction in the controller to the incoming code.

<a id="wifi-example"></a><br>
# The following example works with a WEMOS (Wi-Fi)
To control your microcontroller through a Wi-Fi access point, you must perform the following steps:

<b>Step 1:</b><br>
Launch a Wi-Fi hotspot on your controller.<br>
Code:
```cpp
#include <ESP8266WiFi.h>
#include <WiFiClient.h>
const char* ssid = "Wemos HotSpot";
const char* password = "12345678";
WiFiServer server(80); // Create a server on port 80
void setup() {
  Serial.begin(115200);
  WiFi.softAP(ssid, password);
  Serial.println();
  Serial.println("IP address: ");
  Serial.println(WiFi.softAPIP());
  server.begin(); // Start the server
}
void loop() {}
```

<b>Step 2:</b><br>
Turn on the serial monitor in the Arduino IDE. You will see the IP of your access point. You will need this IP when creating a remote control in the application. If you don't see the IP just restart your controller.

<b>Step 3:</b><br>
Click on "Remote constructor" - You have entered the remote constructor where you can create, delete and edit remotes.
Click on "Add new remote" to create a new remote. Click Wi-Fi. Enter the name of this remote (for example, the name of your project) and the IP address from Step 2. Click OK. Your new remote will appear on this tab. 
Click on it and the editor of this remote will open. Click on ADD at the top, select Button. Enter the name of the button (For example "X"), click ADD. Move the button to a convenient place for you.
Clicking the button will bring up the change menu for that button.

<b>Step 4:</b><br>
Add code to the loop function to read the button.<br>
Code:

```cpp
WiFiClient client = server.available(); // Listen for incoming clients
client.setTimeout(100);
if(client){
  String data = client.readStringUntil('\n'); // Read the data from the client
  data.trim();
  if(data != ""){
    Serial.println(data); // show what the controller sent
  }
}
```
<b>Step 5:</b><br>
Start your controller. On your phone, in Wi-Fi networks, find the new "Wemos HotSpot" hotspot. Connect to this hotspot.<br><br>
<b>Step 6:</b><br>
In the app, go to the first tab (the tab labeled "ARDUINO REMOTE") and select the remote from Step 3. Turn on the serial monitor and press your button. 
When a button is pressed, the serial monitor should show an individual code for this button - b0x1. When released, it will show - b0x0. If you see these codes, then the everything is working, the application sends codes and arduino receives them.<br><br>
You can also add a switch, a potentiometer and a text field.<br><br>
<b>The switch</b> sends 1 at the end of the individual code when clicked and 0 when clicked again.<br><br>
<b>The potentiometer</b> sends an individual code and at the end of the code the value.<br>
Code for reading the Potentiometer:<br>

```cpp
char *p;
char buffer[128];
String potentiometerParts[2];
String potentiometer = data;
potentiometer.toCharArray(buffer, sizeof(buffer));
int i = 0;
p = strtok(buffer, "x");
while(p && i < 2){
  potentiometerParts[i] = p;
  p = strtok(NULL, "x");
  ++i;
}
if(potentiometerParts[0] != ""){
  //getting the code of the potentiometer
  String potentiometerCode = potentiometerParts[0];
  Serial.println("code: " + potentiometerCode);
  //getting the value of the potentiometer
  int potentiometerValue = potentiometerParts[1].toInt();
  Serial.println("value: " + String(potentiometerValue));
}
```
<b>The text field</b> is used to receive information from the arduino. For the text field to work, add the variable String sendValue = ""; over the setup() function.<br>
Sending data option:<br>
```cpp
if (data == "code of the desired button"){
  sendValue = "Hello World!";
}
client.print(sendValue);
```
> [!IMPORTANT]
>  This code needs to be inside if(client){}
<br>
<b>Usage example:</b><br>
Create a remote and add controls in the following order:<br>
1. Switch<br>
2. Button<br>
3. Text field<br>
4. Potentiometer min = 1 max = 3
5. Potentiometer min = 0 max = 500<br><br>

Now copy and paste this code:<br>
```cpp
#include <ESP8266WiFi.h>
#include <WiFiClient.h>
const char* ssid = "Wemos HotSpot";
const char* password = "12345678";
String sendValue = "";
WiFiServer server(80); // Create a server on port 80
void setup() {
  pinMode(LED_BUILTIN, OUTPUT);
  Serial.begin(115200);
  WiFi.softAP(ssid, password);
  Serial.println();
  Serial.println("IP address: ");
  Serial.println(WiFi.softAPIP());
  server.begin(); // Start the server
}
void loop() {
  WiFiClient client = server.available(); // Listen for incoming clients
  client.setTimeout(100);
  if (client) {
    String data = client.readStringUntil('\n'); // Read the data from the client
    data.trim();
    if(data != ""){
      Serial.println(data);
    }
    // Switch
    if (data == "s0x1") {
      digitalWrite(LED_BUILTIN, LOW); // LED on
    } else if (data == "s0x0") {
      digitalWrite(LED_BUILTIN, HIGH); // LED off
    }
    // Button
    if (data == "b1x1") {
      sendValue = "Hello";
    } else if (data == "b1x0") {
      sendValue = "Bye bye";
    }
    //Potentiometer 1 - 3
    char *p;
    char buffer[128];
    String potentiometerParts[2];
    String potentiometer = data;
    potentiometer.toCharArray(buffer, sizeof(buffer));
    int i = 0;
    p = strtok(buffer, "x");
    while (p && i < 2) {
      potentiometerParts[i] = p;
      p = strtok(NULL, "x");
      ++i;
    }
    if (potentiometerParts[0] == "p3") {
        int potentiometerValue = potentiometerParts[1].toInt();
      if (potentiometerValue == 1) {
        sendValue = "Going slow";
      } else if(potentiometerValue == 2){
        sendValue = "Going faster";
      } else if(potentiometerValue == 3){
        sendValue = "Going fast";
      }
    }
    // potentiometer 0 - 500
    potentiometer = data;
    potentiometer.toCharArray(buffer, sizeof(buffer));
    i = 0;
    p = strtok(buffer, "x");
    while (p && i < 2) {
      potentiometerParts[i] = p;
      p = strtok(NULL, "x");
      ++i;
    }
    if (potentiometerParts[0] == "p4") {
      String potentiometerValue = potentiometerParts[1];
      sendValue = "Value: " + potentiometerValue;
    }
    // send data to textfield
    client.print(sendValue);
  }
}
```

<a id="bluetooth-example"></a><br>
# The following example works with the ESP-WROOM-32 (Bluetooth)
To control your microcontroller via Bluetooth, you must perform the following steps:<br><br>
<b>Step 1:</b><br>
Upload the code to your controller to start Bluetooth.<br>
Code:<br>

```cpp
#include "BluetoothSerial.h"
BluetoothSerial SerialBT;
String data = "";
void setup() {
  Serial.begin(115200);
  // Bluetooth device name
  SerialBT.begin("ESP32"); // you can enter any name you want
  Serial.println("Bluetooth started");
}
void loop() { }
```

<b>Step 2:</b><br>
Turn on the serial monitor in the Arduino IDE, restart the controller and you should see Bluetooth has started.<br><br>
<b>Step 3:</b><br>
Add your Bluetooth device to the list of Bluetooth devices on your phone (pair).<br><br>
<b>Step 4:</b><br>
Click on "Remote constructor" - You have entered the remote constructor where you can create, delete and edit remotes. Click on "Add new remote" to create a new remote.
Click on Bluetooth. Enter the name of this remote (for example, the name of your project) and the name of the Bluetooth device (in the example above, it is ESP32). Click OK.
Your new remote will appear on this tab. Click on it and the editor of this remote will open. Click on ADD at the top, select <b>Button</b>. Enter a name for the button (eg "X"), press ADD.
Move the button to a convenient place for you. Clicking on the button will bring up the change menu for that button.<br><br>
<b>Step 5:</b><br>
Add code to the loop function to read the button.<br>
Code:
```cpp
void loop() {
  data = "";
  // Read received messages
  if (SerialBT.available()) {
    delay(1); // pause the controller for reading the incoming data correctly
    while (SerialBT.available()) {
    char incomingChar = SerialBT.read();
      if (incomingChar != '\n'){
        data += String(incomingChar);
      }
    }
  }
  // this is how you can check which code the remote is sending
  if (data != "") {
    Serial.println(data);
  }
}
```
<b>Step 6:</b><br>
In the application, go to the first tab (the tab labeled "ARDUINO REMOTE") and select the remote you created from Step 4. Turn on the port monitor and press your button.
When the button is pressed, the port monitor should receive an individual code for this button - b0x1. When released, it will receive - b0x0.
If you see these codes, then everything is working, the application sends codes and arduino receives them.<br><br>
You can also add a switch, a potentiometer and a text field.<br><br>
<b>The switch</b> sends 1 at the end of the individual code when clicked and 0 when clicked again.<br><br>
<b>The potentiometer</b> sends an individual code and at the end of the code the value.<br>
Code for reading the Potentiometer:<br>

```cpp
char *p;
char buffer[128];
String potentiometerParts[2];
String potentiometer = data;
potentiometer.toCharArray(buffer, sizeof(buffer));
int i = 0;
p = strtok(buffer, "x");
while (p && i < 2) {
  potentiometerParts[i] = p;
  p = strtok(NULL, "x");
  ++i;
}
if (potentiometerParts[0] != "") {
  //getting the code of the potentiometer
  String potentiometerCode = potentiometerParts[0];
  Serial.println("code: " + potentiometerCode);
  //getting the value of the potentiometer
  int potentiometerValue = potentiometerParts[1].toInt();
  Serial.println("value: " + String(potentiometerValue));
}
```
<b>The text field</b> is used to receive information from the arduino. To make the text field work, we call the bluetoothPrintLine() function. Write a string you want to send in the brackets.<br>
> [!IMPORTANT]  
> there must be an asterisk "*" at the end of the string, this is a stop character so that the application detects the end of the message.
<br>

Example:
```cpp
// in loop function
if (data == "b0x1") {
  bluetoothPrintLine("Hello World!*");
}

// bluetoothPrintLine function
void bluetoothPrintLine(String line) {
  unsigned l = line.length();
  for (int i = 0; i < l; i++) {
    if (line[i] != '\0') {
      SerialBT.write(byte(line[i]));
    }
  }
}
```

<b>Usage example:</b><br>
create a remote and add controls in the following order:<br>
1. Switch<br>
2. Button<br>
3. Text field<br>
4. Potentiometer min = 1 max = 3<br>
5. Potentiometer min = 0 max = 500<br>

Now copy and paste the following code:<br>
```cpp
#include "BluetoothSerial.h"
BluetoothSerial SerialBT;
String data = "";
void setup() {
  pinMode(2, OUTPUT); // Builtin LED
  Serial.begin(115200);
  // Bluetooth device name
  SerialBT.begin("ESP32"); // you can enter any name you want
  Serial.println("Bluetooth started");
}
void loop() {
  data = "";
  // Read received messages
  if (SerialBT.available()) {
    delay(1); // pause the controller for reading the incoming data correctly
    while(SerialBT.available()){
      char incomingChar = SerialBT.read();
      if (incomingChar != '\n'){
        data += String(incomingChar);
      }
    }
  }
  if (data == "s0x1") {
    digitalWrite(2, HIGH); // LED on
  } else if (data == "s0x0"){
    digitalWrite(2, LOW); // LED off
  }

  if (data == "b1x1") {
    bluetoothPrintLine("Hello*");
  } else if (data == "b1x0") {
    bluetoothPrintLine("Bye bye*");
  }

  char *p;
  char buffer[128];
  String potentiometerParts[2];
  String potentiometer = data;
  potentiometer.toCharArray(buffer, sizeof(buffer));
  int i = 0;
  p = strtok(buffer, "x");
  while(p && i < 2){
    potentiometerParts[i] = p;
    p = strtok(NULL, "x");
    ++i;
  }
  if (potentiometerParts[0] == "p3") {
    int potentiometerValue = potentiometerParts[1].toInt();
    if(potentiometerValue == 1){
      bluetoothPrintLine("Going slow*");
    } else if (potentiometerValue == 2) {
      bluetoothPrintLine("Going faster*");
    } else if (potentiometerValue == 3) {
      bluetoothPrintLine("Going fast*");
    }
  }

  potentiometer = data;
  potentiometer.toCharArray(buffer, sizeof(buffer));
  i = 0;
  p = strtok(buffer, "x");
  while (p && i < 2) {
    potentiometerParts[i] = p;
    p = strtok(NULL, "x");
    ++i;
  }
  if (potentiometerParts[0] == "p4") {
    String potentiometerValue = potentiometerParts[1];
    bluetoothPrintLine("Value: " + potentiometerValue + "*");
  }
}

void bluetoothPrintLine(String line) {
  unsigned l = line.length();
  for (int i = 0; i < l; i++) {
    if (line[i] != '\0') {
      SerialBT.write(byte(line[i]));
    }
  }
}
```

<a id="notes"></a><br>
# Notes
Try to avoid using the delay() function, since while delay is running,
receiving commands does not work. Instead of delay, it is better to use a millis timer.
To do this, create a variable uint32_t tmr1; above setup() then add the following code:<br>

```cpp
if (millis() - tmr1 >= 500) { // 500 milliseconds
  tmr1 = millis(); // reset timer
  //Serial.println("my timer is working!"); // here you can check that it works if you want
}
```

You can add line breaks for your textfield text by adding "#br#" to the send string.<br>
In case there are more text than the textfield can show, you can scroll down to see the rest.<br>

If you just want to install the app, I added a APK file "arduinoRemote.apk" for the installation.

All remotes are saved in .txt files in:<br>
Android -> data -> com.app.arduinoremote -> files -> Remotes<br><br>
If you have Android 13+ you cannot access this files directly without root permissions. However it is possible by connecting your device to a PC.
> [!CAUTION]
> I do not recommend to do changes in the .txt files manually, because it can lead to errors and unexpected behaviour.

<a id="troubleshooting"></a><br>
# Troubleshooting
<b>Can't connect to the controller via Bluetooth:</b><br>
Check if Bluetooth has started on the controller, by starting the controller and check if the message "Bluetooth started" appears in the port monitor. If you don't see this message restart your controller.
Make sure you added the BT device to the BT devicelist in your phone.
If the app still not connecting and throwing the error "Not able to connect. Cause may be the remote name", check if the name in your controller, set
in SerialBT.begin(); matches with the given device name in the remote. The names are case sensitive, which means that it is important wheter the letters are uppercase or lowercase.
To check the name in the app open the remote in the constructor and click the settings button.
<br><br>
If the name matches in the app and the in controller, but there is still no connection, try to delete the controller from the Bluetooth device list in your phone and add it again.


<b>Can't connect to the controller via Wi-Fi:</b><br>
Make sure you connected your phone to the hotspot of the controller. If so, check wheter the ip given in the remote matches with the ip showing in the port monitor of the controller.
The ip is showed in the port monitor right after the controller starts. If the ip is not showing restart your controller. To change the ip in the app open the remote in the remote constructor and click the
settings button.

<b>While connecting via Wi-Fi the app is showing that it is connected but the controller does not react:</b><br>
If the app is showing that you are connected, but the controller is not receiving anything, you probably connected to the wrong Wi-Fi.
If you are at home your phone probably switched to the home Wi-Fi.

<b>The app is not showing any text sent by the controller via Bluetooth:</b><br>
Make sure you added the asterisk "*" at the end of the message.

<b>The controller did not recognized the code from the switch/button:</b><br>
Your controller is probably busy with other stuff, because of that it receives multiple incoming codes and concatenates them together.
To be sure the controller reacts even if the codes are stacked up, you can just check if the incoming data <b>contains</b>
the desired code instead of checking whether the code <b>is equal to</b> the incoming data.<br>
Like so:
```cpp
if (data.indexOf("myCode") != -1) {
  // do stuff
}
```
