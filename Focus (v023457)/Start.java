import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import Focus.Beta.UTILS.helper.Hwid;
import Focus.Beta.UTILS.world.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.Main;

public class Start
{
    public static void main(String[] args)
    {
        try {
            System.out.println(Hwid.getHWID());
            System.out.println("Checking HWID...");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if(Hwid.blacklisted()){
            System.out.println("Success!");

            Main.main(concat(new String[] {"--version", "mcp", "--accessToken", "0", "--assetsDir", "assets", "--assetIndex", "1.8", "--userProperties", "{}"}, args));
        }else{

            System.out.println("BlackListed");

        }
    }

    public static <T> T[] concat(T[] first, T[] second)
    {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}
