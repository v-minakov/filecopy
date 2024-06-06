package com.vminakov.javafxfilecopy;

import com.vminakov.javafxfilecopy.app.FxApplication;
import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JavafxtestApplication {
    
    public static void main(String[] args) {
        Application.launch(FxApplication.class, args);
    }
}
