package media.samson.jamplate;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import org.controlsfx.control.action.Action;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

public class HelloController {
    @FXML private Button btnNew;
    @FXML private Button btnOpen;
    @FXML private Button btnSave;
    @FXML private Button btnCut;
    @FXML private Button btnCopy;
    @FXML private Button btnPaste;

    @FXML
    public void initialize() {
        GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
        
        btnNew.setGraphic(fontAwesome.create(FontAwesome.Glyph.FILE).size(16));
        btnOpen.setGraphic(fontAwesome.create(FontAwesome.Glyph.FOLDER_OPEN).size(16));
        btnSave.setGraphic(fontAwesome.create(FontAwesome.Glyph.SAVE).size(16));
        btnCut.setGraphic(fontAwesome.create(FontAwesome.Glyph.CUT).size(16));
        btnCopy.setGraphic(fontAwesome.create(FontAwesome.Glyph.COPY).size(16));
        btnPaste.setGraphic(fontAwesome.create(FontAwesome.Glyph.PASTE).size(16));

        // Optional: Set tooltips
        btnNew.setTooltip(new Tooltip("New File"));
        btnOpen.setTooltip(new Tooltip("Open File"));
        btnSave.setTooltip(new Tooltip("Save File"));
    }
}