package media.samson.jamplate;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.application.Platform;
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
    
    @FXML private MenuItem menuNew;
    @FXML private MenuItem menuOpen;
    @FXML private MenuItem menuSave;
    @FXML private MenuItem menuCut;
    @FXML private MenuItem menuCopy;
    @FXML private MenuItem menuPaste;
    @FXML private MenuItem menuUndo;
    @FXML private MenuItem menuRedo;
    @FXML private MenuItem menuAbout;
    @FXML private CheckMenuItem menuShowToolbar;
    @FXML private CheckMenuItem menuShowStatusBar;
    @FXML private ToolBar toolbar;
    @FXML private HBox statusBar;

    @FXML
    public void initialize() {
        GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
        
        // Create glyphs for toolbar buttons (using default color)
        Glyph fileGlyph = fontAwesome.create(FontAwesome.Glyph.FILE).size(16);
        Glyph folderOpenGlyph = fontAwesome.create(FontAwesome.Glyph.FOLDER_OPEN).size(16);
        Glyph saveGlyph = fontAwesome.create(FontAwesome.Glyph.SAVE).size(16);
        Glyph cutGlyph = fontAwesome.create(FontAwesome.Glyph.CUT).size(16);
        Glyph copyGlyph = fontAwesome.create(FontAwesome.Glyph.COPY).size(16);
        Glyph pasteGlyph = fontAwesome.create(FontAwesome.Glyph.PASTE).size(16);
        
        // Create separate glyphs for menu items with darker color
        Glyph menuFileGlyph = fontAwesome.create(FontAwesome.Glyph.FILE).size(16).color(Color.GRAY);
        Glyph menuFolderOpenGlyph = fontAwesome.create(FontAwesome.Glyph.FOLDER_OPEN).size(16).color(Color.GRAY);
        Glyph menuSaveGlyph = fontAwesome.create(FontAwesome.Glyph.SAVE).size(16).color(Color.GRAY);
        Glyph menuCutGlyph = fontAwesome.create(FontAwesome.Glyph.CUT).size(16).color(Color.GRAY);
        Glyph menuCopyGlyph = fontAwesome.create(FontAwesome.Glyph.COPY).size(16).color(Color.GRAY);
        Glyph menuPasteGlyph = fontAwesome.create(FontAwesome.Glyph.PASTE).size(16).color(Color.GRAY);
        
        // Set icons on toolbar buttons
        btnNew.setGraphic(fileGlyph);
        btnOpen.setGraphic(folderOpenGlyph);
        btnSave.setGraphic(saveGlyph);
        btnCut.setGraphic(cutGlyph);
        btnCopy.setGraphic(copyGlyph);
        btnPaste.setGraphic(pasteGlyph);
        
        // Set menu item icons with light-colored glyphs for better visibility in dark themes
        menuNew.setGraphic(menuFileGlyph);
        menuOpen.setGraphic(menuFolderOpenGlyph);
        menuSave.setGraphic(menuSaveGlyph);
        menuCut.setGraphic(menuCutGlyph);
        menuCopy.setGraphic(menuCopyGlyph);
        menuPaste.setGraphic(menuPasteGlyph);

        // Set tooltips with platform-specific keyboard shortcuts
        String ctrlKey = getPlatformSpecificCtrlKey();
        
        btnNew.setTooltip(new Tooltip("New File (" + ctrlKey + "+N)"));
        btnOpen.setTooltip(new Tooltip("Open File (" + ctrlKey + "+O)"));
        btnSave.setTooltip(new Tooltip("Save File (" + ctrlKey + "+S)"));
        btnCut.setTooltip(new Tooltip("Cut (" + ctrlKey + "+X)"));
        btnCopy.setTooltip(new Tooltip("Copy (" + ctrlKey + "+C)"));
        btnPaste.setTooltip(new Tooltip("Paste (" + ctrlKey + "+V)"));
        
        // Set platform-specific Redo shortcut (no tooltip needed for menu items)
        if (isMac()) {
            // On Mac, Redo is typically Cmd+Shift+Z
            menuRedo.setAccelerator(javafx.scene.input.KeyCombination.valueOf("shortcut+shift+Z"));
        } else {
            // On Windows/Linux, Redo is typically Ctrl+Y
            menuRedo.setAccelerator(javafx.scene.input.KeyCombination.valueOf("shortcut+Y"));
        }
        
        // Ensure initial toolbar visibility matches the menuShowToolbar state
        toolbar.setVisible(menuShowToolbar.isSelected());
        toolbar.setManaged(menuShowToolbar.isSelected());
        
        // Ensure initial status bar visibility matches the menuShowStatusBar state
        statusBar.setVisible(menuShowStatusBar.isSelected());
        statusBar.setManaged(menuShowStatusBar.isSelected());
    }
    
    @FXML
    private void handleUndo() {
        System.out.println("Undo action triggered");
        // Implement actual undo logic here
    }
    
    @FXML
    private void handleRedo() {
        System.out.println("Redo action triggered");
        // Implement actual redo logic here
    }
    
    @FXML
    private void handleAbout() {
        AboutDialog aboutDialog = new AboutDialog(menuAbout.getParentPopup().getOwnerWindow());
        aboutDialog.showAndWait();
    }

    @FXML
    private void handleToggleToolbar() {
        // Set toolbar visibility based on the CheckMenuItem's selected state
        toolbar.setVisible(menuShowToolbar.isSelected());
        toolbar.setManaged(menuShowToolbar.isSelected()); // Ensures layout adjusts when toolbar is hidden
    }
    
    @FXML
    private void handleToggleStatusBar() {
        // Set status bar visibility based on the CheckMenuItem's selected state
        statusBar.setVisible(menuShowStatusBar.isSelected());
        statusBar.setManaged(menuShowStatusBar.isSelected()); // Ensures layout adjusts when status bar is hidden
    }
    
    /**
     * Returns the platform-specific control key symbol
     */
    private String getPlatformSpecificCtrlKey() {
        return isMac() ? "⌘" : "Ctrl";
    }
    
    /**
     * Checks if the current platform is macOS
     */
    private boolean isMac() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("mac");
    }
}
