/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.module.impl.player;

import cafe.corrosion.event.impl.EventUpdate;
import cafe.corrosion.module.Module;
import cafe.corrosion.module.attribute.ModuleAttributes;
import cafe.corrosion.property.type.EnumProperty;
import cafe.corrosion.property.type.NumberProperty;
import cafe.corrosion.util.nameable.INameable;
import cafe.corrosion.util.player.MovementUtil;

@ModuleAttributes(name="NoFall", description="Prevents you from taking fall damage", category=Module.Category.PLAYER)
public class NoFall
extends Module {
    private final EnumProperty<Mode> mode = new EnumProperty((Module)this, "Mode", (INameable[])Mode.values());
    private final NumberProperty fallDistance = new NumberProperty(this, "Fall Distance", 3, 2, 5, 0.1);
    private boolean collide;

    public NoFall() {
        this.registerEventHandler(EventUpdate.class, event -> {
            if (NoFall.mc.thePlayer.fallDistance < ((Number)this.fallDistance.getValue()).floatValue()) {
                return;
            }
            switch ((Mode)this.mode.getValue()) {
                case SPOOF: {
                    if (!(NoFall.mc.thePlayer.fallDistance > 0.0f) || !((double)(NoFall.mc.thePlayer.fallDistance % 3.0f) < 0.5)) break;
                    event.setOnGround(true);
                    break;
                }
                case COLLIDE: {
                    if (NoFall.mc.thePlayer.fallDistance > 0.0f && (double)(NoFall.mc.thePlayer.fallDistance % 3.0f) < 0.5) {
                        if (this.collide) break;
                        NoFall.mc.thePlayer.motionY = 0.0;
                        event.setOnGround(true);
                        this.collide = true;
                        break;
                    }
                    this.collide = false;
                    break;
                }
                case ROUND: {
                    double y2 = MovementUtil.roundPos(event.getY());
                    if (!(NoFall.mc.thePlayer.fallDistance > 0.0f)) break;
                    event.setOnGround(true);
                    event.setY(y2);
                    break;
                }
                case TICK: {
                    if (!(NoFall.mc.thePlayer.fallDistance > 2.0f) || NoFall.mc.thePlayer.ticksExisted % 2 != 0) break;
                    event.setOnGround(true);
                }
            }
        });
    }

    @Override
    public void onEnable() {
        this.collide = true;
    }

    static enum Mode implements INameable
    {
        SPOOF("Spoof"),
        COLLIDE("Collide"),
        ROUND("Round"),
        TICK("Tick");

        private final String name;

        private Mode(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return this.name;
        }
    }
}

