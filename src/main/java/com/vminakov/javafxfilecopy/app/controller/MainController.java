package com.vminakov.javafxfilecopy.app.controller;

import javafx.fxml.Initializable;
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
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        log.info("initializing MainController");
    }
}
