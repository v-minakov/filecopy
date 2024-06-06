package com.vminakov.javafxfilecopy.app;

import com.vminakov.javafxfilecopy.JavafxtestApplication;
import com.vminakov.javafxfilecopy.app.event.StageReadyEvent;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class FxApplication extends Application {
    
    private ConfigurableApplicationContext applicationContext;
    
    @Override
    public void init() throws Exception {
        this.applicationContext = new SpringApplicationBuilder()
                .sources(JavafxtestApplication.class)
                .run(getParameters().getRaw().toArray(new String[0]));
    }
    
    @Override
    public void stop() throws Exception {
        this.applicationContext.close();
        Platform.exit();
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        this.applicationContext.publishEvent(new StageReadyEvent(stage));
    }
}
