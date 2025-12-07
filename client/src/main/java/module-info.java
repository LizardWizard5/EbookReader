module ca.lizardwizard.ebookclient {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.net.http;
    requires java.compiler;
    requires com.google.gson;
    requires java.desktop;


    opens ca.lizardwizard.ebookclient to javafx.fxml;
    exports ca.lizardwizard.ebookclient;
}