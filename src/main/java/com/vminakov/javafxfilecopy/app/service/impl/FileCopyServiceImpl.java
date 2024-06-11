package com.vminakov.javafxfilecopy.app.service.impl;

import com.vminakov.javafxfilecopy.app.service.FileCopyService;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.FileChannel;

/**
 * This class implements the FileCopyService interface and provides methods to copy and delete files.
 * It uses the SimpleFileCopyService bean as the service identifier.
 * The class uses asynchronous execution using the BackgroundOperationsThreadPool.
 */
@Service("SimpleFileCopyService")
@RequiredArgsConstructor
@Slf4j(topic = "SIMPLE_FILE_COPY_SERVICE")
public class FileCopyServiceImpl implements FileCopyService {
    
    private Task<Void> copyFileTask;
    
    /**
     * Asynchronously copies the file from a source path to a destination path, providing progress update and status of the operation.
     * <p>
     * This method runs in a separate thread, managed by "BackgroundOperationsThreadPool" so as not to block the user interface or other operations.
     *
     * @param sourcePath      The path of the source file to be copied. It should be a Full file path. The type of this parameter is String.
     * @param destinationPath The destination where the file should be copied to. It should be a full file path. The type of this parameter is String.
     * @param progress        A `SimpleDoubleProperty` object that's bound to the progress of the copy operation. It represents the progress of
     *                        the operation and updates as the file gets copied. The value is a `Double` ranging from
     *                        0.0 (representing no progress) to 1.0 (representing 100% progress).
     * @param copyInProgress  A `SimpleBooleanProperty` object indicating the copy operation's status.
     *                       It set to `true` when copying starts and `false` once copying has completed (regardless of success or failure).
     *                       The type of this parameter is SimpleBooleanProperty.
     * <p>
     * Note: The method does not return any values directly, return type is `void`.
     *       All notifications about the completion of the operation (be it success, failure or cancellation)
     *       are communicated asynchronously through the listeners.
     * <p>
     * Execution Context:
     *       The core business logic of the method (copying the file) is encapsulated in a `Task` that's run on the "BackgroundOperationsThreadPool" thread.
     *       The `Task` reads the source file and writes it to the destination path, reporting progress as it goes.
     *       On completion or failure, it updates the `copyInProgress` property appropriately.
     *       If cancelled, it updates both `copyInProgress` to `false` and `progress` to `0.0`.
     */
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
            log.info("Event log: File copy process completed.");
            copyInProgress.set(false);
            Alert alert;
            File file = new File(destinationPath);
            if (file.exists() && file.isFile()) {
                log.info("Event log: File exists at destination path.");
                alert = new Alert(Alert.AlertType.INFORMATION, "File copied successfully");
                alert.showAndWait();
            } else {
                log.info("Event log: File does not exist at destination path");
                alert = new Alert(Alert.AlertType.ERROR, "File copy failed");
                alert.showAndWait();
            }
        });
        copyFileTask.setOnFailed(event -> {
            log.error("Event log: File copy failed");
            copyInProgress.set(false);
            Alert alert = new Alert(Alert.AlertType.ERROR, "File copy failed");
            alert.showAndWait();
        });
        copyFileTask.setOnCancelled(event -> {
            log.info("Event log: File copy cancelled");
            copyInProgress.set(false);
            progress.set(0.0);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "File copy cancelled");
            alert.showAndWait();
        });
        
        copyInProgress.set(true);
        copyFileTask.run();
    }
    
    /**
     * This method is used to asynchronously delete a file at a specified path.
     *
     * @param filePath A string that represents the path of the file to be deleted. It could be either absolute or relative.
     * <p>
     * The method does not return a value since it is marked with @Override annotation, implying that it's an implementation
     * of an interface method, and therefore respects the return type defined at the contract level.
     * <p>
     * Execution Context:
     * This method is annotated with @Async("BackgroundOperationsThreadPool") which means the execution happens on a
     * separate thread managed by thread pool named "BackgroundOperationsThreadPool".
     * This allows for non-blocking and concurrent execution of file deletion tasks.
     * <p>
     * Business Logic:
     *     1. Checks if the file exists and if it's not a directory.
     *     2. Creates a JavaFX Task which handles the deletion process.
     *     3. Define behaviour for successful and failed deletion using the 'setOnSucceeded' and 'setOnFailed' methods on the Task.
     *         - In case of successful deletion, it logs the success, and displays an Alert to the user stating the file deletion was successful.
     *         - In case of failure, it logs the error, and displays an Alert with a corresponding error message.
     *     4. The Task is started using the run method.
     */
    @Async("BackgroundOperationsThreadPool")
    @Override
    public void deleteFile(String filePath) {
        log.info("Deleting file: {}", filePath);
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            Task<Boolean> fileDeleteTask = new Task<>() {
                @Override
                protected Boolean call() throws Exception {
                    return file.delete();
                }
            };
            fileDeleteTask.setOnSucceeded(event -> {
                File deletedFile = new File(filePath);
                Alert alert;
                if (!deletedFile.exists()) {
                    log.info("File {} deleted.", filePath);
                    alert = new Alert(Alert.AlertType.INFORMATION, "File deleted successfully");
                    alert.setHeaderText("Information");
                } else {
                    log.error("File {} could not be deleted.", filePath);
                    alert = new Alert(Alert.AlertType.ERROR, "File deletion failed");
                }
                alert.showAndWait();
            });
            fileDeleteTask.setOnFailed(event -> {
                log.error("File {} deletion process failed", filePath);
                Alert alert = new Alert(Alert.AlertType.ERROR, "File deletion failed");
                alert.showAndWait();
            });
            
            fileDeleteTask.run();
        } else {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR, "File could not be deleted.");
                alert.showAndWait();
            });
        }
    }
    
    /**
     * Cancels an ongoing file copy task if one exists.
     * <p>
     * This method performs no actions and returns no value if there is no ongoing task.
     * <p>
     * It should be used in proper threading context due to its interaction with an asynchronous task.
     * Current implementation assumes that this method's logic will work in the caller's context.
     */
    @Override
    public void cancelCopy() {
        log.info("Cancelling copy");
        if (copyFileTask != null && copyFileTask.isRunning()) {
            copyFileTask.cancel();
        }
    }
}
