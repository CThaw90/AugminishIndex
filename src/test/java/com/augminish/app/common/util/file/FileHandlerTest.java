package com.augminish.app.common.util.file;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

public class FileHandlerTest {

    private static final String content = "<!DOCTYPE><html><head><title>File Contents</title></head><body>File save testing</body></html>";
    private static final String filePath = "./.ignore/data/www.augminishtest.com/testing_file.html";
    private static final String fakePath = "./ignore/data/www.augminishtest.com/testing_file.html";
    private static FileHandler filehandler;

    @BeforeClass
    public static void initialize() {

        filehandler = new FileHandler();
        Assert.assertTrue("The delete method should clean the testing directory if it exists", filehandler.delete(filePath));
    }

    @Test
    public void successfulSaveTest() throws IOException {
        Assert.assertTrue("File should successfully be saved in the proper working directory", filehandler.save(filePath, content));
    }

    @Test(expected = IOException.class)
    public void unsuccessfulSaveTestWithIOException() throws IOException {
        filehandler.save(fakePath, content);
    }
}