package media.samson.jamplate;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

/**
 * A dialog that shows progress of a task with a progress bar,
 * status message, and cancel button.
 */
public class ProgressDialog extends Dialog<Void> {
    private final Label messageLabel;
    private final ProgressBar progressBar;
    private Task<?> task;
    private double maxProgress = 100.0;
    private double currentProgress = 0.0;

    /**
     * Creates a new progress dialog.
     *
     * @param owner The owner window of this dialog
     */
    public ProgressDialog(Window owner) {
        // Basic dialog setup
        setTitle("Operation in Progress");
        initOwner(owner);
        
        // Create status components
        Label statusLabel = new Label("Processing...");
        statusLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        // Create message label
        messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(Double.MAX_VALUE);
        messageLabel.setStyle("-fx-font-size: 12px;");
        
        progressBar = new ProgressBar();
        progressBar.setMaxWidth(Double.MAX_VALUE);
        
        // Create layout
        VBox content = new VBox(10);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(20));
        content.setPrefWidth(400);
        content.getChildren().addAll(statusLabel, messageLabel, progressBar);
        
        // Add cancel button
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().add(cancelButton);
        
        // Set dialog content
        getDialogPane().setContent(content);
        
        // Handle dialog close request
        setOnCloseRequest(event -> {
            if (task != null && task.isRunning()) {
                event.consume();
                
                // Ask for confirmation
                Alert alert = new Alert(
                    Alert.AlertType.CONFIRMATION,
                    "Are you sure you want to cancel the current operation?",
                    ButtonType.YES, ButtonType.NO
                );
                alert.setTitle("Confirm Cancel");
                alert.setHeaderText("Cancel Operation");
                alert.initOwner(this.getDialogPane().getScene().getWindow());
                
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        task.cancel();
                    }
                });
            }
        });
    }

    /**
     * Sets the task to monitor.
     *
     * @param task The task to monitor
     */
    public void setTask(Task<?> task) {
        this.task = task;
        
        // Unbind any existing bindings
        if (progressBar.progressProperty().isBound()) {
            progressBar.progressProperty().unbind();
        }
        if (messageLabel.textProperty().isBound()) {
            messageLabel.textProperty().unbind();
        }
        
        // Bind progress properties
        progressBar.progressProperty().bind(task.progressProperty());
        messageLabel.textProperty().bind(task.messageProperty());
        
        // Handle task state changes
        task.stateProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case SUCCEEDED:
                    Platform.runLater(this::close);
                    break;
                case FAILED:
                    showError(task.getException());
                    getDialogPane().getButtonTypes().setAll(ButtonType.CLOSE);
                    break;
                case CANCELLED:
                    Platform.runLater(this::close);
                    break;
                default:
                    break;
            }
        });
    }

    /**
     * Shows an error message when the task fails.
     *
     * @param error The error to display
     */
    private void showError(Throwable error) {
        String message = error != null ? error.getMessage() : "Operation failed";
        messageLabel.textProperty().unbind();
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
    }

    /**
     * Sets the maximum progress value. This method should be called before starting
     * the task if you want to manually update progress (not using task binding).
     * 
     * @param max The maximum progress value (must be > 0)
     */
    public void setMax(double max) {
        if (max <= 0) {
            throw new IllegalArgumentException("Maximum progress value must be greater than 0");
        }
        this.maxProgress = max;
        updateProgressBar();
    }

    /**
     * Sets the current progress value. This automatically updates the progress bar
     * if it's not bound to a task.
     * 
     * @param current The current progress value
     */
    public void setProgress(double current) {
        this.currentProgress = current;
        updateProgressBar();
    }

    /**
     * Updates the progress bar based on current and max values.
     * Only updates if the progress bar is not bound to a task.
     */
    private void updateProgressBar() {
        if (!progressBar.progressProperty().isBound()) {
            double progress = Math.min(1.0, Math.max(0.0, currentProgress / maxProgress));
            Platform.runLater(() -> progressBar.setProgress(progress));
        }
    }

    /**
     * Updates both the current and maximum progress values.
     * This is a convenience method that combines setMax and setProgress.
     *
     * @param current The current progress value
     * @param max The maximum progress value
     */
    public void updateProgress(double current, double max) {
        if (max > 0) {
            this.maxProgress = max;
            this.currentProgress = current;
            updateProgressBar();
        }
    }

    /**
     * Sets the status text displayed at the top of the dialog.
     *
     * @param status The status text to display
     */
    public void setStatus(String status) {
        Platform.runLater(() -> {
            Label statusLabel = (Label) ((VBox) getDialogPane().getContent()).getChildren().get(0);
            statusLabel.setText(status);
        });
    }

    /**
     * Sets the process message displayed in the dialog.
     * This message appears below the status text and above the progress bar.
     *
     * @param message The message to display
     */
    public void setProcessMessage(String message) {
        Platform.runLater(() -> {
            if (messageLabel.textProperty().isBound()) {
                messageLabel.textProperty().unbind();
            }
            messageLabel.setText(message);
        });
    }

    /**
     * Updates the display with new status and message.
     * This is a convenience method that updates both the status and process message.
     *
     * @param status The status text to display at the top
     * @param message The process message to display
     */
    public void updateDisplay(String status, String message) {
        Platform.runLater(() -> {
            setStatus(status);
            setProcessMessage(message);
        });
    }
}
