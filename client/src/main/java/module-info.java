module ca.lizardwizard.ebookclient {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.net.http;
    requires java.compiler;
    requires com.google.gson;
    requires java.desktop;

    requires javafx.media;
    requires javafx.graphics;
    requires org.apache.httpcomponents.client5.httpclient5;
    requires org.apache.httpcomponents.core5.httpcore5;


    opens ca.lizardwizard.ebookclient to javafx.fxml;
    exports ca.lizardwizard.ebookclient;
    exports ca.lizardwizard.ebookclient.Lib;
    opens ca.lizardwizard.ebookclient.Lib to javafx.fxml;
    opens ca.lizardwizard.ebookclient.objects to com.google.gson;
}