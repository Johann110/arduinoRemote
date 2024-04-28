package com.app.arduinoremote;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class ConstructRemoteActivity extends AppCompatActivity {
    RelativeLayout layout;
    String remote;

    // option buttons
    LinearLayout optionButtonPanel;
    LinearLayout optionPotiPanel;
    LinearLayout optionTextFieldPanel;

    String selectedElement = "";
    TextView selectedElementTextView;

    UserTextField userTextField;
    // creating code
    List<String> split = new ArrayList<>();
    String code = "";
    List<Integer> codeNumbers = new ArrayList<>();

    // ImageView and textView for the elements
    TextView buttonTextView;
    TextView switchTextView;
    TextView potiTextView;
    TextView textField;
    // remote element arrays
    List<UserButton> userButtons = new ArrayList<>();
    List<UserPoti> userPotis = new ArrayList<>();
    List<SeekBar> potis = new ArrayList<>();

    List<TextView> textViewsForButtons = new ArrayList<>();
    List<TextView> textViewsForPotis = new ArrayList<>();

    // onTouchListener
    private static final int MAX_CLICK_DURATION = 150;
    long startClickTime;
    float downX = 0;
    float downY = 0;
    int currentRotation;

    @Override
    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.construct_remote_layout);

        // button content
        // name|code|posX|posY|size|rotation

        // potentiometer content
        // name|code|min|max|posX|posY|size|rotation

        //(maybe) getField content
        // name|code|posX|posY|size|rotation

        layout = findViewById(R.id.layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeOptionButtons();
            }
        });

        //option buttons
        optionButtonPanel = findViewById(R.id.optionButtonPanel);
        optionButtonPanel.setVisibility(View.GONE);

        optionPotiPanel = findViewById(R.id.optionPotiPanel);
        optionPotiPanel.setVisibility(View.GONE);

        optionTextFieldPanel = findViewById(R.id.optionTextFieldPanel);
        optionTextFieldPanel.setVisibility(View.GONE);

        selectedElementTextView = findViewById(R.id.selectedElementTextView);
        selectedElementTextView.setVisibility(View.GONE);


        // get the remote name from MainActivity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            remote = extras.getString("remote");
        }

        // Top action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.construct_remote_title_layout);
        }

        setRemoteContent();

    }

    public void addElementClick(View view){
        Dialog dialog = new Dialog(ConstructRemoteActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.add_element_dialog_layout);

        Button newButtonBtn = dialog.findViewById(R.id.newButtonBtn);
        Button newSwitchBtn = dialog.findViewById(R.id.newSwitchBtn);
        Button newPotentiometerBtn = dialog.findViewById(R.id.newPotentiometerBtn);

        Button newTextFieldBtn = dialog.findViewById(R.id.newTextFieldBtn);
        File myFile = new File(String.valueOf(this.getExternalFilesDir("Remotes")), remote + ".txt");
        try {
            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fIn));
            String line;

            int firstLine = 0;
            while ((line = bufferedReader.readLine()) != null){
                if (firstLine == 0){
                    firstLine++;
                    continue;
                }
                split = Arrays.asList(line.split("\\|"));
                if (split.get(0).equals("textfield")){
                    newTextFieldBtn.setEnabled(false);
                }
            }
        } catch (IOException e){
            System.out.println(e.getMessage());
        }


        newButtonBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddButtonDialog();
                dialog.dismiss();
            }
        });

        newSwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddSwitchDialog();
                dialog.dismiss();
            }
        });

        newPotentiometerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddPotiDialog();
                dialog.dismiss();
            }
        });

        newTextFieldBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createTextField();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void showChangeFirstLine(View v){
        File myFile = new File(String.valueOf(this.getExternalFilesDir("Remotes")), remote + ".txt");
        String currentValue = "";
        try {
            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fIn));
            currentValue = bufferedReader.readLine();
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
        Dialog dialog = new Dialog(ConstructRemoteActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.change_firstline_dialog_layout);

        TextView changeFirstLineTextView = dialog.findViewById(R.id.changeFirstlineTextView);
        EditText changeFirstLineEditText = dialog.findViewById(R.id.changeFirstlineEditText);
        changeFirstLineEditText.setText(currentValue);

        changeFirstLineEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().contains("\n")) {
                    String newText = s.toString().replace("\n", "");
                    changeFirstLineEditText.setText(newText);
                    changeFirstLineEditText.setSelection(newText.length());
                }
            }
        });

        Button okButtonChangeFirstline = dialog.findViewById(R.id.okButtonChangeFirstline);

        okButtonChangeFirstline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstLine = changeFirstLineEditText.getText().toString();
                if (firstLine.contains("|")){
                    createSnackBar("The symbol '|' is forbidden");
                    dialog.dismiss();
                    return;
                }
                if (firstLine.equals("")){
                    animation(changeFirstLineTextView);
                } else {
                    changeFirstLine(changeFirstLineEditText.getText().toString());
                    dialog.dismiss();
                }

            }
        });

        dialog.show();
    }

    public void changeFirstLine(String replacement) {
        String oldValue = "";
        StringBuffer inputBuffer = new StringBuffer();
        File myFile = new File(String.valueOf(this.getExternalFilesDir("Remotes")), remote + ".txt");
        try {
            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fIn));
            String line;
            int first = 0;
            while ((line = bufferedReader.readLine()) != null) {
                if (first == 0){
                    oldValue = line;
                    first++;
                }
                inputBuffer.append(line);
                inputBuffer.append('\n');
            }
        } catch (IOException e){
            System.out.println(e.getMessage());
        }

        String inputStr = inputBuffer.toString().replaceAll(oldValue, replacement);
        try {
            FileOutputStream fos = new FileOutputStream(myFile, false);
            fos.write(inputStr.getBytes(StandardCharsets.UTF_8));
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void backClick(View view){
        finish();
    }

    private void showAddButtonDialog(){
        Dialog dialog = new Dialog(ConstructRemoteActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.add_button_dialog_layout);

        TextView buttonNameTextView = dialog.findViewById(R.id.buttonNameTextView);
        EditText buttonNameEditText = dialog.findViewById(R.id.buttonNameEditText);

        buttonNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().contains("\n")) {
                    String newText = s.toString().replace("\n", "");
                    buttonNameEditText.setText(newText);
                    buttonNameEditText.setSelection(newText.length());
                }
            }
        });

        Button addButtonAddBtn = dialog.findViewById(R.id.addButtonAddBtn);

        addButtonAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String buttonName = buttonNameEditText.getText().toString();
                if (buttonNameEditText.getText().toString().contains("|")){
                    createSnackBar("The symbol '|' is forbidden");
                    dialog.dismiss();
                    return;
                }
                if (buttonName.equals("")){
                    animation(buttonNameTextView);
                } else {
                    createButton(buttonName);
                    dialog.dismiss();
                }

            }
        });

        dialog.show();
    }

    private void showAddSwitchDialog(){
        Dialog dialog = new Dialog(ConstructRemoteActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.add_switch_dialog_layout);

        TextView switchNameTextView = dialog.findViewById(R.id.switchNameTextView);
        EditText switchNameEditText = dialog.findViewById(R.id.switchNameEditText);

        switchNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().contains("\n")) {
                    String newText = s.toString().replace("\n", "");
                    switchNameEditText.setText(newText);
                    switchNameEditText.setSelection(newText.length());
                }
            }
        });

        Button addButtonAddSwitch = dialog.findViewById(R.id.addButtonAddSwitch);

        addButtonAddSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String switchName = switchNameEditText.getText().toString();
                if (switchName.contains("|")){
                    createSnackBar("The symbol '|' is forbidden");
                    dialog.dismiss();
                    return;
                }
                if (switchName.equals("")){
                    animation(switchNameTextView);
                } else {
                    createSwitch(switchName);
                    dialog.dismiss();
                }

            }
        });

        dialog.show();
    }

    private void showAddPotiDialog(){
        Dialog dialog = new Dialog(ConstructRemoteActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.add_poti_dialog_layout);

        TextView nameTextView = dialog.findViewById(R.id.potiNameTextView);
        EditText nameEditText = dialog.findViewById(R.id.potiNameEditText);

        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().contains("\n")) {
                    String newText = s.toString().replace("\n", "");
                    nameEditText.setText(newText);
                    nameEditText.setSelection(newText.length());
                }
            }
        });

        TextView minTextView = dialog.findViewById(R.id.minTextView);
        EditText minEditText = dialog.findViewById(R.id.minEditText);

        TextView maxTextView = dialog.findViewById(R.id.maxTextView);
        EditText maxEditText = dialog.findViewById(R.id.maxEditText);

        Button addBtn = dialog.findViewById(R.id.addBtnAddPoti);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean everythingFilled = true;
                if (nameEditText.getText().toString().contains("|")){
                    createSnackBar("The symbol '|' is forbidden");
                    dialog.dismiss();
                    return;
                }
                if (nameEditText.getText().toString().equals("")){
                    everythingFilled = false;
                    animation(nameTextView);
                }
                if (minEditText.getText().toString().equals("")){
                    everythingFilled = false;
                    animation(minTextView);
                }
                if (maxEditText.getText().toString().equals("")){
                    everythingFilled = false;
                    animation(maxTextView);
                }
                boolean maxBiggerThanMin = Integer.parseInt(maxEditText.getText().toString()) > Integer.parseInt(minEditText.getText().toString());

                String name = nameEditText.getText().toString();
                String min = minEditText.getText().toString();
                String max = maxEditText.getText().toString();

                if (!maxBiggerThanMin){
                    String minSwap = min;
                    min = max;
                    max = minSwap;
                }

                // check if everything is filled
                // swap min and max if max is less than min
                if (everythingFilled){
                    createPotentiometer(name, min, max);
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }


    // name|code|posX|posY|size|rotation
    private void createButton(String buttonName){
        File myFile = new File(String.valueOf(this.getExternalFilesDir("Remotes")), remote + ".txt");

        // check for duplicate name
        try {
            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fIn));
            String line;

            int firstLine = 0;
            while ((line = bufferedReader.readLine()) != null){
                if (firstLine == 0){
                    firstLine++;
                    continue;
                }
                split = Arrays.asList(line.split("\\|"));
                if (split.get(0).equals(buttonName)){
                    createSnackBar("Element " + buttonName + " already exists");
                    return;
                }
            }
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
        //give unique number to code
        fillCodeNumberArray(myFile);
        createCode("b");


        // write button in txt file
        try {
            byte[] name = buttonName.getBytes();
            FileOutputStream fos = new FileOutputStream(myFile, true);
            fos.write(name);
            fos.write("|".getBytes(StandardCharsets.UTF_8));
            fos.write(code.getBytes(StandardCharsets.UTF_8));
            fos.write("0.0|0.0|0|0".getBytes(StandardCharsets.UTF_8));
            fos.write("\n".getBytes(StandardCharsets.UTF_8));
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setRemoteContent();
    }

    private void createSwitch(String switchName){
        File myFile = new File(String.valueOf(this.getExternalFilesDir("Remotes")), remote + ".txt");
        // check for duplicate name
        try {
            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fIn));
            String line;

            int firstLine = 0;
            while ((line = bufferedReader.readLine()) != null){
                if (firstLine == 0){
                    firstLine++;
                    continue;
                }
                split = Arrays.asList(line.split("\\|"));
                if (split.get(0).equals(switchName)){
                    createSnackBar("Element " + switchName + " already exists");
                    return;
                }
            }
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
        //give unique number to code
        fillCodeNumberArray(myFile);
        createCode("s");

        // write switch in txt file
        try {
            byte[] name = switchName.getBytes();
            FileOutputStream fos = new FileOutputStream(myFile, true);
            fos.write(name);
            fos.write("|".getBytes(StandardCharsets.UTF_8));
            fos.write(code.getBytes(StandardCharsets.UTF_8));
            fos.write("0.0|0.0|0|0".getBytes(StandardCharsets.UTF_8));
            fos.write("\n".getBytes(StandardCharsets.UTF_8));
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setRemoteContent();
    }

    private void createPotentiometer(String potiName, String min, String max){
        File myFile = new File(String.valueOf(this.getExternalFilesDir("Remotes")), remote + ".txt");
        // check for duplicate name
        try {
            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fIn));
            String line;

            int firstLine = 0;
            while ((line = bufferedReader.readLine()) != null){
                if (firstLine == 0){
                    firstLine++;
                    continue;
                }
                split = Arrays.asList(line.split("\\|"));
                if (split.get(0).equals(potiName)){
                    createSnackBar("Element " + potiName + " already exists");
                    return;
                }
            }
        } catch (IOException e){
            System.out.println(e.getMessage());
        }

        fillCodeNumberArray(myFile);
        createCode("p");
        try {
            byte[] name = potiName.getBytes();
            FileOutputStream fos = new FileOutputStream(myFile, true);
            fos.write(name);
            fos.write("|".getBytes(StandardCharsets.UTF_8));
            fos.write(code.getBytes(StandardCharsets.UTF_8));
            fos.write("0.0|50.0|300|50|".getBytes(StandardCharsets.UTF_8));
            fos.write(min.getBytes(StandardCharsets.UTF_8));
            fos.write("|".getBytes(StandardCharsets.UTF_8));
            fos.write(max.getBytes(StandardCharsets.UTF_8));
            fos.write("\n".getBytes(StandardCharsets.UTF_8));
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setRemoteContent();
    }

    private void createTextField(){
        // textfield|t|x|y|w|h|r
        File myFile = new File(String.valueOf(this.getExternalFilesDir("Remotes")), remote + ".txt");
        fillCodeNumberArray(myFile);
        createCode("t");
        try {
            FileOutputStream fos = new FileOutputStream(myFile, true);
            fos.write("textfield|".getBytes(StandardCharsets.UTF_8));
            fos.write(code.getBytes(StandardCharsets.UTF_8));
            fos.write("0.0|0.0|200|60|0".getBytes(StandardCharsets.UTF_8));
            fos.write("\n".getBytes(StandardCharsets.UTF_8));
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setRemoteContent();
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

    private void fillCodeNumberArray(File myFile){
        codeNumbers.clear();
        try {
            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fIn));
            String line;
            String fullCode = "";
            int firstLine = 0;
            while((line = bufferedReader.readLine()) != null){
                if(firstLine == 0){
                    firstLine++;
                    continue;
                }
                split = Arrays.asList(line.split("\\|"));
                fullCode = split.get(1);
                fullCode = fullCode.replace("b", "");
                fullCode = fullCode.replace("s", "");
                fullCode = fullCode.replace("p", "");
                fullCode = fullCode.replace("t", "");
                fullCode = fullCode.replace("x", "");

                codeNumbers.add(Integer.parseInt(fullCode));
            }
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    private void createCode(String letter){
        boolean found = false;
        int uniqueCodeNumber = 0;
        if (codeNumbers.isEmpty()){
            code = letter + "0x|";
        } else {
            for (int i = 0; i < codeNumbers.size(); i++){
                if (codeNumbers.contains(uniqueCodeNumber)){
                    uniqueCodeNumber++;
                } else {
                    code = letter + uniqueCodeNumber + "x|";
                    found = true;
                    break;
                }

            }
            if(!found){
                uniqueCodeNumber = codeNumbers.size();
                code = letter + uniqueCodeNumber + "x|";
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setRemoteContent(){
        layout.removeAllViews();
        textViewsForButtons.clear();
        userButtons.clear();
        potis.clear();
        textViewsForPotis.clear();
        userPotis.clear();
        File myFile = new File(String.valueOf(this.getExternalFilesDir("Remotes")), remote + ".txt");
        try {
            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fIn));
            String line;

            int firstLine = 0;
            int runCount = 0;
            while ((line = bufferedReader.readLine()) != null) {
                if (firstLine == 0){
                    firstLine++;
                    continue;
                }
                split = Arrays.asList(line.split("\\|"));
                if (split.size() != 6){
                    continue;
                }

                if (split.get(1).charAt(0) == 'b'){
                    UserButton userButton = new UserButton(split.get(0), split.get(1), split.get(2), split.get(3), split.get(4),split.get(5));
                    userButtons.add(userButton);

                    String posXstr = userButtons.get(runCount).getPosX();
                    String posYstr = userButtons.get(runCount).getPosY();
                    String size = userButtons.get(runCount).getSize();
                    String rotation = userButtons.get(runCount).getRotation();

                    buttonTextView = new TextView(this);
                    buttonTextView.setX(Float.parseFloat(posXstr));
                    buttonTextView.setY(Float.parseFloat(posYstr));
                    buttonTextView.setGravity(Gravity.CENTER);
                    buttonTextView.setTextColor(Color.BLUE);
                    buttonTextView.setText(userButtons.get(runCount).getName());
                    textViewsForButtons.add(buttonTextView);

                    if (size.equals("0")){
                        buttonTextView.setBackgroundResource(R.drawable.buttonunpressed50x50);
                    } else if (size.equals("1")){
                        buttonTextView.setBackgroundResource(R.drawable.buttonunpressed80x80);
                    } else {
                        buttonTextView.setBackgroundResource(R.drawable.buttonunpressed120x120);
                    }
                    buttonTextView.setRotation(Integer.parseInt(rotation));
                    int finalI = runCount;
                    buttonTextView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            switch (motionEvent.getAction()){
                                case MotionEvent.ACTION_DOWN:
                                    startClickTime = Calendar.getInstance().getTimeInMillis();
                                    downX = motionEvent.getX();
                                    downY = motionEvent.getY();
//                                    currentRotation = (int) textViewsForButtons.get(finalI).getRotation();
//                                    if (textViewsForButtons.get(finalI).getRotation() != 0){
//                                        textViewsForButtons.get(finalI).setRotation(0);
//                                    }
                                case MotionEvent.ACTION_MOVE:

                                    float movedX, movedY;
                                    movedX = motionEvent.getX();
                                    movedY = motionEvent.getY();

                                    float distanceX = movedX-downX;
                                    float distanceY = movedY-downY;

                                    float angle = Integer.parseInt(userButtons.get(finalI).getRotation()) * (float)(Math.PI / 180);
                                    float sinAngle = (float) Math.sin(angle);
                                    float cosAngle = (float) Math.cos(angle);

                                    float transformedX = cosAngle * (movedX - downX) - sinAngle * (movedY - downY);
                                    float transformedY = sinAngle * (movedX - downX) + cosAngle * (movedY - downY);
                                    textViewsForButtons.get(finalI).setX(textViewsForButtons.get(finalI).getX() + transformedX);
                                    textViewsForButtons.get(finalI).setY(textViewsForButtons.get(finalI).getY() + transformedY);
                                    break;

                                case MotionEvent.ACTION_UP: {
                                    userButtons.get(finalI).setPosX(String.valueOf(textViewsForButtons.get(finalI).getX()));
                                    userButtons.get(finalI).setPosY(String.valueOf(textViewsForButtons.get(finalI).getY()));
                                    long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
//                                    textViewsForButtons.get(finalI).setRotation(currentRotation);
                                    if(clickDuration < MAX_CLICK_DURATION) {
                                        addOptionButtons(userButtons.get(finalI).getName());
                                    } else {
                                        save();
                                    }
                                    break;
                                }
                            }
                            return true;
                        }
                    });
                    layout.addView(buttonTextView);

                } else if (split.get(1).charAt(0) == 's'){
                    UserButton userSwitch = new UserButton(split.get(0), split.get(1), split.get(2), split.get(3), split.get(4),split.get(5));
                    userButtons.add(userSwitch);

                    String posXstr = userButtons.get(runCount).getPosX();
                    String posYstr = userButtons.get(runCount).getPosY();
                    String size = userButtons.get(runCount).getSize();
                    String rotation = userButtons.get(runCount).getRotation();

                    switchTextView = new TextView(this);
                    switchTextView.setX(Float.parseFloat(posXstr));
                    switchTextView.setY(Float.parseFloat(posYstr));
                    switchTextView.setGravity(Gravity.CENTER);
                    switchTextView.setTextColor(Color.BLUE);
                    switchTextView.setText(userButtons.get(runCount).getName());
                    textViewsForButtons.add(switchTextView);

                    if (size.equals("0")){
                        switchTextView.setBackgroundResource(R.drawable.switchunpressed100x50);
                    } else if (size.equals("1")){
                        switchTextView.setBackgroundResource(R.drawable.switchunpressed160x80);
                    } else {
                        switchTextView.setBackgroundResource(R.drawable.switchunpressed240x120);
                    }
                    switchTextView.setRotation(Integer.parseInt(rotation));
                    int finalI = runCount;
                    switchTextView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            switch (motionEvent.getAction()){
                                case MotionEvent.ACTION_DOWN:
                                    startClickTime = Calendar.getInstance().getTimeInMillis();
                                    downX = motionEvent.getX();
                                    downY = motionEvent.getY();
//                                    currentRotation = (int) textViewsForButtons.get(finalI).getRotation();
//                                    if (textViewsForButtons.get(finalI).getRotation() != 0){
//                                        textViewsForButtons.get(finalI).setRotation(0);
//                                    }
                                case MotionEvent.ACTION_MOVE:
                                    float movedX, movedY;
                                    movedX = motionEvent.getX();
                                    movedY = motionEvent.getY();

                                    float distanceX = movedX-downX;
                                    float distanceY = movedY-downY;
                                    float angle = textViewsForButtons.get(finalI).getRotation() * (float)(Math.PI / 180);
                                    float sinAngle = (float) Math.sin(angle);
                                    float cosAngle = (float) Math.cos(angle);

                                    float transformedX = cosAngle * (movedX - downX) - sinAngle * (movedY - downY);
                                    float transformedY = sinAngle * (movedX - downX) + cosAngle * (movedY - downY);
                                    textViewsForButtons.get(finalI).setX(textViewsForButtons.get(finalI).getX() + transformedX);
                                    textViewsForButtons.get(finalI).setY(textViewsForButtons.get(finalI).getY() + transformedY);
                                    break;

                                case MotionEvent.ACTION_UP: {
                                    userButtons.get(finalI).setPosX(String.valueOf(textViewsForButtons.get(finalI).getX()));
                                    userButtons.get(finalI).setPosY(String.valueOf(textViewsForButtons.get(finalI).getY()));
                                    long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                                    //textViewsForButtons.get(finalI).setRotation(currentRotation);
                                    if(clickDuration < MAX_CLICK_DURATION) {
                                        addOptionButtons(userButtons.get(finalI).getName());
                                    } else {
                                        save();
                                    }
                                    break;
                                }
                            }
                            return true;
                        }
                    });
                    layout.addView(switchTextView);
                }
                runCount++;
            }
        } catch (IOException e){
            System.out.println(e.getMessage());
        }

        try {
            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fIn));
            String line;

            int firstLine = 0;
            int runCount = 0;
            while ((line = bufferedReader.readLine()) != null) {
                if (firstLine == 0) {
                    firstLine++;
                    continue;
                }

                split = Arrays.asList(line.split("\\|"));
                if (split.size() != 8){
                    continue;
                }


                if (split.get(1).charAt(0) == 'p'){
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

                    int finalRunCount = runCount;
                    potis.get(runCount).setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            switch (motionEvent.getAction()){
                                case MotionEvent.ACTION_DOWN:
                                    startClickTime = Calendar.getInstance().getTimeInMillis();
                                    downX = motionEvent.getX();
                                    downY = motionEvent.getY();
                                case MotionEvent.ACTION_MOVE:
                                    float movedX, movedY;
                                    movedX = motionEvent.getX();
                                    movedY = motionEvent.getY();

                                    float distanceX = movedX-downX;
                                    float distanceY = movedY-downY;
                                    potis.get(finalRunCount).setX(potis.get(finalRunCount).getX() + distanceX);
                                    potis.get(finalRunCount).setY(potis.get(finalRunCount).getY() + distanceY);
                                    textViewsForPotis.get(finalRunCount).setX(potis.get(finalRunCount).getX() + distanceX);
                                    textViewsForPotis.get(finalRunCount).setY(potis.get(finalRunCount).getY() + distanceY - 60);
                                    break;

                                case MotionEvent.ACTION_UP: {
                                    userPotis.get(finalRunCount).setPosX(String.valueOf(potis.get(finalRunCount).getX()));
                                    userPotis.get(finalRunCount).setPosY(String.valueOf(potis.get(finalRunCount).getY()));
                                    textViewsForPotis.get(finalRunCount).setX(potis.get(finalRunCount).getX());
                                    textViewsForPotis.get(finalRunCount).setY(potis.get(finalRunCount).getY() - 60);

                                    long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                                    if(clickDuration < MAX_CLICK_DURATION) {
                                        addOptionButtonsPoti(userPotis.get(finalRunCount).getName());
                                    } else {
                                        save();
                                    }
                                    break;
                                }
                            }
                            return true;
                        }
                    });
                    layout.addView(textViewsForPotis.get(runCount));
                    layout.addView(potis.get(runCount));
                }
                runCount++;
            }
        } catch (IOException e){
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
                if (split.get(1).charAt(0) == 't'){
                    userTextField = new UserTextField(split.get(0),split.get(1),split.get(2),split.get(3),split.get(4),split.get(5),split.get(6));

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
                    textField.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            switch (motionEvent.getAction()){
                                case MotionEvent.ACTION_DOWN:
                                    startClickTime = Calendar.getInstance().getTimeInMillis();
                                    downX = motionEvent.getX();
                                    downY = motionEvent.getY();
//                                    currentRotation = (int) textField.getRotation();
//                                    if (textField.getRotation() != 0){
//                                        textField.setRotation(0);
//                                    }
                                case MotionEvent.ACTION_MOVE:
                                    float movedX, movedY;
                                    movedX = motionEvent.getX();
                                    movedY = motionEvent.getY();

                                    float distanceX = movedX-downX;
                                    float distanceY = movedY-downY;
                                    float angle = textField.getRotation() * (float)(Math.PI / 180);
                                    float sinAngle = (float) Math.sin(angle);
                                    float cosAngle = (float) Math.cos(angle);

                                    float transformedX = cosAngle * (movedX - downX) - sinAngle * (movedY - downY);
                                    float transformedY = sinAngle * (movedX - downX) + cosAngle * (movedY - downY);

                                    textField.setX(textField.getX() + transformedX);
                                    textField.setY(textField.getY() + transformedY);
                                    userTextField.setX(Objects.toString(textField.getX() + transformedX));
                                    userTextField.setY(Objects.toString(textField.getY() + transformedY));
                                    break;
                                case MotionEvent.ACTION_UP: {
                                    //textField.setRotation(currentRotation);
                                    userTextField.setX(Objects.toString(textField.getX()));
                                    userTextField.setY(Objects.toString(textField.getY()));
                                    long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                                    if(clickDuration < MAX_CLICK_DURATION) {
                                        addOptionButtonsTextField();
                                    } else {
                                        save();
                                    }
                                    break;
                                }
                            }
                            return true;
                        }
                    });


                    layout.addView(textField);
                    break;
                }
            }
        } catch (IOException e){
            System.out.println(e.getMessage());
        }

        layout.addView(selectedElementTextView);
        layout.addView(optionButtonPanel);
        layout.addView(optionPotiPanel);
        layout.addView(optionTextFieldPanel);
    }

    private void addOptionButtons(String buttonName){
        selectedElement = buttonName;
        selectedElementTextView.setText("Selected: " + selectedElement);
        selectedElementTextView.setVisibility(View.VISIBLE);
        optionPotiPanel.setVisibility(View.GONE);
        optionTextFieldPanel.setVisibility(View.GONE);
        optionButtonPanel.setVisibility(View.VISIBLE);

    }

    private void addOptionButtonsPoti(String potiName){
        selectedElement = potiName;
        selectedElementTextView.setText("Selected: " + selectedElement);
        selectedElementTextView.setVisibility(View.VISIBLE);
        optionTextFieldPanel.setVisibility(View.GONE);
        optionButtonPanel.setVisibility(View.GONE);
        optionPotiPanel.setVisibility(View.VISIBLE);
    }

    private void addOptionButtonsTextField(){
        selectedElement = "Text field";
        selectedElementTextView.setText("Selected: " + selectedElement);
        selectedElementTextView.setVisibility(View.VISIBLE);
        optionButtonPanel.setVisibility(View.GONE);
        optionPotiPanel.setVisibility(View.GONE);
        optionTextFieldPanel.setVisibility(View.VISIBLE);
    }

    private void removeOptionButtons(){
        selectedElement = "";
        selectedElementTextView.setText("");
        selectedElementTextView.setVisibility(View.GONE);
        optionButtonPanel.setVisibility(View.GONE);
        optionPotiPanel.setVisibility(View.GONE);
        optionTextFieldPanel.setVisibility(View.GONE);
    }

    private void save(){
        String firstLine = "";
        File myFile = new File(String.valueOf(this.getExternalFilesDir("Remotes")), remote + ".txt");
        String textFieldData = "";
        try {
            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fIn));
            String line;

            int firstLineint = 0;
            while ((line = bufferedReader.readLine()) != null){
                if (firstLineint == 0){
                    firstLineint++;
                    continue;
                }
                split = Arrays.asList(line.split("\\|"));
                if (split.get(0).equals("textfield")){
                    if (!userTextField.getName().equals("")){
                        String name = userTextField.getName();
                        String code = userTextField.getCode();
                        String x = userTextField.getX();
                        String y = userTextField.getY();
                        String width = userTextField.getWidth();
                        String height = userTextField.getHeight();
                        String rotation = userTextField.getRotation();
                        textFieldData = name + "|" + code + "|" + x + "|" + y + "|" + width + "|" + height + "|" + rotation;
                    }
                }
            }
        } catch (IOException e){
            System.out.println(e.getMessage());
        }

        try {
            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fIn));
            firstLine = bufferedReader.readLine();
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
        try {
            FileOutputStream fos = new FileOutputStream(myFile, false);
            fos.write(firstLine.getBytes(StandardCharsets.UTF_8));
            fos.write("\n".getBytes(StandardCharsets.UTF_8));
        }  catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < userButtons.size(); i++){
            String name = userButtons.get(i).getName();
            String buttonCode = userButtons.get(i).getCode();
            String posX = userButtons.get(i).getPosX();
            String posY = userButtons.get(i).getPosY();
            String size = userButtons.get(i).getSize();
            String rotation = userButtons.get(i).getRotation();

            try {
                FileOutputStream fos = new FileOutputStream(myFile, true);
                fos.write(name.getBytes(StandardCharsets.UTF_8));
                fos.write("|".getBytes(StandardCharsets.UTF_8));
                fos.write(buttonCode.getBytes(StandardCharsets.UTF_8));
                fos.write("|".getBytes(StandardCharsets.UTF_8));
                fos.write(posX.getBytes(StandardCharsets.UTF_8));
                fos.write("|".getBytes(StandardCharsets.UTF_8));
                fos.write(posY.getBytes(StandardCharsets.UTF_8));
                fos.write("|".getBytes(StandardCharsets.UTF_8));
                fos.write(size.getBytes(StandardCharsets.UTF_8));
                fos.write("|".getBytes(StandardCharsets.UTF_8));
                fos.write(rotation.getBytes(StandardCharsets.UTF_8));
                fos.write("\n".getBytes(StandardCharsets.UTF_8));
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < userPotis.size(); i++){
            String name = userPotis.get(i).getName();
            String potiCode = userPotis.get(i).getCode();
            String posX = userPotis.get(i).getPosX();
            String posY = userPotis.get(i).getPosY();
            String width = userPotis.get(i).getWidth();
            String height = userPotis.get(i).getHeight();
            String min = userPotis.get(i).getMin();
            String max = userPotis.get(i).getMax();

            try {
                FileOutputStream fos = new FileOutputStream(myFile, true);
                fos.write(name.getBytes(StandardCharsets.UTF_8));
                fos.write("|".getBytes(StandardCharsets.UTF_8));
                fos.write(potiCode.getBytes(StandardCharsets.UTF_8));
                fos.write("|".getBytes(StandardCharsets.UTF_8));
                fos.write(posX.getBytes(StandardCharsets.UTF_8));
                fos.write("|".getBytes(StandardCharsets.UTF_8));
                fos.write(posY.getBytes(StandardCharsets.UTF_8));
                fos.write("|".getBytes(StandardCharsets.UTF_8));
                fos.write(width.getBytes(StandardCharsets.UTF_8));
                fos.write("|".getBytes(StandardCharsets.UTF_8));
                fos.write(height.getBytes(StandardCharsets.UTF_8));
                fos.write("|".getBytes(StandardCharsets.UTF_8));
                fos.write(min.getBytes(StandardCharsets.UTF_8));
                fos.write("|".getBytes(StandardCharsets.UTF_8));
                fos.write(max.getBytes(StandardCharsets.UTF_8));
                fos.write("\n".getBytes(StandardCharsets.UTF_8));
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            if(!textFieldData.equals("")){
                FileOutputStream fos = new FileOutputStream(myFile, true);
                fos.write(textFieldData.getBytes(StandardCharsets.UTF_8));
                fos.write("\n".getBytes(StandardCharsets.UTF_8));
                fos.flush();
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteClick(View v){
        for(int i = 0; i < userButtons.size(); i++){
            if (selectedElement.equals(userButtons.get(i).getName())){
                userButtons.remove(i);
            }
        }
        save();
        removeOptionButtons();
        setRemoteContent();

    }

    public void rotateClick(View v){
        for(int i = 0; i < userButtons.size(); i++){
            if (selectedElement.equals(userButtons.get(i).getName())){
                int rotationInt = Integer.parseInt(userButtons.get(i).getRotation());
                rotationInt += 90;
                if (rotationInt == 360){
                    rotationInt = 0;
                }
                userButtons.get(i).setRotation(Objects.toString(rotationInt));
            }
        }
        save();
        setRemoteContent();
    }

    public void minusSizeClick(View v){
        for(int i = 0; i < userButtons.size(); i++){
            if (selectedElement.equals(userButtons.get(i).getName())){
                int size = Integer.parseInt(userButtons.get(i).getSize());
                if (size != 0){
                    size--;
                }
                userButtons.get(i).setSize(Objects.toString(size));
            }
        }
        save();
        setRemoteContent();
    }

    public void plusSizeClick(View v){
        for(int i = 0; i < userButtons.size(); i++){
            if (selectedElement.equals(userButtons.get(i).getName())){
                int size = Integer.parseInt(userButtons.get(i).getSize());
                if (size != 2){
                    size++;
                }
                userButtons.get(i).setSize(Objects.toString(size));
            }
        }
        save();
        setRemoteContent();
    }

//    public void rotatePoti(View v){
//        for(int i = 0; i < userPotis.size(); i++){
//            if (selectedElement.equals(userPotis.get(i).getName())){
//
//                int rotationInt = Integer.parseInt(userPotis.get(i).getRotation());
//                rotationInt += 90;
//                if (rotationInt == 360){
//                    rotationInt = 0;
//                }
//                userPotis.get(i).setRotation(Objects.toString(rotationInt));
//            }
//        }
//        save();
//        setRemoteContent();
//    }

    public void plusWidthClick(View v){
        for(int i = 0; i < userPotis.size(); i++){
            if (selectedElement.equals(userPotis.get(i).getName())){
                String widthStr = userPotis.get(i).getWidth();
                int width = Integer.parseInt(widthStr) + 20;
                userPotis.get(i).setWidth(Objects.toString(width));
            }
        }
        save();
        setRemoteContent();
    }

    public void plusHeightClick(View v){
        for(int i = 0; i < userPotis.size(); i++){
            if (selectedElement.equals(userPotis.get(i).getName())){
                String heightStr = userPotis.get(i).getHeight();
                int height = Integer.parseInt(heightStr) + 20;
                userPotis.get(i).setHeight(Objects.toString(height));
            }
        }
        save();
        setRemoteContent();
    }

    public void setDefaultPotiSizeClick(View v){
        for(int i = 0; i < userPotis.size(); i++){
            if (selectedElement.equals(userPotis.get(i).getName())){
                userPotis.get(i).setWidth("300");
                userPotis.get(i).setHeight("50");
            }
        }
        save();
        setRemoteContent();
    }

    public void deletePotiClick(View v){
        for(int i = 0; i < userPotis.size(); i++){
            if (selectedElement.equals(userPotis.get(i).getName())){
                userPotis.remove(i);
            }
        }
        save();
        removeOptionButtons();
        setRemoteContent();
    }

    public void deleteTextFieldClick(View v){
        userTextField.setName("");
        save();
        removeOptionButtons();
        setRemoteContent();
    }

    public void plusHeightTextFieldClick(View v){
        String heightStr = userTextField.getHeight();
        int height = Integer.parseInt(heightStr) + 30;
        textField.setHeight(height);
        userTextField.setHeight(Objects.toString(height));
        save();
        setRemoteContent();
    }

    public void plusWidthTextFieldClick(View v){
        String widthStr = userTextField.getWidth();
        int width = Integer.parseInt(widthStr) + 30;
        textField.setHeight(width);
        userTextField.setWidth(Objects.toString(width));
        save();
        setRemoteContent();
    }

    public void rotateTextFieldClick(View v){
        String rotationStr = userTextField.getRotation();
        int rotation = Integer.parseInt(rotationStr);
        rotation += 90;
        if (rotation == 360){
            rotation = 0;
        }
        textField.setRotation(rotation);
        userTextField.setRotation(Objects.toString(rotation));
        save();
        setRemoteContent();
    }
}