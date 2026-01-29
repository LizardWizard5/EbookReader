package ca.lizardwizard.ebookclient.Lib;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SceneUtil {

    public static void switchScenes(ActionEvent e, String sceneName) {
        String path = "/ca/lizardwizard/ebookclient/" + sceneName + ".fxml";
        var url = SceneUtil.class.getResource(path);

        if (url == null) {
            throw new IllegalStateException("FXML not found: " + path);
        }

        try {
            Parent newRoot = FXMLLoader.load(url);
            Node sourceNode = (Node) e.getSource();
            Stage stage = (Stage) sourceNode.getScene().getWindow();
            stage.getScene().setRoot(newRoot);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load FXML: " + path, ex);
        }
    }
}
