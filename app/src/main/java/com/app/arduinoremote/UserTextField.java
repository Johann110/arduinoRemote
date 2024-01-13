package com.app.arduinoremote;

public class UserTextField {
    String name;
    String code;
    String x;
    String y;
    String width;
    String height;
    String rotation;

    public UserTextField(String name, String code, String x, String y, String width, String height, String rotation){
        this.name = name;
        this.code = code;
        this.x= x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.rotation = rotation;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getX() {
        return x;
    }

    public String getY() {
        return y;
    }

    public String getWidth() {
        return width;
    }

    public String getHeight() {
        return height;
    }

    public String getRotation() {
        return rotation;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setX(String x) {
        this.x = x;
    }

    public void setY(String y) {
        this.y = y;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void setRotation(String rotation) {
        this.rotation = rotation;
    }
}
