module media.samson.jamplate {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires jakarta.xml.bind;
    requires org.fxmisc.richtext;
    // ReactFX is not properly modularized, access it from classpath instead
    
    opens media.samson.jamplate to javafx.fxml, jakarta.xml.bind, org.fxmisc.richtext, org.junit.platform.commons;
    exports media.samson.jamplate to javafx.graphics, javafx.fxml;
}
