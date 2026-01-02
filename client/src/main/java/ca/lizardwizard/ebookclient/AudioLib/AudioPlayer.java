package ca.lizardwizard.ebookclient.AudioLib;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class AudioPlayer {

    private Clip clip;
    private boolean isPlaying;

    public void loadFromBytes(byte[] wavData)
            throws UnsupportedAudioFileException, IOException, LineUnavailableException {

        // Wrap bytes in an InputStream → AudioInputStream
        try (ByteArrayInputStream bais = new ByteArrayInputStream(wavData);
             AudioInputStream ais = AudioSystem.getAudioInputStream(bais)) {

            clip = AudioSystem.getClip();
            clip.open(ais);  // Clip loads the entire audio data into memory
        }
        isPlaying = false;
        // After this, clip has the data; wavData & streams can be GC'ed if you drop references
    }

    public void play() {
        if (clip == null) return;
        if (!clip.isRunning()) {
            isPlaying = true;
            clip.start();
        }
    }

    public void pause() {
        if (clip == null) return;
        if (clip.isRunning()) {
            isPlaying = false;
            clip.stop();
        }
    }
    public void rewind() {
        if (clip == null) return;
        clip.stop();

        long newPos = clip.getMicrosecondPosition() - 10_000_000L;
        if (newPos < 0) newPos = 0;  // clamp to start

        clip.setMicrosecondPosition(newPos);
        if(isPlaying)
            clip.start();
    }

    public void forward() {
        if (clip == null) return;
        clip.stop();

        long newPos = clip.getMicrosecondPosition() + 10_000_000L;
        long max = clip.getMicrosecondLength();

        if (newPos > max) newPos = max; // clamp to end

        clip.setMicrosecondPosition(newPos);
        if(isPlaying)
            clip.start();
    }

    public void seek(double seconds) {
        if (clip == null) return;
        long microseconds = (long) (seconds * 1_000_000L);
        clip.setMicrosecondPosition(microseconds);
    }

    public void setByPercentage(double percent){
        if(clip == null) return;
        long microseconds = (long)( clip.getMicrosecondLength() * (percent/100));
        System.out.println("Inserted "+percent+"% Searching to " + microseconds +" / " + clip.getMicrosecondLength());

        clip.setMicrosecondPosition(microseconds);

    }

    public long getLengthSeconds() {
        if (clip == null) return 0;
        return clip.getMicrosecondLength() / 1_000_000L;
    }

    public long getPercentCompleted(){
        if (clip == null) return 0;

        return (clip.getMicrosecondPosition() * 100) / clip.getMicrosecondLength();
    }

    public String formatMsToString(long ms){
        if (clip == null || ms ==0) return "00:00:00";


        long h=0,m=0,s=ms/ 1_000_000L;
        while(s>=60){
            s-=60;
            m++;
            if(m>=60) {
                m =0;
                h++;
            }

        }
        return h+":"+m+":"+s;
    }

    public String getFormattedLength(){
        if (clip == null) return "00:00:00/00:00:00 (0% Completed)";
        String currentTime = formatMsToString(clip.getMicrosecondPosition());
        String endTime = formatMsToString(clip.getMicrosecondLength());
        return  currentTime+"/"+endTime +"(" +getPercentCompleted()+"% Completed)";
    }

    public boolean getIsPlaying(){
        return isPlaying;
    }

    public void close() {
        if (clip != null) {
            clip.stop();
            clip.close();
        }
    }
}
