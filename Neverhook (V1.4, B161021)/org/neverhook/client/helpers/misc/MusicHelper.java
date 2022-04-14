package org.neverhook.client.helpers.misc;

import org.neverhook.client.NeverHook;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class MusicHelper {

    public static void playSound(final String url) {
        new Thread(() -> {
            try {
                Clip clip = AudioSystem.getClip();
                AudioInputStream inputStream = AudioSystem.getAudioInputStream(NeverHook.class.getResourceAsStream("/assets/minecraft/neverhook/songs/" + url));
                clip.open(inputStream);
                clip.start();
            } catch (Exception e) {

            }
        }).start();
    }

}
