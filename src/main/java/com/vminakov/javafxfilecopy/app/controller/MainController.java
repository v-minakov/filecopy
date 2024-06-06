package com.vminakov.javafxfilecopy.app.controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
@FxmlView("/view/MainView.fxml")
@Slf4j(topic = "MAIN_CONTROLLER")
@RequiredArgsConstructor
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
        fileCopyProgressBar.progressProperty().bind(progress);
    }
    
    /* Mouse Click handlers */
    
    private void sourceFilePathTextClicked(MouseEvent mouseEvent) {
        log.info("sourceFilePathText Clicked");
    }
    
    private void destinationFilePathTextClicked(MouseEvent mouseEvent) {
        log.info("destinationFilePathText Clicked");
    }
    
    private void copyButtonClicked(MouseEvent mouseEvent) {
        log.info("copyButton Clicked");
        if (!progressContainer.isVisible()) {
            progressContainer.setVisible(true);
        }
    }
    
    private void cancelButtonClicked(MouseEvent mouseEvent) {
        log.info("cancelButton Clicked");
        progress.set(0.0);
    }
}
