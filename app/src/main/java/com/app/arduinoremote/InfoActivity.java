package com.app.arduinoremote;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class InfoActivity extends AppCompatActivity {

    TextView wifiText1;
    TextView wifiText2;
    TextView wifiText3;
    TextView wifiText4;
    TextView wifiText5;
    TextView wifiText6;
    TextView wifiText7;
    TextView wifiText8;
    TextView wifiText9;
    TextView wifiText10;
    TextView wifiText11;

    TextView btText1;
    TextView btText2;
    TextView btText3;
    TextView btText4;
    TextView btText5;
    TextView btText6;
    TextView btText7;
    TextView btText8;
    TextView btText9;
    TextView btText10;
    TextView btText11;

    Button copyWiFiText2Btn;
    Button copyWiFiText4Btn;
    Button copyWiFiText6Btn;
    Button copyWiFiText8Btn;
    Button copyWiFiText10Btn;

    Button copyBTText2Btn;
    Button copyBTText4Btn;
    Button copyBTText6Btn;
    Button copyBTText8Btn;
    Button copyBTText10Btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        wifiText1 = findViewById(R.id.wifiText1);
        wifiText2 = findViewById(R.id.wifiText2);
        wifiText3 = findViewById(R.id.wifiText3);
        wifiText4 = findViewById(R.id.wifiText4);
        wifiText5 = findViewById(R.id.wifiText5);
        wifiText6 = findViewById(R.id.wifiText6);
        wifiText7 = findViewById(R.id.wifiText7);
        wifiText8 = findViewById(R.id.wifiText8);
        wifiText9 = findViewById(R.id.wifiText9);
        wifiText10 = findViewById(R.id.wifiText10);
        wifiText11 = findViewById(R.id.wifiText11);
        copyWiFiText2Btn = findViewById(R.id.copyWiFiText2Btn);
        copyWiFiText4Btn = findViewById(R.id.copyWiFiText4Btn);
        copyWiFiText6Btn = findViewById(R.id.copyWiFiText6Btn);
        copyWiFiText8Btn = findViewById(R.id.copyWiFiText8Btn);
        copyWiFiText10Btn = findViewById(R.id.copyWiFiText10Btn);

        btText1 = findViewById(R.id.btText1);
        btText2 = findViewById(R.id.btText2);
        btText3 = findViewById(R.id.btText3);
        btText4 = findViewById(R.id.btText4);
        btText5 = findViewById(R.id.btText5);
        btText6 = findViewById(R.id.btText6);
        btText7 = findViewById(R.id.btText7);
        btText8 = findViewById(R.id.btText8);
        btText9 = findViewById(R.id.btText9);
        btText10 = findViewById(R.id.btText10);
        btText11 = findViewById(R.id.btText11);
        copyBTText2Btn = findViewById(R.id.copyBTText2Btn);
        copyBTText4Btn = findViewById(R.id.copyBTText4Btn);
        copyBTText6Btn = findViewById(R.id.copyBTText6Btn);
        copyBTText8Btn = findViewById(R.id.copyBTText8Btn);
        copyBTText10Btn = findViewById(R.id.copyBTText10Btn);

        ActionBar actionBar = getSupportActionBar();
        // Top action bar
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.info_title_layout);
        }
    }

    public void backClick(View v){
        finish();
    }

    public void btManualClick(View v){
        wifiText1.setVisibility(View.GONE);
        wifiText2.setVisibility(View.GONE);
        wifiText3.setVisibility(View.GONE);
        wifiText4.setVisibility(View.GONE);
        wifiText5.setVisibility(View.GONE);
        wifiText6.setVisibility(View.GONE);
        wifiText7.setVisibility(View.GONE);
        wifiText8.setVisibility(View.GONE);
        wifiText9.setVisibility(View.GONE);
        wifiText10.setVisibility(View.GONE);
        wifiText11.setVisibility(View.GONE);
        copyWiFiText2Btn.setVisibility(View.GONE);
        copyWiFiText4Btn.setVisibility(View.GONE);
        copyWiFiText6Btn.setVisibility(View.GONE);
        copyWiFiText8Btn.setVisibility(View.GONE);
        copyWiFiText10Btn.setVisibility(View.GONE);

        btText1.setVisibility(View.VISIBLE);
        btText2.setVisibility(View.VISIBLE);
        btText3.setVisibility(View.VISIBLE);
        btText4.setVisibility(View.VISIBLE);
        btText5.setVisibility(View.VISIBLE);
        btText6.setVisibility(View.VISIBLE);
        btText7.setVisibility(View.VISIBLE);
        btText8.setVisibility(View.VISIBLE);
        btText9.setVisibility(View.VISIBLE);
        btText10.setVisibility(View.VISIBLE);
        btText11.setVisibility(View.VISIBLE);
        copyBTText2Btn.setVisibility(View.VISIBLE);
        copyBTText4Btn.setVisibility(View.VISIBLE);
        copyBTText6Btn.setVisibility(View.VISIBLE);
        copyBTText8Btn.setVisibility(View.VISIBLE);
        copyBTText10Btn.setVisibility(View.VISIBLE);
    }

    public void wifiManualClick(View v){
        btText1.setVisibility(View.GONE);
        btText2.setVisibility(View.GONE);
        btText3.setVisibility(View.GONE);
        btText4.setVisibility(View.GONE);
        btText5.setVisibility(View.GONE);
        btText6.setVisibility(View.GONE);
        btText7.setVisibility(View.GONE);
        btText8.setVisibility(View.GONE);
        btText9.setVisibility(View.GONE);
        btText10.setVisibility(View.GONE);
        btText11.setVisibility(View.GONE);
        copyBTText2Btn.setVisibility(View.GONE);
        copyBTText4Btn.setVisibility(View.GONE);
        copyBTText6Btn.setVisibility(View.GONE);
        copyBTText8Btn.setVisibility(View.GONE);
        copyBTText10Btn.setVisibility(View.GONE);

        wifiText1.setVisibility(View.VISIBLE);
        wifiText2.setVisibility(View.VISIBLE);
        wifiText3.setVisibility(View.VISIBLE);
        wifiText4.setVisibility(View.VISIBLE);
        wifiText5.setVisibility(View.VISIBLE);
        wifiText6.setVisibility(View.VISIBLE);
        wifiText7.setVisibility(View.VISIBLE);
        wifiText8.setVisibility(View.VISIBLE);
        wifiText9.setVisibility(View.VISIBLE);
        wifiText10.setVisibility(View.VISIBLE);
        wifiText11.setVisibility(View.VISIBLE);
        copyWiFiText2Btn.setVisibility(View.VISIBLE);
        copyWiFiText4Btn.setVisibility(View.VISIBLE);
        copyWiFiText6Btn.setVisibility(View.VISIBLE);
        copyWiFiText8Btn.setVisibility(View.VISIBLE);
        copyWiFiText10Btn.setVisibility(View.VISIBLE);
    }

    public void copyWiFiText2Click(View v){
        copy(wifiText2.getText().toString());
    }

    public void copyWiFiText4Click(View v){
        copy(wifiText4.getText().toString());
    }

    public void copyWiFiText6Click(View v){
        copy(wifiText6.getText().toString());
    }

    public void copyWiFiText8Click(View v){
        copy(wifiText8.getText().toString());
    }

    public void copyWiFiText10Click(View v){
        copy(wifiText10.getText().toString());
    }

    public void copyBTText2Click(View v){
        copy(btText2.getText().toString());
    }

    public void copyBTText4Click(View v){
        copy(btText4.getText().toString());
    }

    public void copyBTText6Click(View v){
        copy(btText6.getText().toString());
    }

    public void copyBTText8Click(View v){
        copy(btText8.getText().toString());
    }

    public void copyBTText10Click(View v){
        copy(btText10.getText().toString());
    }

    private void copy(String text){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Code", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(InfoActivity.this, "Copied", Toast.LENGTH_SHORT).show();
    }
}