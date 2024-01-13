package com.app.arduinoremote;

import android.widget.ImageView;

public class UserButton {
    String name;
    String code;
    String posX;
    String posY;
    String size;
    String rotation;


    public UserButton(String name, String code, String posX, String posY, String size, String rotation){
        this.name = name;
        this.code = code;
        this.posX = posX;
        this.posY = posY;
        this.size = size;
        this.rotation = rotation;
    }

    public String getName() {
        return this.name;
    }

    public String getCode() {
        return this.code;
    }

    public String getPosX() {
        return this.posX;
    }

    public String getPosY() {
        return this.posY;
    }

    public String getSize() {
        return this.size;
    }

    public String getRotation() {
        return this.rotation;
    }

    public void setPosX(String posX){
        this.posX = posX;
    }

    public void setPosY(String posY){
        this.posY = posY;
    }

    public void setRotation(String rotation){
        this.rotation = rotation;
    }

    public void setSize(String size){
        this.size = size;
    }

}
