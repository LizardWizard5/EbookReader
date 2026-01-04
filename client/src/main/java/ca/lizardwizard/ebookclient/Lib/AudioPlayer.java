/**
 * AudioPlayer.java
 * Written by Eric Wooldridge
 * Purpose: Handle playing and overall controls of audio.
 */

package ca.lizardwizard.ebookclient.Lib;
import ca.lizardwizard.ebookclient.objects.Book;
import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Currently being rewritten to fit more with the pull more chunks as needed design the server is now pushing towards
 * This currently is being done with AudioInputStream and CLip but more research is needed to figure these out and maybe even a better method
 * Constructors:
 * AudioPlayer() - Only to be used for initializing an AudioPlayer, will not work without populating the currentBook var
 * AudioPlayer(Book)
 * Available Methods:
 * loadFromBytes(long) - Used to load more
 */
public class AudioPlayer {

    private Clip clip;
    private boolean isPlaying;
    private Book currentBook;
    private long overallPosition; // Used to store the overall position that we would be in the book
    private AudioInputStream ais;
    private  byte[] loadedBytes = new byte[1];
    /**
     * Only to be used for initializing an AudioPlayer, will not work without populating the currentBook var
     */
    public AudioPlayer(){
            this.currentBook=null;
    }
    public AudioPlayer(Book book){
        this.currentBook = book;

    }

    /**
     *
     * @param ms represented as microseconds will specify the point in audio where we want to grab a chunk from so server will return audio chunk of ms+5Minutes(Server set to return 5 mins)
     * @throws UnsupportedAudioFileException
     * @throws IOException
     * @throws LineUnavailableException
     */
    public void loadFromBytes(long ms)  throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        byte[] wavData = ApiCalls.downloadAudioToMemory(currentBook.getId(),ms);
        // Wrap bytes in an InputStream → AudioInputStream
        try (ByteArrayInputStream bais = new ByteArrayInputStream(wavData);
             AudioInputStream ais = AudioSystem.getAudioInputStream(bais)) {

            clip = AudioSystem.getClip();

            clip.open(ais);  // Clip loads the entire audio data into memory
            byte[] mergedArray = new byte[loadedBytes.length + wavData.length];
            // Source array, source start index, destination array, destination start index, number of elements
            System.arraycopy(loadedBytes, 0, mergedArray, 0, loadedBytes.length);
            System.arraycopy(wavData, 0, mergedArray, loadedBytes.length, wavData.length);
        }
        isPlaying = false;
        // After this, clip has the data; wavData & streams can be GC'ed if you drop references
    }

    public void play(){
        if (clip == null) return;
        clip.start();
    }
    public void stop(){
        if (clip == null) return;
        clip.stop();
    }


    // BELOW METHDOS ARE EXCLUSIVELY CLIP RElATED

    /**
     *
     * @param format Used for determing how you want it formatted as:
     *               0: Microseconds,
     *               1: seconds,
     *               2: minutes,
     *               3: hours.
     * @return long - clip length in desired format.  Returns as microseconds with invalid number
     */
    public long grabClipLength(int format){
        return switch (format) {
            case 0 -> clip.getMicrosecondLength();
            case 1 -> clip.getMicrosecondLength() / 1000000L;
            case 2 -> clip.getMicrosecondLength() / 60000000L;
            case 3 -> clip.getMicrosecondLength() / 3600000000L;
            default -> clip.getMicrosecondLength();
        };
    }
   /* public String formatMsToString(long ms){
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
        String currentTime = formatMsToString(clip.getMicrosecondPosition() );
        String endTime = formatMsToString(currentBook.getAudioLength() );
        return  currentTime+"/"+endTime +"(" +getPercentCompleted()+"% Completed)";//getPercent does not work right now
    }*/


}
