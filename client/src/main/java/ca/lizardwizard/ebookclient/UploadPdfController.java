package ca.lizardwizard.ebookclient;

import ca.lizardwizard.ebookclient.Lib.ApiCalls;
import ca.lizardwizard.ebookclient.Lib.Popup;
import ca.lizardwizard.ebookclient.Lib.SceneUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UploadPdfController implements Initializable {
    private FileChooser pdfChooser = new FileChooser();
    private FileChooser jpegChooser = new FileChooser();
    private File selectedPdf;
    private File selectedJpeg;
    @FXML
    private TextField authorBox;

    @FXML
    private TextArea detailBox;

    @FXML
    private TextField titleBox;

    @FXML
    void uploadCoverButton(ActionEvent event) {

        Node sourceNode = (Node) event.getSource();
        Stage stage = (Stage) sourceNode.getScene().getWindow();
        selectedJpeg = jpegChooser.showOpenDialog(stage);
    }
    @FXML
    void returnButton(ActionEvent event) {
        SceneUtil.switchScenes(event,"home-view");
    }

    @FXML
    void submitButton(ActionEvent event) throws IOException {
        String title = titleBox.getText();
        String author = authorBox.getText();
        String details = detailBox.getText();

        if(selectedPdf == null) {
            new Popup("Error","Book Upload Failed","Please Select a Book To Upload","Ok");
            return;
        }

        boolean res = ApiCalls.postBook(title, author, details, selectedJpeg, selectedPdf);

        if(res){
            new Popup("Success!","Book upload complete", "Please allow a few hours depending on book length for the server to fully process the audio conversion"," I Understand");
        }
    }

    @FXML
    void uploadButton(ActionEvent event) {

        Node sourceNode = (Node) event.getSource();
        Stage stage = (Stage) sourceNode.getScene().getWindow();
        selectedPdf = pdfChooser.showOpenDialog(stage);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        pdfChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );
        jpegChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG Files", "*.jpg", "*.jpeg")
        );

    }
}
