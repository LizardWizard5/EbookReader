package ca.lizardwizard.ebookclient;
import ca.lizardwizard.ebookclient.Lib.Popup;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

public class PopupController extends Parent {

    @FXML
    private TextArea advancedText;

    @FXML
    private Text headerText;

    @FXML
    private Text messageText;

    public PopupController(Popup popup) {
    }

    @FXML
    void dismissButton(ActionEvent event) {

    }

    @Override
    public Node getStyleableNode() {
        return super.getStyleableNode();
    }
}
