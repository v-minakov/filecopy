package com.vminakov.javafxfilecopy.app.service.impl;

import com.vminakov.javafxfilecopy.app.service.FileCopyService;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.FileChannel;

@Service("SimpleFileCopyService")
@RequiredArgsConstructor
@Slf4j(topic = "SIMPLE_FILE_COPY_SERVICE")
public class FileCopyServiceImpl implements FileCopyService {
    
    private Task<Void> copyFileTask;
    
    @Async("BackgroundOperationsThreadPool")
    @Override
    public void copyFile(
            String sourcePath,
            String destinationPath,
            SimpleDoubleProperty progress,
            SimpleBooleanProperty copyInProgress
    ) {
        copyFileTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                FileChannel sourceChannel = null;
                FileChannel destinationChannel = null;
                try {
                    sourceChannel = new FileInputStream(sourcePath).getChannel();
                    destinationChannel = new FileOutputStream(destinationPath).getChannel();
                    
                    long size = sourceChannel.size();
                    long position = 0;
                    long transferred;
                    while (position < size) {
                        if (isCancelled()) {
                            break;
                        }
                        transferred = sourceChannel.transferTo(position, 1024 * 1024, destinationChannel);
                        position += transferred;
                        progress.set((double) position / size);
                    }
                    
                } catch (ClosedByInterruptException e) {
                    log.error("File copy process was interrupted with message: {}", e.getMessage());
                } finally {
                    if (sourceChannel != null) {
                        try {
                            sourceChannel.close();
                        } catch (IOException e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                    if (destinationChannel != null) {
                        try {
                            destinationChannel.close();
                        } catch (IOException e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                }
                
                return null;
            }
        };
        copyFileTask.setOnSucceeded(event -> {
            log.info("Event log: File copy completed successfully");
            copyInProgress.set(false);
        });
        copyFileTask.setOnFailed(event -> {
            log.error("Event log: File copy failed");
            copyInProgress.set(false);
        });
        copyFileTask.setOnCancelled(event -> {
            log.info("Event log: File copy cancelled");
            copyInProgress.set(false);
            progress.set(0.0);
        });
        
        copyInProgress.set(true);
        copyFileTask.run();
    }
    
    @Async("BackgroundOperationsThreadPool")
    @Override
    public void deleteFile(String filePath) {
        log.info("Deleting file: {}", filePath);
        new File(filePath).delete();
    }
    
    @Async("BackgroundOperationsThreadPool")
    @Override
    public void cancelCopy() {
        log.info("Cancelling copy");
        copyFileTask.cancel();
    }
}
