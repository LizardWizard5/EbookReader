package ca.lizardwizard.ebookclient;

import ca.lizardwizard.ebookclient.AudioLib.AudioLoader;
import ca.lizardwizard.ebookclient.AudioLib.AudioPlayer;


import java.io.IOException;
import java.util.Arrays;



public class TestMain {
    public static void main(String[] args) {
        try {
            // 1) Download to memory from your Flask streaming endpoint
            byte[] audioBytes = AudioLoader.downloadAudioToMemory(1);
            // 2) Load into in-memory player
            AudioPlayer player = new AudioPlayer();
            player.loadFromBytes(audioBytes);

            System.out.println("Audio length (s): " + player.getLengthSeconds());

            System.out.println("Length of audio: " + player.getFormattedLength());

            player.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
