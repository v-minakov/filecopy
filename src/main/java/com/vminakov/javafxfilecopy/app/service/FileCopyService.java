package com.vminakov.javafxfilecopy.app.service;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;

public interface FileCopyService {
    void copyFile(String sourcePath, String destinationPath, SimpleDoubleProperty progress, SimpleBooleanProperty copyInProgress);
    void deleteFile(String filePath);
    void cancelCopy();
}
