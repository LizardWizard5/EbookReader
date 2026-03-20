package ca.lizardwizard.ebookclient;

import ca.lizardwizard.ebookclient.Lib.EnvReader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class    HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        //First check if env.txt exisits
        boolean exists = new EnvReader<String>().checkIfFileExists();
        String resource = "home-view.fxml";
        if (!exists) {
            //If env file does not exist, switch resource to scene for user to setup

        }
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("home-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1100, 800);//x,y
        stage.setTitle("EbookReader client");
        stage.setScene(scene);
        stage.show();
    }
}
