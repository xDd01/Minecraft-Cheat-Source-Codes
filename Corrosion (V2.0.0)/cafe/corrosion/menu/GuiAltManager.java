/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.menu;

import cafe.corrosion.Corrosion;
import cafe.corrosion.font.TTFFontRenderer;
import cafe.corrosion.menu.LoginThread;
import cafe.corrosion.util.account.AltAccountType;
import cafe.corrosion.util.account.impl.AltAccount;
import cafe.corrosion.util.font.type.FontType;
import cafe.corrosion.util.render.GuiUtils;
import cafe.corrosion.util.render.RenderUtil;
import cafe.corrosion.util.web.ImageDownloader;
import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.auth.service.MsaAuthenticationService;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class GuiAltManager
extends GuiScreen {
    private static final ArrayDeque<AltAccount> ACCOUNTS = new ArrayDeque();
    private static final ResourceLocation TITLE_TEXTURE = new ResourceLocation("corrosion/logos/corrosion.png");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<Integer, String> ID_DESCRIPTORS = ImmutableMap.of(101, "Login (Mojang)", 102, "Login (Microsoft)", 103, "Add", 104, "Remove", 105, "Clipboard");
    private static AltAccount currentAccount;
    private final GuiScreen previousScreen;
    private AltAccount selectedAccount;
    private GuiTextField password;
    private GuiTextField username;
    private LoginThread thread;
    private int verticalMouseOffset;

    public GuiAltManager(GuiScreen previousScreen) {
        this.previousScreen = previousScreen;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 101: {
                if (this.selectedAccount != null && this.username.getText().isEmpty() && this.selectedAccount.getAccountType() == AltAccountType.MOJANG) {
                    this.thread = new LoginThread(this.selectedAccount.getEmail(), this.selectedAccount.getPassword(), this);
                    this.thread.start();
                    break;
                }
                this.thread = new LoginThread(this.username.getText(), this.password.getText(), this);
                this.thread.start();
                break;
            }
            case 102: {
                try {
                    MsaAuthenticationService msaAuthenticationService = new MsaAuthenticationService("");
                    if (this.selectedAccount != null && this.username.getText().isEmpty() && this.selectedAccount.getAccountType() == AltAccountType.MICROSOFT) {
                        msaAuthenticationService.setUsername(this.selectedAccount.getEmail());
                        msaAuthenticationService.setPassword(this.selectedAccount.getPassword());
                    } else {
                        msaAuthenticationService.setUsername(this.username.getText());
                        msaAuthenticationService.setPassword(this.password.getText());
                    }
                    msaAuthenticationService.login();
                    GameProfile gameProfile = msaAuthenticationService.getAvailableProfiles().get(0);
                    this.mc.session = new Session(gameProfile.getName(), gameProfile.getIdAsString(), msaAuthenticationService.getAccessToken(), "mojang");
                    currentAccount = new AltAccount(gameProfile.getName(), this.username.getText(), this.password.getText(), AltAccountType.MICROSOFT);
                }
                catch (Exception nigger) {
                    nigger.printStackTrace();
                }
                break;
            }
            case 103: {
                if (currentAccount == null || this.selectedAccount == currentAccount) {
                    return;
                }
                String name = currentAccount.getName();
                ACCOUNTS.removeIf(altAccount -> altAccount.getName().equals(name));
                ACCOUNTS.addFirst(currentAccount);
                this.writeAltListContents();
            }
            case 104: {
                if (this.selectedAccount == null) {
                    return;
                }
                ACCOUNTS.remove(this.selectedAccount);
                if (!ACCOUNTS.isEmpty()) {
                    this.selectedAccount = ACCOUNTS.getFirst();
                }
                this.writeAltListContents();
                break;
            }
            case 105: {
                String data = (String)Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
                if (!data.contains(":")) break;
                String[] credentials = data.split(":", 2);
                this.username.setText(credentials[0]);
                this.password.setText(credentials[1]);
                break;
            }
        }
    }

    @Override
    public void drawScreen(int x2, int y2, float z2) {
        ResourceLocation location;
        int startColor = new Color(23, 23, 23).getRGB();
        int endColor = new Color(92, 92, 92).getRGB();
        int boxColor = new Color(56, 56, 56).getRGB();
        int bottomBoxColor = new Color(38, 37, 37).getRGB();
        int boxTextColor = new Color(134, 134, 134).getRGB();
        int borderColor = -6250336;
        int mainTextColor = new Color(219, 219, 219).getRGB();
        ScaledResolution scaledResolution = new ScaledResolution(this.mc);
        GuiAltManager.drawRect(0.0, 0.0, this.width, this.height, Integer.MAX_VALUE);
        this.drawGradientRect(0, 0, this.width, this.height, -2130706433, 0xFFFFFF);
        this.drawGradientRect(0, 0, this.width, this.height, startColor, endColor);
        GuiUtils.drawImage(TITLE_TEXTURE, 35.0f, 10.0f, 140.0f, 45.0f, -1);
        RenderUtil.drawRoundedRect(15.0f, 60.0f, 195.0f, scaledResolution.getScaledHeight() - 20, boxColor, boxColor);
        RenderUtil.drawRoundedRect(205.0f, 60.0f, scaledResolution.getScaledWidth() - 15, scaledResolution.getScaledHeight() - 60, boxColor, boxColor);
        RenderUtil.drawRoundedRect(15.0f, scaledResolution.getScaledHeight() - 60, scaledResolution.getScaledWidth() - 15, scaledResolution.getScaledHeight() - 20, bottomBoxColor, bottomBoxColor);
        TTFFontRenderer font = Corrosion.INSTANCE.getFontManager().getFontRenderer(FontType.FIRA_CODE_MEDIUM, 24.0f);
        TTFFontRenderer smallerFont = Corrosion.INSTANCE.getFontManager().getFontRenderer(FontType.ROBOTO, 22.0f);
        String name = currentAccount == null ? "Steve" : currentAccount.getName();
        try {
            location = ImageDownloader.getTextureAsLocation(name + "-full");
        }
        catch (Exception exc) {
            location = null;
        }
        if (Mouse.hasWheel()) {
            int wheel = Mouse.getDWheel();
            if (wheel > 0) {
                this.verticalMouseOffset = Math.min(0, this.verticalMouseOffset + 26);
            } else if (wheel < 0) {
                this.verticalMouseOffset = Math.min(0, this.verticalMouseOffset - 26);
            }
        }
        int lastPosY = 60;
        int altOffset = Math.abs(this.verticalMouseOffset) / 36;
        int offsetCompletion = 0;
        Iterator<AltAccount> iterator = ACCOUNTS.iterator();
        while (iterator.hasNext() && lastPosY < scaledResolution.getScaledHeight() - 120) {
            ResourceLocation head;
            AltAccount account = iterator.next();
            if (account == null || altOffset > 0 && ++offsetCompletion < altOffset) continue;
            try {
                head = ImageDownloader.getTextureAsLocation(account.getName() + "-head");
            }
            catch (Exception exc) {
                head = null;
            }
            if (head != null) {
                GuiUtils.drawImage(head, 205.0f, lastPosY, 48.0f, 48.0f, -1);
            }
            font.drawString(account.getName(), 255.0f, lastPosY + 8, mainTextColor);
            smallerFont.drawString(account.getAccountType().getName() + " Account", 255.0f, lastPosY + 22, mainTextColor);
            if (this.selectedAccount == account) {
                RenderUtil.drawRect(204.0f, lastPosY, 205.0f, lastPosY + 48, mainTextColor);
                RenderUtil.drawRect(204.0f, lastPosY, 450.0f, lastPosY + 1, mainTextColor);
                RenderUtil.drawRect(204.0f, lastPosY + 48, 450.0f, lastPosY + 49, mainTextColor);
                RenderUtil.drawRect(449.0f, lastPosY, 450.0f, lastPosY + 48, mainTextColor);
            }
            lastPosY += 48;
        }
        if (location != null) {
            GuiUtils.drawImage(location, 55.0f, (float)scaledResolution.getScaledHeight() / 3.5f, 100.0f, 162.0f, -1);
        }
        font.drawString(name, (180.0f - font.getWidth(name)) / 2.0f + 20.0f, (float)scaledResolution.getScaledHeight() / 3.5f + 175.0f, Color.WHITE.getRGB());
        this.username.drawTextBox();
        this.password.drawTextBox();
        if (this.username.getText().isEmpty()) {
            this.mc.fontRendererObj.drawStringWithShadow("Email / Username", 25.0f, scaledResolution.getScaledHeight() - 44, -7829368);
        }
        if (this.password.getText().isEmpty()) {
            this.mc.fontRendererObj.drawStringWithShadow("Password", 180.0f, scaledResolution.getScaledHeight() - 44, -7829368);
        }
        super.drawScreen(x2, y2, z2);
        if (this.thread != null) {
            font.drawString(this.thread.getStatus(), 300.0f, 20.0f, boxTextColor);
        }
        this.buttonList.forEach(button -> {
            button.drawBorder(borderColor);
            String text = ID_DESCRIPTORS.get(button.id);
            this.mc.fontRendererObj.drawString(text, button.xPosition + 5, button.yPosition + 6, boxTextColor);
        });
    }

    @Override
    public void initGui() {
        ScaledResolution scaledResolution = new ScaledResolution(this.mc);
        Color notHovered = new Color(-16777216);
        Color hovered = new Color(1, 1, 1, 1);
        Color textColor = new Color(184, 184, 184);
        this.username = new GuiTextField(100, this.mc.fontRendererObj, 20, scaledResolution.getScaledHeight() - 50, 150, 20);
        this.password = new GuiTextField(100, this.mc.fontRendererObj, 175, scaledResolution.getScaledHeight() - 50, 150, 20);
        this.buttonList.add(new GuiButton(101, 330, scaledResolution.getScaledHeight() - 50, 95, 20, "", notHovered, hovered, textColor));
        this.buttonList.add(new GuiButton(102, 430, scaledResolution.getScaledHeight() - 50, 95, 20, "", notHovered, hovered, textColor));
        this.buttonList.add(new GuiButton(103, 530, scaledResolution.getScaledHeight() - 50, 50, 20, "", notHovered, hovered, textColor));
        this.buttonList.add(new GuiButton(104, 585, scaledResolution.getScaledHeight() - 50, 50, 20, "", notHovered, hovered, textColor));
        this.buttonList.add(new GuiButton(105, 640, scaledResolution.getScaledHeight() - 50, 75, 20, "", notHovered, hovered, textColor));
        this.username.setFocused(true);
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    protected void keyTyped(char character, int key) throws IOException {
        super.keyTyped(character, key);
        if (character == '\t') {
            if (!this.username.isFocused() && !this.password.isFocused()) {
                this.username.setFocused(true);
            } else {
                this.username.setFocused(this.password.isFocused());
                this.password.setFocused(!this.username.isFocused());
            }
        } else if (character == '\r') {
            this.actionPerformed((GuiButton)this.buttonList.get(0));
        }
        this.username.textboxKeyTyped(character, key);
        this.password.textboxKeyTyped(character, key);
    }

    @Override
    protected void mouseClicked(int x2, int y2, int button) throws IOException {
        super.mouseClicked(x2, y2, button);
        this.username.mouseClicked(x2, y2, button);
        this.password.mouseClicked(x2, y2, button);
        int index = 0;
        int baseYFactor = 60;
        int baseX = 205;
        int yModifier = 48;
        int xModifier = 245;
        for (AltAccount account : ACCOUNTS) {
            int baseY = baseYFactor + index * 48;
            if (GuiUtils.isHoveringPos(x2, y2, baseX, baseY, baseX + xModifier, baseY + yModifier)) {
                this.selectedAccount = account;
                return;
            }
            ++index;
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void updateScreen() {
        this.username.updateCursorCounter();
        this.password.updateCursorCounter();
    }

    public static void init() {
        Path path = Corrosion.INSTANCE.getAltsPath();
        if (!path.toFile().exists()) {
            return;
        }
        try {
            byte[] bytes = Files.readAllBytes(path);
            JsonArray array = GSON.fromJson(new String(bytes), JsonArray.class);
            array.forEach(element -> {
                JsonObject object = element.getAsJsonObject();
                AltAccount account = new AltAccount(object);
                ACCOUNTS.add(account);
                String username = account.getName();
                ImageDownloader.downloadImage(username);
            });
        }
        catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    public void onAccountLoad(String username, String emailUsed, String passwordUsed) {
        boolean present = ACCOUNTS.stream().anyMatch(account -> account.getEmail().equalsIgnoreCase(emailUsed) || account.getName().equals(username));
        if (present) {
            ACCOUNTS.removeIf(account -> account.getName().equalsIgnoreCase(username));
        } else {
            ImageDownloader.downloadImage(username);
        }
        currentAccount = new AltAccount(username, emailUsed, passwordUsed, AltAccountType.MOJANG);
    }

    public void writeAltListContents() {
        JsonArray accounts = new JsonArray();
        ACCOUNTS.forEach(account -> accounts.add(account.serialize()));
        Path path = Paths.get("Corrosion/alts.json", new String[0]);
        byte[] bytes = GSON.toJson(accounts).getBytes();
        Files.write(path, bytes, new OpenOption[0]);
    }
}

