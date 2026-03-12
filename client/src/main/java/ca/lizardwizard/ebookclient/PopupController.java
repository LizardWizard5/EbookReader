package ca.lizardwizard.ebookclient;

import ca.lizardwizard.ebookclient.Lib.Popup;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class PopupController implements Initializable {

    private Popup popup;

    @FXML
    private TextArea advancedText;

    @FXML
    private Text titleText;

    @FXML
    private Text messageText;

    public void setPopup(Popup pop) {
        this.popup = pop;
        updateView();
    }

    private void updateView() {

        if (popup == null || titleText == null || messageText == null || advancedText == null) {
            
            return;
        }

        titleText.setText(popup.getHeaderText());
        messageText.setText(popup.getContentText());

        if (popup.getAdvancedDetails() != null && !popup.getAdvancedDetails().isEmpty()) {
            advancedText.setText(popup.getAdvancedDetails());
            advancedText.setVisible(true);
            advancedText.setManaged(true);
        } else {
            advancedText.clear();
            advancedText.setVisible(false);
            advancedText.setManaged(false);
        }
    }

    @FXML
    void dismissButton(ActionEvent event) {
        Stage stage = (Stage) titleText.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        updateView();
    }
}