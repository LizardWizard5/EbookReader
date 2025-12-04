module ca.lizardwizard.ebookclient {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens ca.lizardwizard.ebookclient to javafx.fxml;
    exports ca.lizardwizard.ebookclient;
}