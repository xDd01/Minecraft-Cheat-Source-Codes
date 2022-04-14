package me.spec.eris.client.modules.movement;

import org.lwjgl.input.Keyboard;

import me.spec.eris.api.event.Event;
import me.spec.eris.client.events.player.EventUpdate;
import me.spec.eris.api.module.ModuleCategory;
import me.spec.eris.api.module.Module;
import net.minecraft.client.gui.GuiChat;

public class GuiMove extends Module {

    public GuiMove(String racism) {
        super("GuiMove", ModuleCategory.MOVEMENT, racism);;
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventUpdate) {        
        	if (mc.currentScreen instanceof GuiChat)  return;
        	mc.gameSettings.keyBindForward.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode());
        	mc.gameSettings.keyBindRight.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode());
        	mc.gameSettings.keyBindLeft.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode());
        	mc.gameSettings.keyBindJump.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode());
        	mc.gameSettings.keyBindBack.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode());
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}