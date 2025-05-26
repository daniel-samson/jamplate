module media.samson.jamplate {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens media.samson.jamplate to javafx.fxml;
    exports media.samson.jamplate;
}