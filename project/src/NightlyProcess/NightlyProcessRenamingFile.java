package NightlyProcess;

import java.io.File;

public class NightlyProcessRenamingFile extends Thread {
    private String path;
    private String text;

    public NightlyProcessRenamingFile(String path, String text) {
        this.path = path;
        this.text = text;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void run() {
        File folder = new File(path);
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("Error: Directory not found.");
            return;
        }

        File[] files = folder.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isFile()) {
                File newFile = new File(path + File.separator + text + file.getName());
                boolean renamed = file.renameTo(newFile);
                if (renamed) {
                    System.out.println("Renamed to: " + newFile.getName());
                } else {
                    System.out.println("Error renaming file: " + file.getName());
                }
            }
        }
    }
}
