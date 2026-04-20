package ca.lizardwizard.ebookclient;

import ca.lizardwizard.ebookclient.Lib.*;
import ca.lizardwizard.ebookclient.objects.Book;

import ca.lizardwizard.ebookclient.objects.RecentlyListened;
import javafx.animation.KeyFrame;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import static ca.lizardwizard.ebookclient.Lib.SceneUtil.switchScenes;

public class MainController implements Initializable {

    @FXML
    private Button AddBookButton;
    @FXML
    private ListView<Book> RecentBooksList;
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


    private void setupMediaPlayer(Book currentBook) throws FileNotFoundException {
        //Construct url.
        String host = new EnvReader<String>().readVar("HOST");
        String port = new EnvReader<String>().readVar("PORT");
        String baseUrl = "http://" + host + ":" + port;
        Media media = new Media(baseUrl + "/stream/" + currentBook.getId());

        mediaPlayer = new MediaPlayer(media);

        //Play Audio on ready
        mediaPlayer.setOnReady(() -> {
            // Set slider range to the duration of the book
            double totalMs = mediaPlayer.getTotalDuration().toMillis();
            mediaPlayer.play();
            audioTimeline.setMax(totalMs);
        });
        //Listens for changes in time and updates slider and time text accordingly
        mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            if (!audioTimeline.isValueChanging()) { // Don't move slider while user is dragging it
                audioTimeline.setValue(newTime.toMillis());
                timeText.setText(getFormattedLength((long)Duration.millis(audioTimeline.getValue()).toMillis(),(long)mediaPlayer.getTotalDuration().toMillis()));
            }
        });
        //This is for when user drags slider to update time.
        audioTimeline.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) { // User let go of the slider
                mediaPlayer.seek(Duration.millis(audioTimeline.getValue()));
            }
        });

    }


    private void buildUI(Book currentBook) throws FileNotFoundException {
        //Construct url.
        String host = new EnvReader<String>().readVar("HOST");
        String port = new EnvReader<String>().readVar("PORT");
        String baseUrl = "http://" + host + ":" + port;
        //Setup UI
        NowListeningText.setText("Now Listening to "+currentBook.getName() +"\nBy " + currentBook.getAuthor());
        statusText.setText("Now Listening to "+currentBook.getName() +" By " + currentBook.getAuthor());
        DetailsBookImage.setImage(new Image(baseUrl+"/books/"+currentBook.getId()+"/cover"));
        DetailsBookImage.setOpacity(1);
        NowListeningText.setOpacity(1);

        rewindButton.setDisable(false);
        fastForwardButton.setDisable(false);
        audioTimeline.setDisable(false);
        playButton.setDisable(false);
    }

    private void selectBook(Book book){
        try {
            if(timeline != null){
                timeline.stop();
            }
            if(mediaPlayer != null){
                mediaPlayer.stop();
            }
            setupMediaPlayer(book);
            buildUI(book);
            timeline = getTimeline(book);
            timeline.play();
        } catch (FileNotFoundException e) {
            //throw new RuntimeException(e);
            new Popup("Error!","Error","An error has occurred while trying to play this book. This means that the app has failed to load the audio stream for this book. If you continue, you will not be able to listen to this book.","Ok","Contact server administration or open a git issue with the following error:\n"+e.getMessage());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try{
            Book[] books = ApiCalls.getBooks();
            List<RecentlyListened> recentlyListened = Arrays.asList(ApiCalls.getRecentlyListened());
            //Assort books by most recently listened adding rest to BookList
            if(!recentlyListened.isEmpty()){
                for(Book book:books){
                    for(RecentlyListened listened:recentlyListened){
                        if(listened.getId() == book.getId()){
                            book.setName(book.getName()+" (Last Listened: "+listened.getDate()+"  Current Position: "+listened.getTimestamp()+")");
                            RecentBooksList.getItems().add(book);
                            break;
                        }

                    }
                    if(recentlyListened.stream().noneMatch(l->l.getId() == book.getId())){
                        BookList.getItems().add(book);
                    }
                }
            }
            else {
                RecentBooksList.setDisable(true);
                BookList.getItems().addAll(ApiCalls.getBooks());
            }

            RecentBooksList.getSelectionModel().selectedItemProperty().addListener((observe, previousBook, currentBook)->{
               selectBook(currentBook);
            });

            BookList.getSelectionModel().selectedItemProperty().addListener((observe, previousBook, currentBook)->{
                selectBook(currentBook);
            });



        } catch (IOException e) {
            System.out.println("error detected when initializing MainController");
            new Popup("Error!","Error Unknown","An error has occurred at an early stage in starting the application, it's best to restart.","Ok","Contact server administration or open a git issue with the following error:\n"+e.getMessage());
            throw new RuntimeException(e);
        }
    }


    private Timeline getTimeline(Book currentBook) {
        Timeline timer = new Timeline(new KeyFrame(Duration.minutes(2), event -> {
            try {
                ApiCalls.postRecentlyListened(currentBook.getId(), (long) mediaPlayer.getCurrentTime().toMillis());
            } catch (IOException e) {
                //throw new RuntimeException(e);
                new Popup("Error!","Error","An error has occurred while updating your recently listened list. This means that the app has failed to log your current listening positon. If you continue, your positon in this book might not be stored.","Ok","Contact server administration or open a git issue with the following error:\n"+e.getMessage());
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        return timer;
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

    @FXML
    protected void onTestPopup(ActionEvent e) throws IOException {
        Popup popup = new Popup("Test popup","TEST ERROR", "This is a test error.", "Huh?");
        //popup.createPopup();

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
