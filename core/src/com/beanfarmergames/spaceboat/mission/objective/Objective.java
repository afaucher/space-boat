package com.beanfarmergames.spaceboat.mission.objective;

import com.badlogic.gdx.utils.Disposable;
import com.beanfarmergames.common.callbacks.RenderCallback;
import com.beanfarmergames.common.callbacks.UpdateCallback;
import com.beanfarmergames.spaceboat.RenderContext;

public interface Objective extends UpdateCallback {
    public String getObjectiveName();
    public boolean getObjectiveCompletion();
}
