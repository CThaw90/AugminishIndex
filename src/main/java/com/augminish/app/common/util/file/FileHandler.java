package com.augminish.app.common.util.file;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileHandler {

    private static final String FS = "/";

    private BufferedReader bufferedReader;
    private FileReader fileReader;

    private BufferedWriter bufferedWriter;
    private FileWriter fileWriter;

    private File file;

    public FileHandler() {
    }

    public boolean save(String path, String fileName, String content) throws IOException {

        boolean saved = true;

        file = new File(path);
        if (!file.exists()) {
            saved = file.mkdirs();
        }

        if (saved) {
            file = new File(path + FS + fileName);
            fileWriter = new FileWriter(file);
            bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write(content);

            bufferedWriter.close();
            fileWriter.close();
        }

        return saved;
    }

    public String read(String fileName) throws IOException {

        StringBuilder content = new StringBuilder();
        String line = new String();

        try {
            file = new File(fileName);
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);

            while ((line = bufferedReader.readLine()) != null) {
                content.append(line);
            }
        }
        finally {
            if (fileReader != null)
                fileReader.close();

            if (bufferedReader != null)
                bufferedReader.close();
        }

        return content.toString();
    }

    public boolean rmdir(String directory) {

        File file = new File(directory);
        boolean deleted = false;

        try {
            FileUtils.deleteDirectory(file);
            deleted = true;
        }
        catch (IOException ioe) {

        }

        return deleted;
    }

    protected boolean delete(String path) {

        boolean deleted = false;

        file = new File(path);
        if (!file.exists()) {
            deleted = true;
        }
        else {
            deleted = file.delete();
        }

        return deleted;
    }
}
