package com.augminish.app.common.util.file;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

public class FileHandlerTest {

    private static final String CONTENT = "<!DOCTYPE><html><head><title>File Contents</title></head><body>File save testing</body></html>";

    private static FileHandler filehandler;

    @BeforeClass
    public static void initialize() {

        filehandler = new FileHandler();
        Assert.assertTrue("The delete method should clean the testing directory if it exists", filehandler.delete("./.ignore/data/www.augminishtest.com/testing_file.html"));
    }

    @Test
    public void successfulSaveAndReadTest() throws IOException {
        Assert.assertTrue("File should successfully be saved in the proper working directory",
                filehandler.save("./.ignore/data/www.augminishtest.com", "testing_file.html", CONTENT));

        Assert.assertEquals("File should successfully be read into string", CONTENT, filehandler.read("./.ignore/data/www.augminishtest.com/testing_file.html"));
    }

    @Test(expected = IOException.class)
    public void unsuccessfulReadTest() throws IOException {
        filehandler.read("./.ignore/data/www.augminishtest.com/unsuccessfulRead.html");
    }
}