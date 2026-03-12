package ca.lizardwizard.ebookclient.Lib;

import ca.lizardwizard.ebookclient.PopupController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Popup {

    private String windowTitle;
    private String headerText;
    private String contentText;
    private String dismissText = "OK";
    private String advancedDetails = "";


    public Popup(String windowTitle, String headerText, String contentText){
        this.windowTitle = windowTitle;
        this.headerText = headerText;
        this.contentText = contentText;
        this.createPopup();
    }
    public Popup(String windowTitle, String headerText, String contentText, String dismissText){
        this.windowTitle = windowTitle;
        this.headerText = headerText;
        this.contentText = contentText;
        this.dismissText = dismissText;
        this.createPopup();
    }
    public Popup(String windowTitle, String headerText, String contentText, String dismissText, String advancedDetails){
        this.windowTitle = windowTitle;
        this.headerText = headerText;
        this.contentText = contentText;
        this.dismissText = dismissText;
        this.advancedDetails = advancedDetails;
        this.createPopup();
    }

    public void createPopup() {
        try {

            URL url = getClass().getResource("/ca/lizardwizard/ebookclient/popup-view.fxml");
            System.out.println("FXML URL: " + url);

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            PopupController controller = loader.getController();
            controller.setPopup(this);
            Stage popupStage = new Stage();
            popupStage.setScene(new Scene(root, 400, 300));
            popupStage.setTitle("Popup");
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getWindowTitle() {
        return windowTitle;
    }

    public void setWindowTitle(String windowTitle) {
        this.windowTitle = windowTitle;
    }

    public String getHeaderText() {
        return headerText;
    }

    public void setHeaderText(String headerText) {
        this.headerText = headerText;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public String getDismissText() {
        return dismissText;
    }

    public void setDismissText(String dismissText) {
        this.dismissText = dismissText;
    }

    public String getAdvancedDetails() {
        return advancedDetails;
    }

    public void setAdvancedDetails(String advancedDetails) {
        this.advancedDetails = advancedDetails;
    }

    @Override
    public String toString() {
        return "WindowTitle: " + windowTitle + ", HeaderText: " + headerText + ", ContentText: " + contentText;
    }
}
