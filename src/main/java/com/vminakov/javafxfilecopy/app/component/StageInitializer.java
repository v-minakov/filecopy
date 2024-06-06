package com.vminakov.javafxfilecopy.app.component;

import com.vminakov.javafxfilecopy.app.controller.MainController;
import com.vminakov.javafxfilecopy.app.event.StageReadyEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StageInitializer implements ApplicationListener<StageReadyEvent> {
    
    @Value("${app.window.caption}")
    private String captionText;
    
    private final FxWeaver fxWeaver;
    
    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        Stage stage = event.stage;
        stage.setTitle(captionText);
        Parent parent = fxWeaver.loadView(MainController.class);
        Scene scene = new Scene(parent, 400, 300);
        stage.setScene(scene);
        stage.show();
    }
}
