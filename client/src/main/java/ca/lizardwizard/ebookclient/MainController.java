package ca.lizardwizard.ebookclient;

import ca.lizardwizard.ebookclient.AudioLib.AudioLoader;
import ca.lizardwizard.ebookclient.AudioLib.AudioPlayer;
import ca.lizardwizard.ebookclient.objects.Book;
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
    private Font x3;

    @FXML
    private Color x4;

    private AudioPlayer player = new AudioPlayer();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try{
            BookList.getItems().addAll(ApiCalls.getBooks());
            BookList.getSelectionModel().selectedItemProperty().addListener((observe, previousBook, currentBook)->{
                //Sets the vbox to visible
                //Sets name role label text
                NowListeningText.setText("Now Listening to "+currentBook.getName() +"\nBy " + currentBook.getAuthor());
                //Sets the image as the heroes' portrait.
                //heroImage.setImage(new Image(currentHero.getPortrait()));
                DetailsBookImage.setImage(new Image("http://localhost:5000/books/"+currentBook.getId()+"/cover"));
                try {
                    playButton.setText("Loading Audio...");
                    // 1) Download to memory from your Flask streaming endpoint
                    byte[] audioBytes = AudioLoader.downloadAudioToMemory(currentBook.getId());
                    // 2) Load into in-memory player
                    player.loadFromBytes(audioBytes);
                    player.play();
                    playButton.setText("Pause");
                }catch (IOException | UnsupportedAudioFileException | LineUnavailableException error){
                    throw new RuntimeException(error);
                }

            });

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
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
