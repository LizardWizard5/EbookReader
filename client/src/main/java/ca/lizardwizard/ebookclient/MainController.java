package ca.lizardwizard.ebookclient;

import ca.lizardwizard.ebookclient.objects.Book;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

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
    private Label NowListeningText;

    @FXML
    private Font x1;

    @FXML
    private Color x2;

    @FXML
    private Font x3;

    @FXML
    private Color x4;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try{
            BookList.getItems().addAll(ApiCalls.getBooks());
            BookList.getSelectionModel().selectedItemProperty().addListener((observe, previousBook, currentBook)->{
                //Sets the vbox to visible
                //Sets name role label text
                NowListeningText.setText("Now Listening to "+currentBook.getName() +", by " + currentBook.getAuthor());
                //Sets the image as the heroes' portrait.
                //heroImage.setImage(new Image(currentHero.getPortrait()));

            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
