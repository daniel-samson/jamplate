module media.samson.jamplate {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires jakarta.xml.bind;
    
    opens media.samson.jamplate to javafx.fxml, jakarta.xml.bind;
    exports media.samson.jamplate;
}
