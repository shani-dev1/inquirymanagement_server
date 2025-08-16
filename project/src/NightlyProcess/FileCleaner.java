package NightlyProcess;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class FileCleaner extends Thread {

    private String folderName;
    private Integer days;

    public FileCleaner(String folderName, Integer days) {
        this.folderName = folderName;
        this.days = days;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    @Override
    public void run() {
        File folder = new File(folderName);
        if (!folder.isDirectory()) {
            return;
        }

        Instant expirationDate = Instant.now().minus(days, ChronoUnit.DAYS);
        File[] files = folder.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            try {
                Instant creationTime = Files.readAttributes(file.toPath(), BasicFileAttributes.class).creationTime().toInstant();
                if (creationTime.isBefore(expirationDate) && file.delete()) {
                    System.out.println("Deleted: " + file.getName());
                }
            } catch (Exception e) {
                System.out.println("Error processing file: " + file.getName());
                e.printStackTrace();
            }
        }
    }
}
