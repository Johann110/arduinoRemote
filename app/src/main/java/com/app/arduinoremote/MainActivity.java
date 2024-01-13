package com.app.arduinoremote;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    File folder;

    static boolean wifiIsPicked = true;
    List<String> remotes = new ArrayList<>();
    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create the .txt files for the remotes
        createFolder();
        folder = new File(String.valueOf(this.getExternalFilesDir("Remotes")));

        Button remoteConstructorBtn = findViewById(R.id.remoteConstructorBtn);

        remoteConstructorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToConstructorActivity();
            }
        });

        ActionBar actionBar = getSupportActionBar();
        // Top action bar
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.title_layout);
        }

        fillRemotePanel();
        askForBluetoothPermission();
    }

    private void createFolder(){
        File folder = new File(String.valueOf(this.getExternalFilesDir("Remotes")));
        if(!folder.exists()){
            folder.mkdir();
        }

    }

    private void switchToConstructorActivity(){
        Intent switchToConstructor = new Intent(this, ConstructorActivity.class);
        startActivity(switchToConstructor);
        finish();
    }

    private void fillRemotePanel(){
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout remotePanel = findViewById(R.id.remotePanel);

        remotes.clear();
        remotePanel.removeAllViews();
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 30);
        Drawable remoteButtonShape = ResourcesCompat.getDrawable(getResources(), R.drawable.shape_remote_buttons, getTheme());
        File[] files = folder.listFiles();
        for(int i = 0; i < files.length; i++){
            File file = files[i];
            remotes.add(file.getName().substring(0, file.getName().lastIndexOf(".")));
        }

        for(int i = 0; i < remotes.size(); i++){
            Space space = new Space(this);
            space.setLayoutParams(layoutParams1);
            Button button = new Button(this);
            button.setText(remotes.get(i));
            button.setBackground(remoteButtonShape);
            button.setLayoutParams(layoutParams2);
            button.setGravity(Gravity.CENTER);
            button.setAllCaps(false);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switchToRemoteActivity(button.getText().toString());
                }
            });

            remotePanel.addView(button);
            remotePanel.addView(space);
        }
    }

    private void switchToRemoteActivity(String remote){
        Intent switchToRemote = new Intent(this, RemoteActivity.class);
        switchToRemote.putExtra("remote", remote);
        startActivity(switchToRemote);
    }

    public void exitClick(View v){
        finish();
    }

    public void infoClick(View v){
        Intent switchToInfo = new Intent(this, InfoActivity.class);
        startActivity(switchToInfo);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void askForBluetoothPermission(){
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED) {
            // You can use the API that requires the permission.
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT},1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission granted", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}