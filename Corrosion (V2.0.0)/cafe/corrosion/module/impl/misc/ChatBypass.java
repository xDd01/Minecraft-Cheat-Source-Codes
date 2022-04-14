/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.module.impl.misc;

import cafe.corrosion.event.impl.EventChat;
import cafe.corrosion.module.Module;
import cafe.corrosion.module.attribute.ModuleAttributes;
import cafe.corrosion.util.math.RandomUtil;

@ModuleAttributes(name="ChatBypass", description="Attempts to bypass chat filters", category=Module.Category.MISC)
public class ChatBypass
extends Module {
    private static final char SPACE = ' ';
    private final Character[] BYPASS_CHARACTERS = new Character[]{Character.valueOf('\u26df'), Character.valueOf('\u26e0'), Character.valueOf('\u26e1'), Character.valueOf('\u26e2'), Character.valueOf('\u26e3'), Character.valueOf('\u26e4'), Character.valueOf('\u2753'), Character.valueOf('\u2e3b'), Character.valueOf('\u0fd8')};

    public ChatBypass() {
        this.registerEventHandler(EventChat.class, this::handleChat);
    }

    public void handleChat(EventChat chatEvent) {
        if (chatEvent.getRawMessage().startsWith("-") || chatEvent.getRawMessage().startsWith("/")) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        char[] array = chatEvent.getRawMessage().toCharArray();
        for (int i2 = 0; i2 < array.length; ++i2) {
            char character = array[i2];
            stringBuilder.append(character);
            if (i2 % 2 != 0 || character == ' ') continue;
            stringBuilder.append(RandomUtil.choice(this.BYPASS_CHARACTERS));
        }
        chatEvent.setRawMessage(stringBuilder.toString());
    }
}

