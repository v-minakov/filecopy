package com.vminakov.javafxfilecopy.app.controller;

import com.vminakov.javafxfilecopy.app.service.FileCopyService;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

@Component
@FxmlView("/view/MainView.fxml")
@Slf4j(topic = "MAIN_CONTROLLER")
public class MainController implements Initializable {
    
    @FXML
    private TextField sourceFilePathText;
    @FXML
    private TextField destinationFilePathText;
    @FXML
    private ProgressBar fileCopyProgressBar;
    @FXML
    private Button copyButton;
    @FXML
    private Button cancelButton;
    @FXML
    private HBox progressContainer;
    
    private SimpleDoubleProperty progress;
    private SimpleBooleanProperty copyInProgress;
    
    private final FileCopyService fileCopyService;
    
    public MainController(
            @Qualifier("SimpleFileCopyService")
            FileCopyService fileCopyService
    ) {
        this.fileCopyService = fileCopyService;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        log.info("initializing MainController");
        // Binding click events
        sourceFilePathText.setOnMouseClicked(this::sourceFilePathTextClicked);
        destinationFilePathText.setOnMouseClicked(this::destinationFilePathTextClicked);
        copyButton.setOnMouseClicked(this::copyButtonClicked);
        cancelButton.setOnMouseClicked(this::cancelButtonClicked);
        // Binding property
        progress = new SimpleDoubleProperty(0.0);
        copyInProgress = new SimpleBooleanProperty(false);
        fileCopyProgressBar.progressProperty().bind(progress);
        copyButton.disableProperty().bind(copyInProgress);
    }
    
    /* Mouse Click handlers */
    
    private void sourceFilePathTextClicked(MouseEvent mouseEvent) {
        log.info("sourceFilePathText Clicked");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("All files", "*/*"));
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            log.info("selected source file: {}", selectedFile.getAbsolutePath());
            sourceFilePathText.textProperty().set(selectedFile.getAbsolutePath());
        } else {
            log.info("no selected source file");
        }
    }
    
    private void destinationFilePathTextClicked(MouseEvent mouseEvent) {
        log.info("destinationFilePathText Clicked");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("All files", "*/*"));
        File selectedFile = fileChooser.showSaveDialog(null);
        if (selectedFile != null) {
            log.info("selected destination file: {}", selectedFile.getAbsolutePath());
            destinationFilePathText.textProperty().set(selectedFile.getAbsolutePath());
        } else {
            log.info("no selected destination file");
        }
    }
    
    private void copyButtonClicked(MouseEvent mouseEvent) {
        log.info("copyButton Clicked");
        if (!progressContainer.isVisible()) {
            progressContainer.setVisible(true);
        }
        fileCopyService.copyFile(sourceFilePathText.getText(), destinationFilePathText.getText(), progress, copyInProgress);
    }
    
    private void cancelButtonClicked(MouseEvent mouseEvent) {
        log.info("cancelButton Clicked");
        fileCopyService.cancelCopy();
        fileCopyService.deleteFile(destinationFilePathText.getText());
    }
}
