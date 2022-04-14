package org.lwjgl.util.jinput;

import net.java.games.util.plugins.*;
import net.java.games.input.*;

public class LWJGLEnvironmentPlugin extends ControllerEnvironment implements Plugin
{
    private final Controller[] controllers;
    
    public LWJGLEnvironmentPlugin() {
        this.controllers = new Controller[] { new LWJGLKeyboard(), new LWJGLMouse() };
    }
    
    @Override
    public Controller[] getControllers() {
        return this.controllers;
    }
    
    @Override
    public boolean isSupported() {
        return true;
    }
}
