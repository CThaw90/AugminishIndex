package com.augminish.app.common.exceptions;

import com.augminish.app.common.exceptions.helper.ExceptionType;

public class ExceptionManager extends Exception {

    private static final long serialVersionUID = -1451478209907780455L;

    public ExceptionManager(String className, String cause) {

        switch (delegate(cause)) {

            case MALFORMED_URL:
                break;

            case SQL_BUILDER:
                break;

            case SOCKET:
                break;

            case MYSQL:
                break;

            case HTTP:
                break;

            case IO:
                break;

            default:
                break;
        }
    }

    public void log() {

    }

    protected static ExceptionType delegate(String cause) {
        return ExceptionType.getExceptionType(cause);
    }
}
