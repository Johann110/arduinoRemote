package com.app.arduinoremote;

public class UserPoti {
    String name;
    String code;
    String posX;
    String posY;
    String width;
    String height;
    String min;
    String max;

    public UserPoti(String name, String code, String posX, String posY, String width, String height, String min, String max){
        this.name = name;
        this.code = code;
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.min = min;
        this.max = max;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getPosX() {
        return posX;
    }

    public String getPosY() {
        return posY;
    }

    public String getWidth() {
        return width;
    }

    public String getHeight() {
        return height;
    }

    public String getMin() {
        return min;
    }

    public String getMax() {
        return max;
    }

    public void setPosX(String posX){
        this.posX = posX;
    }

    public void setPosY(String posY) {
        this.posY = posY;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public void setHeight(String height) {
        this.height = height;
    }
}
