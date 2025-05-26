module media.samson.jamplate {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires jakarta.xml.bind;
    requires org.fxmisc.richtext;
    
    opens media.samson.jamplate to javafx.fxml, jakarta.xml.bind, org.fxmisc.richtext;
    exports media.samson.jamplate;
}
