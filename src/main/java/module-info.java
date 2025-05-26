module media.samson.jamplate {
    // JavaFX dependencies
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    
    // Rich text editor dependencies
    requires org.fxmisc.richtext;
    requires static org.fxmisc.flowless;
    
    // XML and CSV handling
    requires jakarta.xml.bind;
    requires org.apache.commons.csv;
    
    // Export the main package
    exports media.samson.jamplate;
    
    // Open the package for reflection access
    opens media.samson.jamplate to 
        javafx.fxml,           // For FXML loading
        jakarta.xml.bind,      // For XML serialization
        org.fxmisc.richtext;  // For rich text editor
}
