package actions;

import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;

public class FileAction {

    private String fileName;
    private String eventPatternName;
    private String alert;

    public FileAction(String fileName, String eventPatternName, String alert) {
        System.out.println("Initializing File Action");
        this.fileName = fileName;
        this.eventPatternName = eventPatternName;
        this.alert = alert;
    }

    public void saveAlert() {
        System.out.println("Saving file...");
        String message = new Date() + " | Alert '" + eventPatternName + "' detected | Properties: " + alert + '\n';
        try {
            File file = new File(fileName);
            file.createNewFile();
            Files.write(
                    Paths.get(fileName),
                    message.getBytes(),
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
