package com.augminish.app.common.util.file;

import org.apache.commons.io.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileHandler {
    
    private static final String FS = "/";
    
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
    
    public boolean rmdir(String directory) {
        
        File file = new File(directory);
        boolean deleted = false;
        
        try {
            FileUtils.deleteDirectory(file);
            deleted = true;
        } catch (IOException ioe) {
            
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
