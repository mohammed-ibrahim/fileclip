//package com.javapapers.java;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.io.File;
import java.awt.datatransfer.*;
import java.awt.Toolkit;
import java.nio.file.Files;


public class FileWatcher {

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length != 1) {
            System.err.println("usage: java <program> <file_to_watch>");
            System.err.println("Aborting");
            return;
        } 

        String fileName = args[0];
        File file = new File(fileName);
        if (!file.exists()) {
            System.err.println("Given input is not a file: " + fileName);
            System.err.println("Aborting");
            return;
        }

        String fileToWatch = file.getName();
        String directoryToWatch = getParentDirectory(file.getPath());
        System.out.println("Watching the directory: " + directoryToWatch + " for the filename: " + fileToWatch);

        Path targetFolder = Paths.get(directoryToWatch);
        WatchService watchService = FileSystems.getDefault().newWatchService();
        final WatchKey watchKey = targetFolder.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
        boolean valid = true;

        while(valid) {
            final WatchKey entryKey = watchService.take();

            for (WatchEvent<?> event: entryKey.pollEvents()) {
                final Path changed = (Path) event.context();
                String changedFileName = changed.toString();
                
                if (fileToWatch.equals(changedFileName)) {
                    System.out.println("Now the required file is changed: " + fileToWatch);
                    copyToClipboard(file.getPath());
                }
                
            }

            valid = entryKey.reset();
            if (!valid) {
                System.err.println("Key has been unregistered");
            }
        }
    }

    public static void copyToClipboard(String filePath) {
        try {
            String str = new String(Files.readAllBytes(Paths.get(filePath)));
            StringSelection stringSelection = new StringSelection(str);
            Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
            clpbrd.setContents(stringSelection, null);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to read the execution command file");
        }
    }

    public static String getParentDirectory(String filePath) {
        File file = new File(filePath);
        return file.getAbsoluteFile().getParent();
    }
}
