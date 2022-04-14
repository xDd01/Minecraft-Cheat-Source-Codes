package me.spec.eris.client.ui.login;

import java.io.IOException;

import me.spec.eris.client.ui.main.buttons.ExpandButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import me.spec.eris.Eris;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class GuiLogin extends GuiScreen {

    private GuiPasswordField password;
    private final GuiScreen previousScreen;
    private GuiTextField name;
    private GuiTextField uid;

    protected Minecraft mc = Minecraft.getMinecraft();

    public GuiLogin(final GuiScreen previousScreen) {
        this.previousScreen = previousScreen;
    }

    @Override
    protected void actionPerformed(final GuiButton button) throws IOException {
        switch (button.id) {
            case 1: {
                mc.displayGuiScreen(this.previousScreen);
                break;
            }
            case 0: {
                try {

              //      InvalidProcess.run();
             //       AntiHostsEdit.run();
            //        AntiVM.run();
                 //   SerDes serializer = new SerDes(null, Arrays.asList(C42069ProtectionPacket.class, C42069ProtectionPacket.class));
               //     ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    //DataOutputStream output = new DataOutputStream(baos);
                    //C42069ProtectionPacket packet = new C42069ProtectionPacket();
                   // packet.NAME = this.name.getText();
                   // packet.PASSWORD = this.password.getText();
                    //packet.UID = Integer.parseInt(this.uid.getText());
          //          serializer.writeObject(packet, output);
             //       output.flush();
	        		/*Socket s = new Socket("127.0.0.1", 3000);
	        		PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
	        		pw.println(new String(baos.toByteArray()));
	        		
	        		BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
	        		String line = br.readLine();
	        		if (line == null) {
	        			return;
	        		}
	        		while (line != null) {
	        			
	        			try {
	        				line = br.readLine();
	        			} catch (Exception e) {
	        				line = null;
	        				return;
	        			}
	        		}*/
                    Eris.INSTANCE = new Eris();
                    Eris.INSTANCE.onStart();
                    mc.displayGuiScreen(this.previousScreen);
                } catch (Exception e) {}
                break;
            }
        }
    }

    @Override
    public void drawScreen(final int x, final int y, final float z) {
        this.drawDefaultBackground();
        this.password.drawTextBox();
        this.name.drawTextBox();
        this.uid.drawTextBox();
        if (this.password.getText().isEmpty()) {
          mc.fontRendererObj.drawStringWithShadow("Password", (float) (this.width / 2 - 94), height / 2 - 14, -7829368);
        }
        if (this.uid.getText().isEmpty()) {
            mc.fontRendererObj.drawStringWithShadow("UID", (float) (this.width / 2 - 94), height / 2 - 68, -7829368);
        }
        if (this.name.getText().isEmpty()) {
            mc.fontRendererObj.drawStringWithShadow("Name", (float) (this.width / 2 - 94), height / 2 - 39, -7829368);
        }
        super.drawScreen(x, y, z);
    }

    @Override
    public void initGui() {
        this.drawDefaultBackground();
        Display.setTitle("You need to login to Eris - Just press login, login disabled until release is ready");
        final int var3 = this.height / 4 + 24;
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, height / 2 + 10, "Login"));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, height / 2 + 35, "Exit Client"));
        this.password = new GuiPasswordField(this.fontRendererObj, this.width / 2 - 98, height / 2 - 20, 195, 20);
        this.name = new GuiTextField(var3, this.fontRendererObj, this.width / 2 - 98, height / 2 - 45, 195, 20);
        this.uid = new GuiTextField(var3, this.fontRendererObj, this.width / 2 - 98, height / 2 - 75, 195, 20);
        this.uid.setFocused(true);
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    protected void keyTyped(final char character, final int key) throws IOException {
        try {
            super.keyTyped(character, key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (character == '\t' && (this.password.isFocused() || this.name.isFocused() || this.uid.isFocused())) {
            this.password.setFocused(!this.password.isFocused());
            this.name.setFocused(!this.name.isFocused());
            this.uid.setFocused(!this.uid.isFocused());
        }
        if (character == '\r') {
            this.actionPerformed(this.buttonList.get(0));
        }
        this.password.textboxKeyTyped(character, key);
        this.name.textboxKeyTyped(character, key);
        if (key == 14) {
            this.uid.textboxKeyTyped(character, key);
        } else {
            try {
                Integer.parseInt(character + "");
                this.uid.textboxKeyTyped(character, key);
            } catch (Exception e) {
            }
        }
    }

    @Override
    protected void mouseClicked(final int x, final int y, final int button) {
        try {
            super.mouseClicked(x, y, button);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.password.mouseClicked(x, y, button);
        this.name.mouseClicked(x, y, button);
        this.uid.mouseClicked(x, y, button);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void updateScreen() {
        this.password.updateCursorCounter();
        this.uid.updateCursorCounter();
        this.name.updateCursorCounter();
    }
}
