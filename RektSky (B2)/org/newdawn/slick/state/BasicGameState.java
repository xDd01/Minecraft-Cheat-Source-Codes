package org.newdawn.slick.state;

import org.newdawn.slick.*;

public abstract class BasicGameState implements GameState
{
    private boolean pauseUpdate;
    private boolean pauseRender;
    
    public BasicGameState() {
        this.pauseUpdate = false;
        this.pauseRender = false;
    }
    
    @Override
    public void inputStarted() {
    }
    
    @Override
    public boolean isAcceptingInput() {
        return true;
    }
    
    @Override
    public void setInput(final Input input) {
    }
    
    @Override
    public void inputEnded() {
    }
    
    @Override
    public abstract int getID();
    
    @Override
    public void controllerButtonPressed(final int controller, final int button) {
    }
    
    @Override
    public void controllerButtonReleased(final int controller, final int button) {
    }
    
    @Override
    public void controllerDownPressed(final int controller) {
    }
    
    @Override
    public void controllerDownReleased(final int controller) {
    }
    
    @Override
    public void controllerLeftPressed(final int controller) {
    }
    
    @Override
    public void controllerLeftReleased(final int controller) {
    }
    
    @Override
    public void controllerRightPressed(final int controller) {
    }
    
    @Override
    public void controllerRightReleased(final int controller) {
    }
    
    @Override
    public void controllerUpPressed(final int controller) {
    }
    
    @Override
    public void controllerUpReleased(final int controller) {
    }
    
    @Override
    public void keyPressed(final int key, final char c) {
    }
    
    @Override
    public void keyReleased(final int key, final char c) {
    }
    
    @Override
    public void mouseMoved(final int oldx, final int oldy, final int newx, final int newy) {
    }
    
    @Override
    public void mouseDragged(final int oldx, final int oldy, final int newx, final int newy) {
    }
    
    @Override
    public void mouseClicked(final int button, final int x, final int y, final int clickCount) {
    }
    
    @Override
    public void mousePressed(final int button, final int x, final int y) {
    }
    
    @Override
    public void mouseReleased(final int button, final int x, final int y) {
    }
    
    @Override
    public void enter(final GameContainer container, final StateBasedGame game) throws SlickException {
    }
    
    @Override
    public void leave(final GameContainer container, final StateBasedGame game) throws SlickException {
    }
    
    @Override
    public void mouseWheelMoved(final int newValue) {
    }
    
    @Override
    public void pauseUpdate() {
        this.pauseUpdate = true;
    }
    
    @Override
    public void pauseRender() {
        this.pauseRender = true;
    }
    
    @Override
    public void unpauseUpdate() {
        this.pauseUpdate = false;
    }
    
    @Override
    public void unpauseRender() {
        this.pauseRender = false;
    }
    
    @Override
    public boolean isUpdatePaused() {
        return this.pauseUpdate;
    }
    
    @Override
    public boolean isRenderPaused() {
        return this.pauseRender;
    }
    
    @Override
    public void setUpdatePaused(final boolean pause) {
        this.pauseUpdate = pause;
    }
    
    @Override
    public void setRenderPaused(final boolean pause) {
        this.pauseRender = pause;
    }
}
