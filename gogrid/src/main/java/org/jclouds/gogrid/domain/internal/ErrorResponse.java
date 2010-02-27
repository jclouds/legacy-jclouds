package org.jclouds.gogrid.domain.internal;

/**
 * @author Oleksiy Yarmula
 */
public class ErrorResponse {

    private String message;
    private String errorCode;

    /**
     * A no-args constructor is required for deserialization
     */
    public ErrorResponse() {
    }

    public ErrorResponse(String message, String errorCode) {
        this.message = message;
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
