package handler;

import java.io.File;

public class FileHandler {

    public static File[] listLocalFiles(String sharedDirectory) {
        File folder = new File(sharedDirectory);
        return folder.listFiles();
    }

    public static void listAndPrintLocalFiles(String sharedDirectory) {
        File folder = new File(sharedDirectory);

        if (!folder.exists() || !folder.isDirectory() || !folder.canRead()) {
            System.out.println("Invalid directory or cannot be read: " + sharedDirectory);
            return;
        }

        File[] files = folder.listFiles();

        if (files == null || files.length == 0) {
            System.out.println("No files found in the shared directory.");
        } else {
            for (File file : files) {
                if (file.isFile()) {
                    System.out.println(file.getName());
                }
            }
        }
    }
}
