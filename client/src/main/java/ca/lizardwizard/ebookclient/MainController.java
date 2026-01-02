package ca.lizardwizard.ebookclient;

import ca.lizardwizard.ebookclient.AudioLib.AudioLoader;
import ca.lizardwizard.ebookclient.AudioLib.AudioPlayer;
import ca.lizardwizard.ebookclient.objects.Book;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

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
    private Text DebugText;

    @FXML
    private Text timeText;

    @FXML
    private Font x3;

    @FXML
    private Color x4;

    private AudioPlayer player = new AudioPlayer();

    private Timeline timeline;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {



        try{
            BookList.getItems().addAll(ApiCalls.getBooks());
            BookList.getSelectionModel().selectedItemProperty().addListener((observe, previousBook, currentBook)->{
                NowListeningText.setText("Now Listening to "+currentBook.getName() +"\nBy " + currentBook.getAuthor());
                DetailsBookImage.setImage(new Image("http://localhost:5000/books/"+currentBook.getId()+"/cover"));
                try {
                    playButton.setText("Loading Audio...");
                    // 1) Download to memory from your Flask streaming endpoint
                    byte[] audioBytes = AudioLoader.downloadAudioToMemory(currentBook.getId());
                    // 2) Load into in-memory player
                    player.loadFromBytes(audioBytes);

                    player.play();
                    playButton.setText("Pause");

                    //Setup timeline
                    timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> updateUI()));
                    timeline.setCycleCount(Timeline.INDEFINITE);
                    timeline.play();

                }catch (IOException | UnsupportedAudioFileException | LineUnavailableException error){
                    throw new RuntimeException(error);
                }

            });

            audioTimeline.valueProperty().addListener((obs, oldVal, newVal) -> {
                if ((newVal.doubleValue() <oldVal.doubleValue()) && ((newVal.doubleValue()+1) >oldVal.doubleValue()) ) //Quick fix where when adjusting value would double trigger because of manually moving timeline + updateUI moving it. Remove this line to inspect the issue.
                    return;
                System.out.println("Slider value: " + newVal);
                DebugText.setText("Slider value: " + String.format("%.2f", newVal.doubleValue()));
                player.setByPercentage(newVal.doubleValue());

            });

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateUI(){
        timeText.setText(player.getFormattedLength());

        audioTimeline.adjustValue(player.getPercentCompleted());

    }

    @FXML
    protected void onPlayButton(){
        if(player.getIsPlaying()) {
            player.pause();
            playButton.setText("Play");
        }
        else{
            player.play();
            playButton.setText("Pause");
        }
    }


    @FXML
    protected void onRewindButton(){
        player.rewind();
    }

    @FXML
    protected void onFastForwardButton(){
        player.forward();
    }
}
