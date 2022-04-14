/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.menu.social;

import cafe.corrosion.Corrosion;
import cafe.corrosion.font.TTFFontRenderer;
import cafe.corrosion.social.CorrosionSocket;
import cafe.corrosion.social.friend.Friend;
import cafe.corrosion.util.font.type.FontType;
import java.awt.Color;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class FriendGUI
extends GuiScreen {
    private static final String CHECK_MARK = "Accept";
    private static final String X_MARK = "Deny";
    private static final Color GREEN = new Color(50, 168, 82);
    private static final Color RED = new Color(130, 0, 0);

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        CorrosionSocket corrosionSocket = Corrosion.INSTANCE.getCorrosionSocket();
        List<Friend> inboundFriendRequests = corrosionSocket.getFriendManager().getInboundFriendRequests();
        TTFFontRenderer font = Corrosion.INSTANCE.getFontManager().getFontRenderer(FontType.ROBOTO, 18.0f);
        font.drawStringWithShadow("Inbound Friend Requests - " + inboundFriendRequests.size(), 1.0f, 10.0f, GREEN.getRGB());
        this.onReceiveFriendRequest();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void onReceiveFriendRequest() {
        this.buttonList.clear();
        AtomicInteger id2 = new AtomicInteger();
        CorrosionSocket corrosionSocket = Corrosion.INSTANCE.getCorrosionSocket();
        corrosionSocket.getFriendManager().getInboundFriendRequests().forEach(request -> {
            int friendId = id2.incrementAndGet();
            int posY = 25 * friendId + 20;
            String text = "Friend Request - " + request.getUsername() + " (" + request.getId() + ")";
            int maxX = 325;
            this.buttonList.add(new GuiButton(friendId, 0, posY, 325, 20, text));
            this.buttonList.add(new GuiButton(friendId * 10000, maxX + 5, posY, 60, 20, CHECK_MARK, GREEN));
            this.buttonList.add(new GuiButton(friendId * 20000, maxX + 70, posY, 35, 20, X_MARK, RED));
        });
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }
}

