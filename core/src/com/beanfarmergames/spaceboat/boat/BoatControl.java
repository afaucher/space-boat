package com.beanfarmergames.spaceboat.boat;

import com.beanfarmergames.common.controls.AxisControl;
import com.beanfarmergames.common.controls.ToggleControl;

public class BoatControl {
    private AxisControl left = new AxisControl(0, 1, 0);
    private AxisControl right = new AxisControl(0, 1, 0);
    private ToggleControl ray = new ToggleControl(); 

    public AxisControl getLeft() {
        return left;
    }

    public AxisControl getRight() {
        return right;
    }

}
