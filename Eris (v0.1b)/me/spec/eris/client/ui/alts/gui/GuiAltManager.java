package me.spec.eris.client.ui.alts.gui;

import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.swing.JFileChooser;

import me.spec.eris.client.ui.alts.gui.buttons.ExpandButton;
import me.spec.eris.client.ui.alts.gui.buttons.GuiTextField;
import me.spec.eris.utils.visual.RenderUtilities;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;

import libraries.thealtening.service.AlteningServiceType;
import me.spec.eris.Eris;
import me.spec.eris.client.ui.alts.Alt;
import me.spec.eris.utils.file.FileUtils;
import me.spec.eris.utils.misc.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Session;

public class GuiAltManager extends GuiScreen {

    private String status = "";
    private GuiTextField email;
    private GuiTextField pass;
    private long lastStatusMs;
    private boolean selected;
    private boolean inBoundsCheck;

    @Override
    public void initGui() {
        refresh();
        Keyboard.enableRepeatEvents(true);
    }

    private void refresh() {
        buttonList.clear();
        offset = 0;

        for (int i = 0; i < Eris.getInstance().altManager.getManagerArraylist().size(); i++) {
            buttonList.add(new AltButton(5, ((i + 1) * 21) + 20, i, this));
        }

        email = new GuiTextField(0, mc.fontRendererObj, width - 155, 10, 150, 20, false);
        pass = new GuiTextField(0, mc.fontRendererObj, width - 155, 30, 150, 20, true);
        buttonList.add(new ExpandButton(1, width - 155, height - 35, width - 85, height - 5, "Remove", () -> selected));
        buttonList.add(new ExpandButton(3, width - 155, height - 75, width - 85, height - 40, "Clear List", () -> selected));
        buttonList.add(new ExpandButton(4, width - 104, 55, width - 52, 75, "Add", ()-> !email.getText().isEmpty()));
        buttonList.add(new ExpandButton(5, width - 155, 55, width - 106, 75, "Login", ()-> !email.getText().isEmpty()));
        buttonList.add(new ExpandButton(6, width - 50, 55, width - 3, 75, "Clipboard", null));

        buttonList.add(new ExpandButton(2, width - 75, height - 75, width - 5, height - 45, "Import List", () -> selected));
        buttonList.add(new ExpandButton(7, width - 75, height - 35, width - 5, height - 5, "Altening Menu", null));
    }

    private int offset;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (Mouse.hasWheel()) {
            final int wheel = Mouse.getDWheel();
            if (wheel < 0) {
                offset += 21;
                if (offset < 0) {
                    offset = 0;
                }
            } else if (wheel > 0) {
                offset -= 21;
                if (offset < 0) {
                    offset = 0;
                }
            }
        }
        //Colorful red background
        drawGradientRect(0, 0, width, height, 0, -1610612736);
        drawGradientRect(0, 0, width, height, -2130750123, 16764108);
        //Sidebox
        RenderUtilities.drawRoundedRect(width - 155, 76, width + 5, height - 100, new Color(255,75,75).getRGB(), new Color(0,0,0,125).getRGB());
        //Account
        Eris.getInstance().getFontRenderer().drawStringWithShadow("Current Account:", width - 145, 85, new Color(255,255,255).getRGB());
        Eris.getInstance().getFontRenderer().drawStringWithShadow(Minecraft.getMinecraft().getSession().getUsername(), width - 145, 95, new Color(30,255,30).getRGB());
        //Status
        Eris.getInstance().getFontRenderer().drawStringWithShadow("Status:", width - 145, 105, new Color(255,255,255).getRGB());
        if (System.currentTimeMillis() - lastStatusMs > 10000) {
            status = EnumChatFormatting.GRAY + "Waiting...";
        }
        Eris.getInstance().getFontRenderer().drawStringWithShadow(status.equals("") ? "Waiting" : status, width - 145, 115, new Color(255,255,255).getRGB());
        //Alt list
        RenderUtilities.drawRoundedRect(5, 5, width - 160, height - 5, new Color(255,75,75).getRGB(), new Color(5,5,5,100).getRGB());
      //  Gui.drawRect(5, 5, width - 160, height - 5, new Color(255, 255, 255, 35).getRGB());
        Eris.getInstance().getFontRenderer().drawCenteredString("Account List", Math.abs(5 - (width - 160)) / 2, 5, new Color(255,255,255).getRGB());

        for (int i = 0; i < buttonList.size(); ++i) {
            if (buttonList.get(i) instanceof AltButton) {
                AltButton ab = (AltButton) buttonList.get(i);
                ab.yPosition = ab.getOrigY() - offset;
            }
            buttonList.get(i).drawButton(mc, mouseX, mouseY);
        }
        email.drawTextBox();
        pass.drawTextBox();
        if (email.getText().isEmpty()) {
            mc.fontRendererObj.drawString("Email", width - 150, 15, new Color(100, 100, 100).getRGB());
        }

        if (pass.getText().isEmpty()) {
            mc.fontRendererObj.drawString("Password", width - 150, 36, new Color(100, 100, 100).getRGB());
        }
    }

    public void login(final Alt alt) {
        if (alt == null) {
            return;
        }
        Thread login = new Thread(new Runnable() {

            @Override
            public void run() {
                if (alt.isCracked()) {
                    status = EnumChatFormatting.YELLOW + "Logged into cracked account!";
                    Minecraft.getMinecraft().session = new Session(alt.getUser(), "", "", "legacy");
                } else {
                    Eris.INSTANCE.serviceSwitcher.switchToService(AlteningServiceType.MOJANG);
                    status = EnumChatFormatting.YELLOW + "Logging in...";
                    YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
                    YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) service.createUserAuthentication(Agent.MINECRAFT);
                    auth.setUsername(alt.getUser());
                    auth.setPassword(alt.getPass());
                    try {
                        auth.logIn();
                        Minecraft.getMinecraft().session = new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), "mojang");
                        status = EnumChatFormatting.GREEN + "Logged in as " + auth.getSelectedProfile().getName() + ".";
                    } catch (AuthenticationException localAuthenticationException) {
                        localAuthenticationException.printStackTrace();
                        status = EnumChatFormatting.RED + "Login failed.";
                    }
                }
                lastStatusMs = System.currentTimeMillis();
            }
        });
        login.start();
    }

    @Override
    protected void keyTyped(final char character, final int key) throws IOException {
        try {
            super.keyTyped(character, key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (character == '\t' && (email.isFocused() || pass.isFocused())) {
            selected = false;
            email.setFocused(!email.isFocused());
            pass.setFocused(!pass.isFocused());
        }
        if (character == '\r') {
            selected = false;
            actionPerformed(buttonList.get(0));
        }
        email.textboxKeyTyped(character, key);
        pass.textboxKeyTyped(character, key);
    }

    public boolean isAltInArea(final int y) {
        return y <= height && y > 20;
    }

    public void setSelected(int val) {
        for (GuiButton g : buttonList) {
            if (g instanceof AltButton) {
                AltButton ab = (AltButton) g;
                ab.setSelected(selected = false);
            }
        }

        try {
            ((AltButton) buttonList.get(val)).setSelected(selected = true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void mouseClicked(final int x, final int y, final int button) {
        selected = (x > 5 && x < width - 170 && y > 5 && y < height - 5);

        inBoundsCheck = x > width - 75 && x < width - 5 && y < height - 45 && y > height - 75;
        try {
            super.mouseClicked(x, y, button);
        } catch (IOException e) {
            e.printStackTrace();
        }
        email.mouseClicked(x, y, button);
        pass.mouseClicked(x, y, button);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void updateScreen() {
        email.updateCursorCounter();
        pass.updateCursorCounter();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:

            break;
            case 1: //Remove
                ArrayList<GuiButton> buttons = new ArrayList<GuiButton>(buttonList);
                for (GuiButton g : buttons) {
                    if (g instanceof AltButton) {
                        AltButton ab = (AltButton) g;
                        if (ab.isSelected()) {
                            Eris.getInstance().altManager.removeAlt(ab.getAlt());
                        }
                    }
                    refresh();
                }
                selected = false;
                status = "";
            break;
            case 2: //Import
                if (!inBoundsCheck) return;
                selected = false;
                Thread fileChoose = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JFileChooser fc = new JFileChooser("Import Alts");
                        int returnVal = fc.showOpenDialog(null);

                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            File file = fc.getSelectedFile();
                            if (file.exists()) {
                                ArrayList<String> lines = FileUtils.getLines(file);
                                for (String str : lines) {
                                    if (str.contains(":") && !str.startsWith(":") && !str.endsWith(":")) {
                                        Eris.getInstance().altManager.addAlt(new Alt(str.split(":")[0], str.substring(str.indexOf(":") + 1)));
                                    }
                                }
                                refresh();
                            }
                        }
                    }

                });
                fileChoose.start();
            break;
            case 3: //Clear
                selected = false;
                Eris.getInstance().altManager.getManagerArraylist().clear();
                refresh();
                Eris.INSTANCE.fileManager.saveDataFile();
            break;
            case 4://Add
                selected = false;
                if (!email.getText().isEmpty() ) {
                    if (!pass.getText().isEmpty()) {
                        try {
                            String email = this.email.getText();
                            String pass = this.pass.getText();
                            Eris.getInstance().altManager.addAlt(new Alt(email, pass));
                            Eris.INSTANCE.fileManager.saveAltsFile();
                            refresh();
                        } catch (Exception e) {
                        }
                    } else {
                        try {
                            String email =  this.email.getText();
                            Eris.getInstance().altManager.addAlt(new Alt(email, ""));
                            Eris.INSTANCE.fileManager.saveAltsFile();
                            refresh();
                        } catch (Exception e) {
                        }
                    }
                }
            break;
            case 5://Login
                if (!email.getText().isEmpty()) {
                    if (!pass.getText().isEmpty()) {
                        login(new Alt(email.getText(), pass.getText()));
                    } else {
                        login(new Alt(email.getText(), ""));
                    }
                } else {
                    status = "Email or Username needed for login";
                }
                selected = false;
            break;
            case 6://Clipboard
                selected = false;
                try {
                    if (Utils.getLatestPaste() != null) {
                        String result = Utils.getLatestPaste();
                        if (result.contains(":") && !result.startsWith(":") && !result.endsWith(":")) {
                            email.setText(result.split(":")[0]);
                            pass.setText(result.split(":")[1]);
                        }
                    }
                } catch (Exception e) {
                }
            break;
            case 7://GuiAltening
                selected = false;
                mc.displayGuiScreen(new GuiTheAltening(this));
            break;

        }
        selected = false;
        Eris.INSTANCE.fileManager.saveAltsFile();
    }
}
