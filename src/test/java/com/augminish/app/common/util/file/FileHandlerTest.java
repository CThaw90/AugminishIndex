package com.augminish.app.common.util.file;

import com.augminish.app.common.util.strings.StaticString;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

public class FileHandlerTest {

    private static FileHandler filehandler;

    @BeforeClass
    public static void initialize() {

        filehandler = new FileHandler();
        Assert.assertTrue("The delete method should clean the testing directory if it exists", filehandler.delete(StaticString.FILE_HANDLER_TEST_FILE_PATH));
    }

    @Test
    public void successfulSaveTest() throws IOException {
        Assert.assertTrue("File should successfully be saved in the proper working directory", 
                filehandler.save(StaticString.FILE_HANDLER_TEST_FILE_PATH, StaticString.FILE_HANDLER_TEST_CONTENT));
    }

    @Test(expected = IOException.class)
    public void unsuccessfulSaveTestWithIOException() throws IOException {
        filehandler.save(StaticString.FILE_HANDLER_TEST_FAKE_PATH, StaticString.FILE_HANDLER_TEST_CONTENT);
    }
}