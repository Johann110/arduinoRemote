package com.app.arduinoremote;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.res.ResourcesCompat;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.FileUtils;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class ConstructorActivity extends AppCompatActivity {
    LinearLayout layout;
    int wifiPicked = 0;
    String deviceIP = "";
    String deviceName = "";
    File folder;
    List<String> remotes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.constructor_layout);

        folder = new File(String.valueOf(this.getExternalFilesDir("Remotes")));

        Button addBtn = findViewById(R.id.addBtn);

        layout = findViewById(R.id.layout);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddRemoteDialog();
            }
        });

        // Top action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.constructor_title_layout);
        }

        fillRemotePanel();
    }

    private void showAddRemoteDialog(){
        Dialog dialog = new Dialog(ConstructorActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.add_dialog_layout);

        Button wifiBtn = dialog.findViewById(R.id.wifiBtn);
        Button bluetoothBtn = dialog.findViewById(R.id.bluetoothBtn);
        Button okBtn = dialog.findViewById(R.id.okBtn);

        TextView connectionTypeTextView = dialog.findViewById(R.id.connectionTypeTextView);

        TextView remoteNameTextView = dialog.findViewById(R.id.remoteNameTextView);
        EditText remoteNameEditText = dialog.findViewById(R.id.remoteNameEditText);
        
        TextView deviceIPTextView = dialog.findViewById(R.id.deviceIPTextView);
        EditText deviceIPEditText = dialog.findViewById(R.id.deviceIPEditText);

        TextView deviceNameTextView = dialog.findViewById(R.id.deviceNameTextView);
        EditText deviceNameEditText = dialog.findViewById(R.id.deviceNameEditText);

        wifiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deviceNameTextView.setVisibility(View.GONE);
                deviceNameEditText.setVisibility(View.GONE);
                deviceIPTextView.setVisibility(View.VISIBLE);
                deviceIPEditText.setVisibility(View.VISIBLE);
                wifiPicked = 1;
            }

        });

        bluetoothBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deviceIPTextView.setVisibility(View.GONE);
                deviceIPEditText.setVisibility(View.GONE);
                deviceNameTextView.setVisibility(View.VISIBLE);
                deviceNameEditText.setVisibility(View.VISIBLE);
                wifiPicked = 2;
            }
        });

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String remoteName = remoteNameEditText.getText().toString();
                // check for no remote name input
                if(remoteName.contains("|")){
                    createSnackBar("The symbol '|' is forbidden");
                    dialog.dismiss();
                    return;
                }
                if (remoteName.equals("")){
                    animation(remoteNameTextView);
                }

                if(wifiPicked == 0){
                    animation(connectionTypeTextView);
                }

                if(wifiPicked == 1){
                    deviceIP = deviceIPEditText.getText().toString();
                    if(deviceIP.contains("|")){
                        createSnackBar("The symbol '|' is forbidden");
                        dialog.dismiss();
                        return;
                    }
                    // check for no IP input
                    if(deviceIP.equals("")){
                        animation(deviceIPTextView);
                    }
                    // if everything is entered -> save remote
                    if(!remoteName.equals("") && !deviceIP.equals("")){
                        createNewRemote(remoteName, deviceIP, 0);
                        dialog.dismiss();
                    }
                } else if(wifiPicked == 2){
                    deviceName = deviceNameEditText.getText().toString();
                    if(deviceName.contains("|")){
                        createSnackBar("The symbol '|' is forbidden");
                        dialog.dismiss();
                        return;
                    }
                    // check for no device name input
                    if(deviceName.equals("")){
                        animation(deviceNameTextView);
                    }
                    // if everything is entered -> save remote
                    if(!remoteName.equals("") && !deviceName.equals("")){
                        createNewRemote(remoteName, deviceName, 1);
                        dialog.dismiss();
                    }
                }
            }
        });

        dialog.show();
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
            button.setLayoutParams(layoutParams2);
            button.setBackground(remoteButtonShape);
            button.setGravity(Gravity.CENTER);
            button.setAllCaps(false);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switchToConstructRemoteActivity(button.getText().toString());
                }
            });
            button.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showDeleteDialog(button.getText().toString());
                    return true;
                }
            });

            remotePanel.addView(button);
            remotePanel.addView(space);
        }
    }


    private void createNewRemote(String filename, String data, int type){
        byte[] writeData = data.getBytes();
        if (type == 0){
            filename = filename + " - WiFi";
        } else {
            filename = filename + " - BT";
        }
        File file = new File(folder, filename + ".txt");
        String fileName = file.getName().substring(0, file.getName().lastIndexOf("."));

        if(file.exists()){
            createSnackBar("Remote " + fileName + " already exists");
            return;
        }

        try {
            FileOutputStream fos = new FileOutputStream(file, true);
            fos.write(writeData);
            fos.write("\n".getBytes(StandardCharsets.UTF_8));
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        fillRemotePanel();
    }

    private void animation(TextView title) {
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(100); // blinking time
        animation.setStartOffset(20);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setRepeatCount(4);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                title.setTextColor(getColor(R.color.red));
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                title.setTextColor(getColor(R.color.black));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        title.startAnimation(animation);
    }

    private void createSnackBar(String text) {
        Snackbar snack = Snackbar.make(layout, text, Snackbar.LENGTH_INDEFINITE).setMaxInlineActionWidth(127);
        View view = snack.getView();
        TextView snackTextView = (TextView) view.findViewById(com.google.android.material.R.id.snackbar_text);
        snackTextView.setSingleLine(false);
        snackTextView.setTextSize(20);
        TextView textAction = (TextView) view.findViewById(com.google.android.material.R.id.snackbar_action);
        textAction.setTextSize(30);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.gravity = Gravity.TOP;
        view.setLayoutParams(params);
        snack.setAction("Ok", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snack.dismiss();
            }
        });
        snack.show();
    }

    private void showDeleteDialog(String remoteName){
        Dialog dialog = new Dialog(ConstructorActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.delete_dialog_layout);

        TextView deleteTextTextView = dialog.findViewById(R.id.deleteTextTextView);
        deleteTextTextView.setText("Delete " + remoteName + "?");

        Button yesBtn = dialog.findViewById(R.id.yesBtn);
        Button noBtn = dialog.findViewById(R.id.noBtn);

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteRemote(remoteName);
                dialog.dismiss();
            }
        });

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void deleteRemote(String remoteName){
        File file = new File(folder, remoteName + ".txt");
        if(file.exists()){
            file.delete();
        }
        fillRemotePanel();
    }

    private void switchToConstructRemoteActivity(String remote){
        Intent switchToConstructRemote = new Intent(this, ConstructRemoteActivity.class);
        switchToConstructRemote.putExtra("remote", remote);
        startActivity(switchToConstructRemote);
    }

    public void backClick(View view){
        startActivity(new Intent(ConstructorActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        // do what you want to do when the "back" button is pressed.
        startActivity(new Intent(ConstructorActivity.this, MainActivity.class));
        finish();
    }
}