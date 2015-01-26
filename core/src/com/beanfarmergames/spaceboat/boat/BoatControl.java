package com.beanfarmergames.spaceboat.boat;

import com.beanfarmergames.common.controls.AxisControl;

public class BoatControl {
    private AxisControl left = new AxisControl(0,1,0);
    private AxisControl right = new AxisControl(0,1,0);
    
    
    public AxisControl getLeft() {
        return left;
    }
    public AxisControl getRight() {
        return right;
    }
    
    
}
