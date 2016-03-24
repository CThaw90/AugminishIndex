package com.augminish.app.common.exceptions.helper;

public enum ExceptionType {

    HTTP("^com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException:.*"),
    SQL_BUILDER("^com.augminish.app.common.util.mysql.helper.SqlBuilder:.*"),
    MYSQL("^com.augminish.app.common.util.mysql.MySQL:.*"),
    MALFORMED_URL("^java.net.MalformedURLException:.*"),
    GENERIC(""),
    SOCKET(""),
    IO("");

    private String regex;

    ExceptionType(String regex) {
        this.regex = regex;
    }

    public static ExceptionType getExceptionType(String cause) {
        for (ExceptionType exception : ExceptionType.values()) {
            if (exception.matches(cause)) {
                return exception;
            }
        }

        return GENERIC;
    }

    protected boolean matches(String cause) {
        return cause.matches(this.regex);
    }
}
