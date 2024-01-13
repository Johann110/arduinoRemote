package com.app.arduinoremote;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class RemoteActivity extends AppCompatActivity {
    public static final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    String remote = "";
    String device = "";
    int b = 0;
    String wifiReadingData = "";
    String bluetoothReadingData = "";

    RelativeLayout layout;
    boolean wifiConnection = true;

    Thread thread;
    Thread readDataThread;
    //String data = "";

    // remote element arrays
    List<UserButton> userButtons = new ArrayList<>();
    List<TextView> textViewsForButtons = new ArrayList<>();

    List<SeekBar> potis = new ArrayList<>();
    List<UserPoti> userPotis = new ArrayList<>();
    List<TextView> textViewsForPotis = new ArrayList<>();

    BluetoothManager bluetoothManager;
    BluetoothAdapter bluetoothAdapter;
    Set<BluetoothDevice> devices;
    BluetoothDevice BTdevice = null;
    BluetoothSocket socket;

    List<String> split = new ArrayList<>();

    TextView buttonTextView;
    TextView switchTextView;
    TextView potiTextView;
    TextView textField;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remote_layout);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            remote = extras.getString("remote");
        }

        // Top action bar
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setTitle(remote + ": connected");


        File myFile = new File(String.valueOf(this.getExternalFilesDir("Remotes")), remote + ".txt");
        try {
            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fIn));
            device = bufferedReader.readLine();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        wifiConnection = remote.endsWith("WiFi");

        if (!wifiConnection) {
            bluetoothManager = getSystemService(BluetoothManager.class);
            bluetoothAdapter = bluetoothManager.getAdapter();
            if (!bluetoothAdapter.isEnabled()) {
                Toast.makeText(getApplicationContext(), "Bluetooth is off", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            devices = bluetoothAdapter.getBondedDevices();

            if (devices.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please pair a device first", Toast.LENGTH_SHORT).show();
                finish();
                return;
            } else {
                for (BluetoothDevice iterator : devices) {
                    if (iterator.getName().equals(device)) { //Replace with iterator.getName() if comparing Device names.{
                        BTdevice = iterator; // device is an object of type BluetoothDevice
                        break;
                    }
                }
            }
            /*
            socket = null;
            try {
                socket = BTdevice.createRfcommSocketToServiceRecord(PORT_UUID);
                socket.connect();
            } catch (NullPointerException | IOException e) {
                Toast.makeText(getApplicationContext(),"Cannot connect", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

             */
        } else {
            WifiManager wifi = (WifiManager) getSystemService(RemoteActivity.WIFI_SERVICE);
            if (!wifi.isWifiEnabled()) {
                Toast.makeText(getApplicationContext(), "WiFi is disabled", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

        }

        layout = findViewById(R.id.layout);
        setRemoteContent();


        // if there is a TextField, start listen for information
        try {
            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fIn));
            String line;

            int firstLine = 0;
            while ((line = bufferedReader.readLine()) != null) {
                if (firstLine == 0) {
                    firstLine++;
                    continue;
                }
                split = Arrays.asList(line.split("\\|"));
                if (split.get(0).equals("textfield")) {
                    final Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (wifiConnection) {
                                    startReadingWifi();
                                } else {
                                    startReadingBT();
                                }
                            } catch (Exception e) {
                                // TODO: handle exception
                                System.out.println("fail");
                            } finally {
                                //also call the same runnable to call it at regular interval
                                handler.postDelayed(this, 300);
                            }
                        }
                    };
                    handler.post(runnable);
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setRemoteContent() {
        layout.removeAllViews();
        textViewsForButtons.clear();
        userButtons.clear();
        File myFile = new File(String.valueOf(this.getExternalFilesDir("Remotes")), remote + ".txt");

        // userButtons and userSwitches
        try {
            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fIn));
            String line;

            int runCount = 0;
            int firstLine = 0;
            while ((line = bufferedReader.readLine()) != null) {
                if (firstLine == 0) {
                    firstLine++;
                    continue;
                }

                split = Arrays.asList(line.split("\\|"));
                if (split.size() != 6) {
                    continue;
                }

                if (split.get(1).charAt(0) == 'b') { // buttons
                    UserButton userButton = new UserButton(split.get(0), split.get(1), split.get(2), split.get(3), split.get(4), split.get(5));
                    userButtons.add(userButton);

                    String posXstr = userButtons.get(runCount).getPosX();
                    String posYstr = userButtons.get(runCount).getPosY();
                    String size = userButtons.get(runCount).getSize();
                    String rotation = userButtons.get(runCount).getRotation();

                    buttonTextView = new TextView(this);
                    textViewsForButtons.add(buttonTextView);

                    textViewsForButtons.get(runCount).setX(Float.parseFloat(posXstr));
                    textViewsForButtons.get(runCount).setY(Float.parseFloat(posYstr));
                    textViewsForButtons.get(runCount).setGravity(Gravity.CENTER);
                    textViewsForButtons.get(runCount).setTextColor(Color.BLUE);
                    textViewsForButtons.get(runCount).setText(userButtons.get(runCount).getName());

                    if (size.equals("0")) {
                        textViewsForButtons.get(runCount).setBackgroundResource(R.drawable.buttonunpressed50x50);
                    } else if (size.equals("1")) {
                        textViewsForButtons.get(runCount).setBackgroundResource(R.drawable.buttonunpressed80x80);
                    } else {
                        textViewsForButtons.get(runCount).setBackgroundResource(R.drawable.buttonunpressed120x120);
                    }
                    textViewsForButtons.get(runCount).setRotation(Integer.parseInt(rotation));
                    int finalI = runCount;
                    textViewsForButtons.get(runCount).setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) { // send data
                            switch (motionEvent.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                    if (wifiConnection) {
                                        sendOverWifi(userButtons.get(finalI).getCode(), "1");
                                    } else {
                                        sendOverBT(userButtons.get(finalI).getCode(), "1");
                                    }
                                    if (size.equals("0")) {
                                        textViewsForButtons.get(finalI).setBackgroundResource(R.drawable.buttonpressed50x50);
                                    } else if (size.equals("1")) {
                                        textViewsForButtons.get(finalI).setBackgroundResource(R.drawable.buttonpressed80x80);
                                    } else {
                                        textViewsForButtons.get(finalI).setBackgroundResource(R.drawable.buttonpressed120x120);
                                    }
                                    break;

                                case MotionEvent.ACTION_UP:
                                    if (wifiConnection) {
                                        sendOverWifi(userButtons.get(finalI).getCode(), "0");
                                    } else {
                                        sendOverBT(userButtons.get(finalI).getCode(), "0");
                                    }
                                    if (size.equals("0")) {
                                        textViewsForButtons.get(finalI).setBackgroundResource(R.drawable.buttonunpressed50x50);
                                    } else if (size.equals("1")) {
                                        textViewsForButtons.get(finalI).setBackgroundResource(R.drawable.buttonunpressed80x80);
                                    } else {
                                        textViewsForButtons.get(finalI).setBackgroundResource(R.drawable.buttonunpressed120x120);
                                    }
                            }
                            return true;
                        }
                    });
                    layout.addView(textViewsForButtons.get(runCount));
                } else if (split.get(1).charAt(0) == 's') { // switches
                    UserButton userSwitch = new UserButton(split.get(0), split.get(1), split.get(2), split.get(3), split.get(4), split.get(5));
                    userButtons.add(userSwitch);

                    String posXstr = userButtons.get(runCount).getPosX();
                    String posYstr = userButtons.get(runCount).getPosY();
                    String size = userButtons.get(runCount).getSize();
                    String rotation = userButtons.get(runCount).getRotation();

                    switchTextView = new TextView(this);
                    textViewsForButtons.add(switchTextView);

                    textViewsForButtons.get(runCount).setX(Float.parseFloat(posXstr));
                    textViewsForButtons.get(runCount).setY(Float.parseFloat(posYstr));
                    textViewsForButtons.get(runCount).setGravity(Gravity.CENTER);
                    textViewsForButtons.get(runCount).setTextColor(Color.BLUE);
                    textViewsForButtons.get(runCount).setText(userButtons.get(runCount).getName());

                    if (size.equals("0")) {
                        textViewsForButtons.get(runCount).setBackgroundResource(R.drawable.switchunpressed100x50);
                    } else if (size.equals("1")) {
                        textViewsForButtons.get(runCount).setBackgroundResource(R.drawable.switchunpressed160x80);
                    } else {
                        textViewsForButtons.get(runCount).setBackgroundResource(R.drawable.switchunpressed240x120);
                    }

                    textViewsForButtons.get(runCount).setRotation(Integer.parseInt(rotation));

                    int finalI = runCount;
                    textViewsForButtons.get(runCount).setOnClickListener(new View.OnClickListener() {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        @Override
                        public void onClick(View view) {
                            int currentResource = getDrawableResourceForSize(size, false);
                            int pressedResource = getDrawableResourceForSize(size, true);
                            // unpressed to pressed
                            if (textViewsForButtons.get(finalI).getBackground().getConstantState() == getResources().getDrawable(currentResource).getConstantState()) {
                                textViewsForButtons.get(finalI).setBackgroundResource(pressedResource);
                                sendButtonStateOverConnection(userButtons.get(finalI).getCode(), "1");
                            } else { // pressed to unpressed
                                textViewsForButtons.get(finalI).setBackgroundResource(currentResource);
                                sendButtonStateOverConnection(userButtons.get(finalI).getCode(), "0");
                            }
                        }
                    });
                    layout.addView(textViewsForButtons.get(runCount));
                }
                runCount++;
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        try {
            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fIn));
            String line;

            int runCount = 0;
            int firstLine = 0;
            while ((line = bufferedReader.readLine()) != null) {
                if (firstLine == 0) {
                    firstLine++;
                    continue;
                }

                split = Arrays.asList(line.split("\\|"));
                if (split.size() != 8) {
                    continue;
                }

                if (split.get(1).charAt(0) == 'p') {
                    UserPoti userPoti = new UserPoti(split.get(0), split.get(1), split.get(2), split.get(3), split.get(4), split.get(5), split.get(6), split.get(7));
                    userPotis.add(userPoti);

                    String posXstr = userPotis.get(runCount).getPosX();
                    String posYstr = userPotis.get(runCount).getPosY();
                    String widthStr = userPotis.get(runCount).getWidth();
                    String heightStr = userPotis.get(runCount).getHeight();
                    String minStr = userPotis.get(runCount).getMin();
                    String maxStr = userPotis.get(runCount).getMax();

                    potiTextView = new TextView(this);
                    potiTextView.setX(Float.parseFloat(posXstr));
                    potiTextView.setY(Float.parseFloat(posYstr) - 60);
                    potiTextView.setText(userPotis.get(runCount).getName());
                    textViewsForPotis.add(potiTextView);

                    SeekBar poti = new SeekBar(this);
                    poti.setX(Float.parseFloat(posXstr));
                    poti.setY(Float.parseFloat(posYstr));
                    poti.setMin(Integer.parseInt(minStr));
                    poti.setMax(Integer.parseInt(maxStr));
                    poti.setBackgroundColor(Color.rgb(222, 230, 154));
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(Integer.parseInt(widthStr), Integer.parseInt(heightStr));
                    poti.setLayoutParams(lp);
                    potis.add(poti);
                    textViewsForPotis.get(runCount).setText(userPotis.get(runCount).getName() + ": 0");
                    int finalRunCount = runCount;
                    potis.get(runCount).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                            textViewsForPotis.get(finalRunCount).setText(userPotis.get(finalRunCount).getName() + ": " + progress);
                            seekBarClicked(String.valueOf(progress), false, userPotis.get(finalRunCount).getCode());
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                            seekBarClicked(String.valueOf(seekBar.getProgress()), true, userPotis.get(finalRunCount).getCode());
                        }
                    });
                    layout.addView(potis.get(runCount));
                    layout.addView(textViewsForPotis.get(runCount));
                }
                runCount++;
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        try {
            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fIn));
            String line;

            int firstLine = 0;
            while ((line = bufferedReader.readLine()) != null) {
                if (firstLine == 0) {
                    firstLine++;
                    continue;
                }
                split = Arrays.asList(line.split("\\|"));
                if (split.get(1).charAt(0) == 't') {
                    String posXstr = split.get(2);
                    String posYstr = split.get(3);
                    String widthStr = split.get(4);
                    String heightStr = split.get(5);
                    String rotation = split.get(6);

                    textField = new TextView(this);
                    textField.setTextColor(Color.BLACK);
                    textField.setBackgroundColor(Color.rgb(141, 243, 252));
                    textField.setX(Float.parseFloat(posXstr));
                    textField.setY(Float.parseFloat(posYstr));
                    textField.setWidth(Integer.parseInt(widthStr));
                    textField.setHeight(Integer.parseInt(heightStr));
                    textField.setRotation(Integer.parseInt(rotation));
                    textField.setVerticalScrollBarEnabled(true);
                    textField.setMovementMethod(new ScrollingMovementMethod());
                    layout.addView(textField);
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void sendButtonStateOverConnection(String code, String state) {
        if (wifiConnection) {
            sendOverWifi(code, state);
        } else {
            sendOverBT(code, state);
        }
    }

    private int getDrawableResourceForSize(String size, boolean isPressed) {
        int resourceId;
        if (isPressed) {
            switch (size) {
                case "0":
                    resourceId = R.drawable.switchpressed100x50;
                    break;
                case "1":
                    resourceId = R.drawable.switchpressed160x80;
                    break;
                default:
                    resourceId = R.drawable.switchpressed240x120;
                    break;
            }
        } else {
            switch (size) {
                case "0":
                    resourceId = R.drawable.switchunpressed100x50;
                    break;
                case "1":
                    resourceId = R.drawable.switchunpressed160x80;
                    break;
                default:
                    resourceId = R.drawable.switchunpressed240x120;
                    break;
            }
        }
        return resourceId;
    }

    private void sendOverWifi(String code, String value) {
        String data = code + value;
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Connect to the ESP32's IP address and port
                Socket socket = null;
                try {
                    //wsocket = new Socket(device, 80);
                    //wsocket.connect(new InetSocketAddress(device, 80), 500);
                    socket = new Socket(device, 80);
                    PrintWriter out = new PrintWriter(socket.getOutputStream());

                    // Send the data
                    out.println(data);
                    out.flush();

                    // Close the connection
                    out.close();
                    socket.close();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            ActionBar ab = getSupportActionBar();
                            assert ab != null;
                            ab.setTitle(remote + ": connected");
                        }
                    });
                } catch (UnknownHostException e) {
                    new Thread() {
                        public void run() {
                            RemoteActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Wrong IP", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }.start();
                    finish();
                } catch (IOException e) {
                    new Thread() {
                        public void run() {
                            RemoteActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            ActionBar ab = getSupportActionBar();
                                            assert ab != null;
                                            ab.setTitle(remote + ": not connected");
                                        }
                                    });
                                }
                            });
                        }
                    }.start();
                }
            }
        }).start();
    }

    @SuppressLint("MissingPermission")
    private void sendOverBT(String code, String value) {
        String data = code + value;
        Thread thread = new Thread() {
            public void run() {

                OutputStream outputStream = null;
                try {
                    if (socket == null) {
                        socket = BTdevice.createRfcommSocketToServiceRecord(PORT_UUID);
                        socket.connect();
                    } else {
                        outputStream = socket.getOutputStream();
                        outputStream.write(data.getBytes());
                    }
                } catch ( IOException e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            ActionBar ab = getSupportActionBar();
                            assert ab != null;
                            ab.setTitle(remote + ": not connected");
                        }
                    });
                } catch (NullPointerException e){
                    Log.wtf("nullpointer", "null");
                }
            }
        };
        thread.start();

    }

    private void seekBarClicked(String value, boolean lastSend, String potiCode) {
        if (lastSend) {
            if (wifiConnection) {
                sendOverWifi(potiCode, value);
            } else {
                sendOverBT(potiCode, value);
            }
            return;
        }
        if (thread != null) {
            if (thread.isAlive()) {
                return;
            }
        }

        thread = new Thread() {
            public void run() {
                if (wifiConnection) {
                    sendOverWifi(potiCode, value);
                } else {
                    sendOverBT(potiCode, value);
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };
        thread.start();
    }

    @SuppressLint("MissingPermission")
    private void startReadingBT() {
        if (readDataThread != null) {
            if (readDataThread.isAlive()) {
                return;
            }
        }

        readDataThread = new Thread() {
            public void run() {
                InputStream inputStream = null;
                try {
                    if (socket == null) {
                        socket = BTdevice.createRfcommSocketToServiceRecord(PORT_UUID);
                        socket.connect();
                    } else {
                        inputStream = socket.getInputStream();
                        while (true) {
                            byte incomingBytes = (byte) inputStream.read();
                            String incomingData = Character.toString((char) incomingBytes);
                            if (incomingData.equals("*")) {
                                break;
                            }
                            if (incomingData.equals("")) {
                                break;
                            }
                            //System.out.println(bluetoothReadingData);
                            bluetoothReadingData += incomingData;
                        }
                        textField.setText(bluetoothReadingData);
                        bluetoothReadingData = "";
                    }
                } catch (IOException e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            ActionBar ab = getSupportActionBar();
                            assert ab != null;
                            ab.setTitle(remote + ": not connected");
                        }
                    });
                } catch (NullPointerException e) {
                    Log.wtf("nullpointer", "null");
                }
            }
        };
        readDataThread.start();
    }

    private void startReadingWifi(){
        if (thread != null){
            if (thread.isAlive()){
                return;
            }
        }
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket(device, 80);
                    InputStream inputStream = socket.getInputStream();
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(inputStream));
                    String data1 = "";
                    data1 = br.readLine();
                    String finalData = data1;
                    textField.setText(finalData);

                    // Close the connection
                    inputStream.close();
                    runOnUiThread(new Runnable() {
                        public void run(){
                            ActionBar ab = getSupportActionBar();
                            assert ab != null;
                            ab.setTitle(remote + ": connected");
                        }
                    });
                } catch (IOException e) {
                    runOnUiThread(new Runnable() {
                        public void run(){
                            textField.setText("No connection");
                        }
                    });
                    runOnUiThread(new Runnable() {
                        public void run(){
                            ActionBar ab = getSupportActionBar();
                            assert ab != null;
                            ab.setTitle(remote + ": not connected");
                        }
                    });

                }
            }
        });
        thread.start();

    }


    @Override
    public void onBackPressed() {
        if (socket != null){
            if(socket.isConnected()){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        finish();
    }
}