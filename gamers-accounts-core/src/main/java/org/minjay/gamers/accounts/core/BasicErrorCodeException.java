package org.minjay.gamers.accounts.core;

public class BasicErrorCodeException extends RuntimeException {

    private final int status;
    private final String message;

    public BasicErrorCodeException(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public BasicErrorCodeException(int status) {
        this.status = status;
        this.message = null;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }
}
