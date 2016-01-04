package com.augminish.app.common.util.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileHandler {

    private BufferedWriter bufferedWriter;
    private FileWriter fileWriter;
    private File file;

    public FileHandler() {
    }

    public boolean save(String path, String content) throws IOException {

        boolean saved = true;

        file = new File(path);
        if (!file.exists()) {

            if (!file.createNewFile()) {
                saved = false;
            }
        }

        if (saved) {
            fileWriter = new FileWriter(file);
            bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write(content);

            bufferedWriter.close();
            fileWriter.close();
        }

        return saved;
    }
}
