package media.samson.jamplate;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 * AboutDialog shows information about the Jamplate application.
 */
public class AboutDialog extends Stage {
    
    private static final String APP_NAME = "Jamplates";
    private static final String APP_VERSION = "1.0-SNAPSHOT";
    
    /**
     * Creates a new AboutDialog.
     *
     * @param owner the owner window of this dialog
     */
    public AboutDialog(Window owner) {
        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.UTILITY);
        setTitle("About " + APP_NAME);
        setResizable(false);
        
        // Create content
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);
        
        // Application name with larger font
        Label nameLabel = new Label(APP_NAME);
        nameLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        // Version info
        Label versionLabel = new Label("Version: " + APP_VERSION);
        
        // Copyright info
        Label copyrightLabel = new Label("© 2025 Samson Media");
        
        // Close button
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> close());
        closeButton.setPrefWidth(100);
        
        content.getChildren().addAll(
                nameLabel,
                versionLabel,
                copyrightLabel,
                new Label(""), // spacer
                closeButton
        );
        
        Scene scene = new Scene(content);
        setScene(scene);
        
        // Set dialog size
        setWidth(300);
        setHeight(200);
        
        // Center on parent
        setOnShown(e -> centerOnOwner());
    }
    
    /**
     * Centers this stage on its owner.
     */
    private void centerOnOwner() {
        Window owner = getOwner();
        if (owner != null) {
            setX(owner.getX() + (owner.getWidth() - getWidth()) / 2);
            setY(owner.getY() + (owner.getHeight() - getHeight()) / 2);
        }
    }
}

