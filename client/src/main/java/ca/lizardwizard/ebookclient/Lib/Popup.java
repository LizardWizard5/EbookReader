package ca.lizardwizard.ebookclient.Lib;

import ca.lizardwizard.ebookclient.PopupController;
import javafx.scene.Scene;

public class Popup {

    private String windowTitle;
    private String headerText;
    private String contentText;
    private String dismissText = "OK";
    private String advancedDetails = "";

    Popup(String windowTitle, String headerText, String contentText){
        this.windowTitle = windowTitle;
        this.headerText = headerText;
        this.contentText = contentText;
    }
    Popup(String windowTitle, String headerText, String contentText, String dismissText){
        this.windowTitle = windowTitle;
        this.headerText = headerText;
        this.contentText = contentText;
        this.dismissText = dismissText;
    }
    Popup(String windowTitle, String headerText, String contentText, String dismissText, String advancedDetails){
        this.windowTitle = windowTitle;
        this.headerText = headerText;
        this.contentText = contentText;
        this.dismissText = dismissText;
        this.advancedDetails = advancedDetails;
    }

    void createPopup(){
        PopupController popupController = new PopupController(this);
        Scene popupScene = new Scene(popupController);

    }


}
