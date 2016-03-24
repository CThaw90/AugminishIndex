package com.augminish.app.common.exceptions;

import com.augminish.app.common.exceptions.helper.ExceptionType;

import org.junit.Assert;
import org.junit.Test;

public class ExceptionManagerTest {

    private static final String MALFORMED_URL_SIGNATURE = "java.net.MalformedURLException: no protocol: www.https.//facebook.com/malformed-url-testing";
    private static final String FAILING_HTTP_SIGNATURE = "com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException: 404 Not Found for http://127.0.0.1/404.php";

    @Test
    public void failingHttpCodeStatusExceptionTest() {
        Assert.assertEquals(ExceptionType.HTTP, ExceptionManager.delegate(FAILING_HTTP_SIGNATURE));
    }

    @Test
    public void malformedUtlExceptionTest() {
        Assert.assertEquals(ExceptionType.MALFORMED_URL, ExceptionManager.delegate(MALFORMED_URL_SIGNATURE));
    }
}
