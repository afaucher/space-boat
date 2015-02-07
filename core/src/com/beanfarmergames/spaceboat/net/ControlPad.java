package com.beanfarmergames.spaceboat.net;

import com.badlogic.gdx.math.Vector2;

public class ControlPad {
    private Vector2 axis = new Vector2();
    private boolean buttonStart;
    private boolean buttonA;
    private boolean buttonB;
    private boolean buttonC;
    
    public Vector2 getAxis() {
        return axis;
    }
    public void setAxis(Vector2 axis) {
        this.axis = axis.cpy();
    }
    public boolean isButtonStart() {
        return buttonStart;
    }
    public void setButtonStart(boolean buttonStart) {
        this.buttonStart = buttonStart;
    }
    public boolean isButtonA() {
        return buttonA;
    }
    public void setButtonA(boolean buttonA) {
        this.buttonA = buttonA;
    }
    public boolean isButtonB() {
        return buttonB;
    }
    public void setButtonB(boolean buttonB) {
        this.buttonB = buttonB;
    }
    public boolean isButtonC() {
        return buttonC;
    }
    public void setButtonC(boolean buttonC) {
        this.buttonC = buttonC;
    }
    public ControlPad() {
        super();
    }
    public ControlPad(Vector2 axis, boolean buttonStart, boolean buttonA, boolean buttonB, boolean buttonC) {
        super();
        this.axis = axis.cpy();
        this.buttonStart = buttonStart;
        this.buttonA = buttonA;
        this.buttonB = buttonB;
        this.buttonC = buttonC;
    }
    
    
    
}
