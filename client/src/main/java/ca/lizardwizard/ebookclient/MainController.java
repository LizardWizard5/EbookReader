package ca.lizardwizard.ebookclient;

import ca.lizardwizard.ebookclient.Lib.ApiCalls;
import ca.lizardwizard.ebookclient.Lib.AudioPlayer;
import ca.lizardwizard.ebookclient.Lib.SceneUtil;
import ca.lizardwizard.ebookclient.objects.Book;

import javafx.animation.Timeline;
import javafx.event.ActionEvent;

import javafx.fxml.FXML;

import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import javafx.util.Duration;
import javafx.scene.media.MediaPlayer;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import static ca.lizardwizard.ebookclient.Lib.SceneUtil.switchScenes;

public class MainController implements Initializable {

    @FXML
    private Button AddBookButton;

    @FXML
    private ListView<Book> BookList;

    @FXML
    private ImageView DetailsBookImage;

    @FXML
    private Slider audioTimeline;

    @FXML
    private Button fastForwardButton;

    @FXML
    private Button playButton;

    @FXML
    private Button rewindButton;

    @FXML
    private Label statusText;

    @FXML
    private Text NowListeningText;

    @FXML
    private Text timeText;


    @FXML
    private MediaPlayer mediaPlayer;

    private AudioPlayer player = new AudioPlayer();

    private Timeline timeline;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try{
            BookList.getItems().addAll(ApiCalls.getBooks());
            BookList.getSelectionModel().selectedItemProperty().addListener((observe, previousBook, currentBook)->{
                //Start getting media loaded
                Media media = new Media("http://127.0.0.1:5000/stream/"+currentBook.getId());
                mediaPlayer = new MediaPlayer(media);
                //Setup UI
                NowListeningText.setText("Now Listening to "+currentBook.getName() +"\nBy " + currentBook.getAuthor());
                statusText.setText("Now Listening to "+currentBook.getName() +" By " + currentBook.getAuthor());
                DetailsBookImage.setImage(new Image("http://localhost:5000/books/"+currentBook.getId()+"/cover"));
                DetailsBookImage.setOpacity(1);
                NowListeningText.setOpacity(1);

                rewindButton.setDisable(false);
                fastForwardButton.setDisable(false);
                audioTimeline.setDisable(false);
                playButton.setDisable(false);
                //Play Audio on ready
                mediaPlayer.setOnReady(() -> {
                    // Set slider range to the duration of the book
                    double totalMs = mediaPlayer.getTotalDuration().toMillis();
                    mediaPlayer.play();
                    audioTimeline.setMax(totalMs);
                });

                mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                    if (!audioTimeline.isValueChanging()) { // Don't move slider while user is dragging it
                        audioTimeline.setValue(newTime.toMillis());
                        timeText.setText(getFormattedLength((long)Duration.millis(audioTimeline.getValue()).toMillis(),(long)mediaPlayer.getTotalDuration().toMillis()));
                    }
                });

                audioTimeline.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
                    if (!isChanging) { // User let go of the slider
                        mediaPlayer.seek(Duration.millis(audioTimeline.getValue()));
                    }
                });

            });



        } catch (IOException e) {
            System.out.println("error detected when initializing MainController");
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void onRefreshButton(ActionEvent e) throws IOException {
        BookList.getItems().clear();
        BookList.getItems().addAll(ApiCalls.getBooks());
    }
    @FXML
    protected void onPlayButton(){


        if(mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING    ) {
            mediaPlayer.pause();
            playButton.setText("Play");
        }
        else{
            mediaPlayer.play();
            playButton.setText("Pause");
        }

    }


    @FXML
    protected void onRewindButton(){



        Duration cTime = mediaPlayer.getCurrentTime();
        Duration newTime = cTime.subtract(Duration.seconds(10));
        if(newTime.lessThan(Duration.seconds(0)))
            newTime=Duration.seconds(0);
        mediaPlayer.seek(newTime);
    }

    @FXML
    protected void onFastForwardButton(){
        Duration cTime = mediaPlayer.getCurrentTime();
        Duration totalTime = mediaPlayer.getTotalDuration();
        Duration newTime = cTime.add(Duration.seconds(10));
        if(newTime.greaterThan(totalTime))
            newTime=totalTime;
        mediaPlayer.seek(newTime);
    }

    @FXML
    protected void onUploadPdf(ActionEvent e) throws IOException {
        switchScenes(e,"upload-pdf");

    }

    public String formatMsToString(long ms) {

        long totalSeconds = ms / 1000;

        long h = totalSeconds / 3600;
        long m = (totalSeconds % 3600) / 60;
        long s = totalSeconds % 60;


        if (h > 0) {
            return String.format("%d:%02d:%02d", h, m, s);
        } else {
            return String.format("%02d:%02d", m, s);
        }
    }

    public String getFormattedLength(long time, long end) {
        if (end <= 0) return "00:00 / --:-- (0% Completed)";

        String currentTime = formatMsToString(time);
        String endTime = formatMsToString(end);

        // 3. Fix the percentage math: cast to double first
        int percent = (int) (((double) time / end) * 100);

        return currentTime + " / " + endTime + " (" + percent + "% Completed)";
    }
}
